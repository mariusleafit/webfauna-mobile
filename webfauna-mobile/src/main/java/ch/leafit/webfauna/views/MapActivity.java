package ch.leafit.webfauna.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.*;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import ch.leafit.om.cache.MapSection;
import ch.leafit.om.cache.OfflineMapManager;
import ch.leafit.om.tile_providers.CachedTilesProvider;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.Utils.NetworkManager;
import ch.leafit.webfauna.config.Config;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by marius on 29/07/14.
 */
public class MapActivity extends FragmentActivity implements  NetworkManager.NetworkManagerCallback{

    public static final String BUNDLE_LAT_LNG_KEY = "latlng";

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

    private SupportMapFragment mMapFragment;
    private GoogleMap mMap;

    private LatLng mDefaultPosition;
    private Marker mSelectedPositionMarker;

    private NetworkManager mNetworkManager;

    private TileOverlay mCachedTilesOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.map_activity);



        /*add mapFragment*/
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NONE)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .zoomControlsEnabled(false)
                .zoomGesturesEnabled(false);

        mMapFragment = SupportMapFragment.newInstance();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(ch.leafit.om.R.id.map_frame, mMapFragment,"map").commit();

        //check if default-position has been sent
        Intent intent = getIntent();
        if(intent != null && intent.hasExtra(BUNDLE_LAT_LNG_KEY) && intent.getParcelableExtra(BUNDLE_LAT_LNG_KEY) instanceof LatLng)
            mDefaultPosition = (LatLng)intent.getParcelableExtra(BUNDLE_LAT_LNG_KEY);

        //instanciate NetworkManager
        mNetworkManager = new NetworkManager(this,this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        /*mapFragment configuration*/
        setUpMapIfNeeded();

        /*register network receiver*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkManager, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkManager);
    }

    @Override
    public void onBackPressed() {
        returnSelectedPosition();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_activity_actionbar_menu,menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ok:
                returnSelectedPosition();
                return true;
            case R.id.action_show_dialog:
                SectionSelectDialog dialog = new SectionSelectDialog();
                dialog.show(getSupportFragmentManager(),"");
                return true;
            default:
                return super.onMenuItemSelected(featureId,item);
        }
    }

    private void returnSelectedPosition() {
        /*return selected position*/
        Intent result = new Intent();
        if(mSelectedPositionMarker != null && mSelectedPositionMarker.getPosition() != null) {
            result.putExtra(BUNDLE_LAT_LNG_KEY, mSelectedPositionMarker.getPosition());
        }
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    private void showSelectedPositionMarker(LatLng position) {
        if(position != null) {

            if(mSelectedPositionMarker != null)
                mSelectedPositionMarker.remove();

            MarkerOptions marker = new MarkerOptions().position(position).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            mSelectedPositionMarker = mMap.addMarker(marker);
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(ch.leafit.om.R.id.map_frame)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setPadding(0,0,0,20);

                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);

                mMap.setMyLocationEnabled(true);


                mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new CachedTilesProvider()));

                //move camera position
                CameraUpdate center = null;
                if(mDefaultPosition != null)
                    center = CameraUpdateFactory.newLatLng(mDefaultPosition);
                else
                    center = CameraUpdateFactory.newLatLng(new LatLng(46.951083, 7.438639));
                CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);


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
                    }
                });

                /*mark selected position*/
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        showSelectedPositionMarker(latLng);
                    }
                });

                //paint swiss boundary rectangle
                mMap.addPolygon(sCH09BoundaryPolygonOptions);

                /*
                add cached map section-polygons
                 */
                List<MapSection> mapSections = OfflineMapManager.getInstance().getCachedMapSections();
                for(MapSection mapSection: mapSections) {
                    mMap.addPolygon(mapSection.getPolygon());
                }

                /*
                show default position if necessary
                 */
                if(mDefaultPosition != null)
                    showSelectedPositionMarker(mDefaultPosition);

            }
        }
    }

     /* NetworkManager.NetworkManagerCallback*/

    @Override
    public void networkConnectionStatusChanged(boolean isConnected) {
        if(isConnected) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

            //remove cachedTilesProvider
            if(mCachedTilesOverlay != null) {
                mCachedTilesOverlay.remove();
                mCachedTilesOverlay = null;
            }

            //unlock zoomLevel
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
        } else {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);

            //add cachedTilesProvider
            if(mCachedTilesOverlay == null) {
                mCachedTilesOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new CachedTilesProvider()));
            }


            //lock zoomLevel
            mMap.animateCamera(CameraUpdateFactory.zoomTo(Config.LOCKED_ZOOMLEVEL_IN_OFFLINE_MODE));
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
        }
    }

    /**
     * allows to select on which section the map should be zoomed
     */
    private class SectionSelectDialog extends DialogFragment {
        private ListView mLstMain;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            mLstMain = new ListView(getActivity());

            //setup list

            //get MapSections from OfflineMapManagementFragment
            MapSectionListAdapter listAdapter = new MapSectionListAdapter(getActivity(),android.R.layout.simple_list_item_1,
                    (ArrayList<MapSection>)OfflineMapManager.getInstance().getCachedMapSections(),new MapSectionListAdapter.MapSectionListAdapterCallback() {
                @Override
                public void mapSectionSelected(MapSection mapSection) {

                    //zoom to mapSection
                    CameraUpdate center = CameraUpdateFactory.newLatLng(mapSection.getPolygon().getPoints().get(0));
                    MapActivity.this.mMap.animateCamera(center);
                    SectionSelectDialog.this.dismiss();
                }
            });

            mLstMain.setAdapter(listAdapter);

            builder.setView(mLstMain);
            return builder.create();
        }
    }

    private static class MapSectionListAdapter extends ArrayAdapter<MapSection> {

        ArrayList<MapSection> mListItems;

        private MapSectionListAdapterCallback mCallback;

        public MapSectionListAdapter(Context context, int resourceId, ArrayList<MapSection> listItems, MapSectionListAdapterCallback callback) {
            super(context,resourceId,listItems);
            mListItems = listItems;

            mCallback = callback;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MapSection mapSection = (MapSection)v.getTag();
                        mCallback.mapSectionSelected(mapSection);

                    }
                });
            }
            if(mListItems.size() > position) {
                MapSection mapSection = mListItems.get(position);

                TextView txtName = (TextView)convertView.findViewById(android.R.id.text1);
                txtName.setText(mapSection.getName());
                convertView.setTag(mapSection);
            }

            return convertView;

        }

        public interface MapSectionListAdapterCallback {
            public void mapSectionSelected(MapSection mapSection);
        }
    }
}
