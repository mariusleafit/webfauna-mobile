package ch.leafit.webfauna.Utils;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.config.Config;

/**
 * Created by marius on 15/07/14.
 *
 * tracks location changes & returns the current coordinates
 */
public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    private boolean mIsGPSEnabled = false;
    private boolean mIsNetworkEnabled = false;
    private boolean mCanGetLocation = false;

    private Location mLocation;

    protected LocationManager mLocationManager;

    public GPSTracker(Context context) {
        mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            mLocationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // getting GPS status
            mIsGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            mIsNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!mIsGPSEnabled && !mIsNetworkEnabled) {
                // no network provider is enabled
            } else {
                mCanGetLocation = true;
                // First get location from Network Provider
                if (mIsNetworkEnabled) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                            Config.MIN_TIME_BW_UPDATES,
                            Config.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (mLocationManager != null) {
                        mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (mIsGPSEnabled) {
                    if (mLocation == null) {
                        mLocationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                Config.MIN_TIME_BW_UPDATES,
                                Config.MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (mLocationManager != null) {
                            mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLocation;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(mLocationManager != null){
            mLocationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to get latitude
     * */
    public double getLatitude(){
        double latitude = -1;
        if(mLocation != null){
            latitude = mLocation.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     * */
    public double getLongitude(){
        double longitude = -1;
        if(mLocation != null){
            longitude = mLocation.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     *
     * @return x coordinates in CH03-coordinate system
     */
    public double getCHx() {
        double chX = -1;

        double lat = getLatitude();
        double lng = getLongitude();

        if(lat != -1 && lng != -1) {
            chX = GeoMath.getCHx(lat, lng);
        }

        return chX;
    }

    /**
     *
     * @return y coordinates in CH03-coordinate system
     */
    public double getCHy() {
        double chY = -1;

        double lat = getLatitude();
        double lng = getLongitude();

        if(lat != -1 && lng != -1) {
            chY = GeoMath.getCHy(lat, lng);
        }

        return chY;
    }

    /**
     * Function to check GPS/wifi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        return mCanGetLocation;
    }

    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
   public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        Resources res = mContext.getResources();

        alertDialog.setTitle(res.getString(R.string.gps_disabled_dialog_title));
        alertDialog.setMessage(res.getString(R.string.gps_disabled_dialog_message));

        // On pressing Settings button
        alertDialog.setPositiveButton(res.getText(R.string.settings), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
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

    /*LocationListener*/
    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /*Service*/
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
