package ch.leafit.webfauna.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ch.leafit.om.cache.MapSection;
import ch.leafit.om.cache.OfflineMapManager;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaObservationFile;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by marius on 29/07/14.
 */
public class OfflineMapManagementFragment extends BaseFragment implements OfflineMapManager.Subscriber{
    public static final String TAG = "OfflineMapManagementFragment";

    private static final double CH09_SOUTH_WEST_LAT = 45.8300;
    private static final double CH09_SOUTH_WEST_LNG = 5.9700;
    private static final double CH09_NORTH_EAST_LAT = 47.8100;
    private static final double CH09_NORTH_EAST_LNG = 10.4900;

    private static final PolygonOptions sCH09BoundaryPolygonOptions;
    private static final LatLng sCH09SouthWest;
    private static final LatLng sCH09NorthEast;
    private static final LatLngBounds sCH09Bounds;
    static {
        sCH09SouthWest = new LatLng(CH09_SOUTH_WEST_LAT,CH09_SOUTH_WEST_LNG);
        sCH09NorthEast = new LatLng(CH09_NORTH_EAST_LAT,CH09_NORTH_EAST_LNG);
        sCH09Bounds = new LatLngBounds(sCH09SouthWest,sCH09NorthEast);

        //lat = north-south/top-bottom
        //lng = east-west/right-left
        sCH09BoundaryPolygonOptions = new PolygonOptions()
                .add(sCH09SouthWest) //top-left
                .add(new LatLng(sCH09SouthWest.latitude,sCH09NorthEast.longitude))//top-right
                .add(sCH09NorthEast)//bottom-right
                .add(new LatLng(sCH09NorthEast.latitude,sCH09SouthWest.longitude));//bottom-left

    }

    private GoogleMap mMap;

    private Polygon mCachingRectangle;

    protected ProgressDialog mProgressDialog;

    private int mRemainingTilesToDownload;

    /**
     * contains the polygon, which mark the cached MapSection (key: name of the section)
     */
    private HashMap<String,Polygon> mSectionPolygons;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.offline_map_management_fragment, null);

        //subscribe for offlinemapManager changes
        OfflineMapManager.getInstance().subscribe(this);

        /*add map*/
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_HYBRID)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .zoomControlsEnabled(true)
                .zoomGesturesEnabled(true);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);

        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.map_frame, mapFragment,"map").commit();

        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setupMapIfNeeded();
    }

    public void onPause() {
        super.onPause();
        OfflineMapManager.getInstance().writeCache();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.offline_map_management_fragment_actionbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_add:
                addMapSegment();
                return true;
            case R.id.action_remove:
                SectionManagementDialog dialog = new SectionManagementDialog();
                dialog.show(getChildFragmentManager(),"");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addMapSegment() {
        if(OfflineMapManager.getInstance().getCachedMapSections().size() < Config.NUMBER_OF_SQUARES_CACHEABLE_AT_SAME_TIME) {

            SectionNameDialog dialog = new SectionNameDialog();
            dialog.show(getChildFragmentManager(),"");

        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

            Resources res = getResources();

            alertDialog.setMessage(res.getString(R.string.offline_map_to_much_squares));

            // On pressing Settings button
            alertDialog.setPositiveButton(res.getText(android.R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }

    private void clearOfflineMap() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        Resources res = getResources();

        alertDialog.setMessage(res.getString(R.string.clear_offline_map_sections_message));

        // On pressing Settings button
        alertDialog.setPositiveButton(res.getText(android.R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                OfflineMapManager.getInstance().clearCache();
                for(Polygon p: mSectionPolygons.values())
                    p.remove();

            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(res.getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }


    private void setupMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(ch.leafit.om.R.id.map_frame)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                //move camera position
                CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(46.951083, 7.438639));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(14);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);

                mMap.setMyLocationEnabled(true);

                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        //check bounds
                        //lat = north-south/top-bottom
                        //lng = east-west/right-left
                        if(!sCH09Bounds.contains(cameraPosition.target)) {
                            double moveToLat = cameraPosition.target.latitude;
                            double moveToLng = cameraPosition.target.longitude;

                            //does the longitude exceed the bounds on the "right"?
                            if(cameraPosition.target.longitude > sCH09NorthEast.longitude)
                                moveToLng = sCH09NorthEast.longitude;
                            //does the longitude exceed the bounds on the "left"?
                            if(cameraPosition.target.longitude < sCH09SouthWest.longitude)
                                moveToLng = sCH09SouthWest.longitude;

                            //does the latitude exceed the bounds on the "top"?
                            if(cameraPosition.target.latitude > sCH09NorthEast.latitude)
                                moveToLat = sCH09NorthEast.latitude;
                            //does the latitude exceed the bounds on the "bottom"?
                            if(cameraPosition.target.latitude < sCH09SouthWest.latitude)
                                moveToLat = sCH09SouthWest.latitude;

                            mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(moveToLat,moveToLng)));

                        }


                        //draw caching-rectangle
                        if(mCachingRectangle == null) {
                            mCachingRectangle = mMap.addPolygon(new PolygonOptions().addAll(calculatePointsOfCachingRectangle(cameraPosition)));
                            mCachingRectangle.setStrokeColor(Color.BLACK);
                        }
                        else
                            mCachingRectangle.setPoints(calculatePointsOfCachingRectangle(cameraPosition));
                    }
                });

                //paint swiss boundary rectangle
                mMap.addPolygon(sCH09BoundaryPolygonOptions);


                mSectionPolygons = new HashMap<String, Polygon>();

                List<MapSection> mapSections = OfflineMapManager.getInstance().getCachedMapSections();
                for(MapSection mapSection: mapSections) {
                    Polygon p = mMap.addPolygon(mapSection.getPolygon());

                    mSectionPolygons.put(mapSection.getName(),p);



                }

            }
        }
    }
    /*OfflineMapManager.Subscriber*/
    public void finishedMapSectionCachingSuccessfully() {
        hideProgressDialog();
        mRemainingTilesToDownload = 0;
    }

    public void finishedMapSectionCachingWithErrors() {
        hideProgressDialog();
        mRemainingTilesToDownload = 0;

        //show retry dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        Resources res = getResources();

        alertDialog.setMessage(res.getString(R.string.offline_map_retry_dialog_message));

        // On pressing Settings button
        alertDialog.setPositiveButton(res.getText(android.R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                OfflineMapManager.getInstance().retryFailedTileDownloads();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton(res.getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void tilesDownloadProgressNewTilesDownloaded(int numberOfNewlyDownloadedTiles) {

            mRemainingTilesToDownload -= numberOfNewlyDownloadedTiles;
            getActivity().runOnUiThread(changeMessage);

    }

    private Runnable changeMessage = new Runnable() {
        @Override
        public void run() {
            //Log.v(TAG, strCharacters);
            if(mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.setMessage(getResources().getString(R.string.offline_map_progress_message) + mRemainingTilesToDownload);
            }
        }
    };

    /*helper methods*/

    protected void showProgressDialog(String title,String message) {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressDialog.show(getActivity(), title, message, true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     *
     * @param map
     * @return denominator of scale
     */
    private double calculateScaleOfMap(GoogleMap map, Resources res) {
        long denominator;

        /*calculate width on screen ( 100px)*/
        float widthOnScreen = pxToMM(100,getResources().getDisplayMetrics());


        /*calculate real Width*/
        Projection projection = mMap.getProjection();

        LatLng position1 = projection.fromScreenLocation(new Point(0, 0));
        LatLng position2 = projection.fromScreenLocation(new Point(100, 0));
        float[] distanceResults = new float[3];
        //get distance between position1 & position2 in meters
        Location.distanceBetween(position1.latitude, position1.longitude, position2.latitude, position2.longitude, distanceResults);
        float realDistance = distanceResults[0];

        //calculate denominator
        denominator = Math.round((realDistance * 1000) / widthOnScreen);

        return denominator;
    }

    private float pxToMM(int px, DisplayMetrics metrics) {
        return ((px * 1.0f) / (metrics.densityDpi * 1.0f)) * 25.4f;
    }

    private float mmToPx(float mm, DisplayMetrics metrics) {
        return (mm/25.4f) * metrics.densityDpi * 1.0f;
    }


    private List<LatLng> calculatePointsOfCachingRectangle(CameraPosition cameraPosition) {
        ArrayList<LatLng> points = new ArrayList<LatLng>();

        LatLng center = cameraPosition.target;

        //calculate cachingRectangle dimensions
        double scale = calculateScaleOfMap(mMap,getResources());
        float cachingRectangleWithOnScreenInMM = (float)(Config.CACHING_RECTANGLE_WIDTH_M / scale) * 1000;
        int cachingRectangleWithOnScreenInPX = Math.round(mmToPx(cachingRectangleWithOnScreenInMM, getResources().getDisplayMetrics()));

        Projection projection = mMap.getProjection();

        //calculate corner screenPoints of cachingRectangle
        Point centerScreenPoint = projection.toScreenLocation(center);

        Point topLeft = new Point(centerScreenPoint.x - cachingRectangleWithOnScreenInPX / 2, centerScreenPoint.y + cachingRectangleWithOnScreenInPX / 2);
        Point topRight = new Point(centerScreenPoint.x + cachingRectangleWithOnScreenInPX / 2,centerScreenPoint.y + cachingRectangleWithOnScreenInPX / 2);
        Point bottomRight = new Point(centerScreenPoint.x + cachingRectangleWithOnScreenInPX / 2, centerScreenPoint.y - cachingRectangleWithOnScreenInPX / 2);
        Point bottomLeft = new Point(centerScreenPoint.x - cachingRectangleWithOnScreenInPX / 2, centerScreenPoint.y - cachingRectangleWithOnScreenInPX / 2);


        //calculate LatLngs
        points.add(projection.fromScreenLocation(topLeft));
        points.add(projection.fromScreenLocation(topRight));
        points.add(projection.fromScreenLocation(bottomRight));
        points.add(projection.fromScreenLocation(bottomLeft));

        return points;
    }

    /**
     * dialog which is shown when the user wants to add a segment
     */
    private class SectionNameDialog extends DialogFragment {

        private EditText txtSectionName;
        private Button btnAdd;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View contentView = getActivity().getLayoutInflater().inflate(R.layout.offline_map_add_section_dialog, null);

            txtSectionName = (EditText)contentView.findViewById(R.id.txtSectionName);
            btnAdd = (Button)contentView.findViewById(R.id.btnAdd);

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txtSectionName.getText() != null && !txtSectionName.getText().toString().equals("")) {

                        //save polygon
                        PolygonOptions polygonToCache = new PolygonOptions();
                        for(LatLng point : mCachingRectangle.getPoints())
                            polygonToCache.add(point);

                        polygonToCache.fillColor(Color.argb(120,0,255,0));
                        polygonToCache.strokeWidth(0);
                        Polygon polygon = mMap.addPolygon(polygonToCache);
                        MapSection newMapSection = new MapSection(polygonToCache,txtSectionName.getText().toString(), ch.leafit.om.config.Config.CACHED_ZOOM_LEVELS);
                        OfflineMapManager.getInstance().addMapSectionToCache(newMapSection);

                        mSectionPolygons.put(txtSectionName.getText().toString(),polygon);

                        mRemainingTilesToDownload = newMapSection.estimateNumberOfNeededTiles();

                        showProgressDialog(getResources().getString(R.string.offline_map_progress_title),getResources().getString(R.string.offline_map_progress_message) + mRemainingTilesToDownload);
                        SectionNameDialog.this.dismiss();
                    }
                }
            });

            builder.setView(contentView);
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }


    /**
     * allows the user to delete sections
     */
    private class SectionManagementDialog extends DialogFragment {
        private ImageButton mBtnRemoveAll;
        private ListView mLstMain;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            View contentView = getActivity().getLayoutInflater().inflate(R.layout.offline_map_remove_section_dialog,null);

            mBtnRemoveAll = (ImageButton)contentView.findViewById(R.id.btnRemoveAll);
            mLstMain = (ListView)contentView.findViewById(R.id.lstMain);

            mBtnRemoveAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearOfflineMap();
                }
            });

            //setup list

            //get MapSections from OfflineMapManagementFragment
            MapSectionListAdapter listAdapter = new MapSectionListAdapter(getActivity(),R.layout.offline_map_remove_section_list_item,
                    (ArrayList<MapSection>)OfflineMapManager.getInstance().getCachedMapSections(),new MapSectionListAdapterCallback() {
                @Override
                public void mapSectionRemoved(String name) {
                    //remove the mapSection-polygon from the map
                    OfflineMapManagementFragment self = OfflineMapManagementFragment.this;

                    if(self.mSectionPolygons.containsKey(name) && self.mSectionPolygons.get(name) != null) {
                        self.mSectionPolygons.get(name).remove();

                        self.mSectionPolygons.remove(name);
                    }
                }
            });

            mLstMain.setAdapter(listAdapter);

            builder.setView(contentView);
            return builder.create();
        }
    }

    /**
     * used to display the MapSections in the list
     */
    private static class MapSectionListAdapter extends ArrayAdapter<MapSection> {

        ArrayList<MapSection> mListItems;

        private MapSectionListAdapterCallback mCallback;

        public MapSectionListAdapter(Context context, int resourceId, ArrayList<MapSection> listItems, MapSectionListAdapterCallback callback) {
            super(context,resourceId,listItems);
            mListItems = listItems;

            mCallback = callback;
        }

        private class ViewHolder {
            TextView txtName;
            ImageButton btnDelete;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.offline_map_remove_section_list_item, null);

                holder = new ViewHolder();
                holder.txtName = (TextView) convertView.findViewById(R.id.txtName);
                holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btnDelete);
                convertView.setTag(holder);

                holder.btnDelete.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        ImageButton btn = (ImageButton) v ;
                        MapSection mapSection = (MapSection) btn.getTag();

                        OfflineMapManager.getInstance().deleteMapSection(mapSection.getName());

                        //update list
                        ArrayList<MapSection> mapSections = (ArrayList<MapSection>)OfflineMapManager.getInstance().getCachedMapSections();

                        MapSectionListAdapter.this.mListItems = mapSections;
                        MapSectionListAdapter.this.notifyDataSetChanged();

                        mCallback.mapSectionRemoved(mapSection.getName());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(mListItems.size() > position) {
                MapSection mapSection = mListItems.get(position);

                holder.txtName.setText(mapSection.getName());
                holder.btnDelete.setTag(mapSection);
            }

            return convertView;

        }
    }

    public interface MapSectionListAdapterCallback {
        public void mapSectionRemoved(String name);
    }
}
