package ch.leafit.webfauna.gdc;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import ch.leafit.gdc.GDCDataField;
import ch.leafit.gdc.GDCDefaultStyleConfig;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.Utils.GPSTracker;
import ch.leafit.webfauna.Utils.GeoMath;
import ch.leafit.webfauna.gdc.styles.GDCCoordinatesDataFieldDefaultStyle;

/**
 * Created by marius on 12/07/14.
 */
public class GDCCoordinatesDataField extends GDCDataField{

    String mFieldName;

    protected double mCHx;
    protected double mCHy;
    protected double mLat;
    protected double mLng;

    Callback mCallback;

    protected GPSTracker mGPSTracker;

    /*UI-Elements*/
    public TextView mLblFieldName;
    public TextView mLblCH03Title;
    public TextView mLblCH03;
    public TextView mLblWGSTitle;
    public TextView mLblWGS;
    public ImageView mBtnReload;

    public GDCCoordinatesDataField(Activity activity, int tag, String fieldName,Callback callback) {
        this(activity,tag,fieldName,callback,-1,-1,-1,-1);
    }

    public GDCCoordinatesDataField(Activity activity, int tag, String fieldName,Callback callback,  double chX, double chY) {
        super(activity,tag);
        mFieldName = fieldName;
        mCallback = callback;

        setCHcoordinates(chX,chY);

    }

    public GDCCoordinatesDataField(Activity activity, int tag, String fieldName,Callback callback,  double chX, double chY, double lat, double lng) {
        super(activity,tag);
        mFieldName = fieldName;
        mCallback = callback;

        setCoordinates(chX,chY,lat,lng);
    }

    @Override
    public View getView() {
        if(mView == null) {
            mView = mInflater.inflate(R.layout.gdc_coordinates_field, null);

            mLblFieldName = (TextView)mView.findViewById(R.id.lblFieldName);
            mLblCH03Title = (TextView)mView.findViewById(R.id.lblCH03Title);
            mLblCH03 = (TextView)mView.findViewById(R.id.lblCH03);
            mLblWGSTitle = (TextView)mView.findViewById(R.id.lblWGSTitle);
            mLblWGS = (TextView)mView.findViewById(R.id.lblWGS);
            mBtnReload = (ImageView)mView.findViewById(R.id.btnReload);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCallback.coordinatesDataFieldClicked();
                }
            });



            mBtnReload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*get location*/
                    mGPSTracker = new GPSTracker(mActivity);
                    if(mGPSTracker.canGetLocation()) {
                        double chX = mGPSTracker.getCHx();
                        double chY = mGPSTracker.getCHy();
                        double lat = mGPSTracker.getLatitude() ;
                        double lng = mGPSTracker.getLongitude() ;



                        setCoordinates(chX,chY,lat,lng);
                        mCallback.coordinatesDataFieldChanged(chX,chY,lat,lng);
                    } else {
                        mGPSTracker.showSettingsAlert();
                    }
                }
            });

            this.setFieldName(mFieldName);
            this.setCoordinates(mCHx, mCHy, mLat, mLng);

            this.applyStyle();
        }
        return mView;
    }

    @Override
    protected void applyStyle() {
        if(mView != null) {
            if(mStyle == null) {
                mStyle = new GDCCoordinatesDataFieldDefaultStyle();
            }
            mStyle.applyStyleToField(this);
        }
    }


    public double getCHx() {
        return mCHx;
    }
    public double getCHy() {
        return mCHy;
    }
    public void setCHcoordinates(double chX, double chY) {
        if(chX != -1 && chY != -1) {
            double lat = GeoMath.getLat(chX,chY);
            double lng = GeoMath.getLng(chX,chY);

            setCoordinates(chX,chY,lat,lng);
        }
    }

    public double getLat() {
        return mLat;
    }
    public double getLng() {
        return mLng;
    }
    public void setWGScoordinates(double lat, double lng) {
        if(lat != -1 && lng != -1) {
            double chX = GeoMath.getCHx(lat, lng);
            double chY = GeoMath.getCHy(lat, lng);

            setCoordinates(chX,chY,lat,lng);
        }
    }

    public void setCoordinates(double chX, double chY, double lat, double lng) {
        chX = Math.round(chX);
        chY = Math.round(chY);
        lat = Math.round(lat * 100)/100.0;
        lng = Math.round(lng * 100)/100.0;

        if(mLblCH03 != null && chX != -1 && chY != -1) {
            mLblCH03.setText(chX + " - " + chY);
        }
        mCHx = chX;
        mCHy = chY;

        if(mLblWGS != null && lat != -1 && lng != -1) {
            mLblWGS.setText(lat + " - " + lng);
        }
        mLat = lat;
        mLng = lng;
    }


    public String getFieldName() {
        return mFieldName;
    }

    public void setFieldName(String fieldName) {
        if(mLblFieldName != null) {
            mLblFieldName.setText(fieldName);
        }
        mFieldName = fieldName;
    }

    public static interface Callback {
        public void coordinatesDataFieldChanged(double chX, double chY, double lat, double lng);
        public void coordinatesDataFieldClicked();
    }

    @Override
    protected void finalize() throws Throwable {
        try{
            /*stop gps tracker*/
            mGPSTracker.stopUsingGPS();

        }catch(Throwable t){
            throw t;
        }finally{
            super.finalize();
        }

    }
}
