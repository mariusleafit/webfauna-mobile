package ch.leafit.webfauna.models;

import android.content.res.Resources;
import android.util.Log;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaLocation extends WebfaunaBaseModel implements WebfaunaValidatable {

    /*constant for switzerland*/
    private String mCountryCode = "SZ";

    private WebfaunaRealmValue mPrecision;
    private String mLieudit;
    private double mSwissCoordinatesX;
    private double mSwissCoordinatesY;

    private double mWGSCoordinatesLat;
    private double mWGSCoordinatesLng;

    private Integer mAltitude;

    public WebfaunaLocation(){}

    public WebfaunaLocation(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        putJSON(jsonObject);
    }

    public WebfaunaLocation(WebfaunaLocation toCopy) {
        if(toCopy == null)
            toCopy = new WebfaunaLocation();
        mCountryCode = toCopy.mCountryCode;
        mPrecision = new WebfaunaRealmValue(toCopy.mPrecision);
        mLieudit = toCopy.mLieudit;
        mSwissCoordinatesX = toCopy.mSwissCoordinatesX;
        mSwissCoordinatesY = toCopy.mSwissCoordinatesY;
        mWGSCoordinatesLat = toCopy.mWGSCoordinatesLat;
        mWGSCoordinatesLng = toCopy.mWGSCoordinatesLng;
        mAltitude = toCopy.mAltitude;
    }

    public String getCountryCode() {
        return mCountryCode;
    }

    public WebfaunaRealmValue getPrecision() {
        return mPrecision;
    }
    public void setPrecision(WebfaunaRealmValue precision) {
        mPrecision = precision;
    }

    public String getLieudit() {
        return mLieudit;
    }
    public void setLieudit(String lieudit) {
        mLieudit = lieudit;
    }

    public double getSwissCoordinatesX() {
        return mSwissCoordinatesX;
    }
    public void setSwissCoordinatesX(double x) {
        mSwissCoordinatesX = x;
    }

    public double getSwissCoordinatesY() {
        return mSwissCoordinatesY;
    }
    public void setSwissCoordinatesY(double y) {
        mSwissCoordinatesY = y;
    }

    public double getWGSCoordinatesLat() {
        return mWGSCoordinatesLat;
    }
    public void setWGSCoordinatesLat(double lat) {
        mWGSCoordinatesLat = lat;
    }

    public double getWGSCoordinatesLng() {
        return mWGSCoordinatesLng;
    }
    public void setWGSCoordinatesLng(double lng) {
        mWGSCoordinatesLng = lng;
    }

    public Integer getAltitude() {
        return mAltitude;
    }
    public void setAltitude(Integer altitude) {
        mAltitude = altitude;
    }

    @Override
    public void putJSON(JSONObject jsonObject) throws Exception{
        try {
            /*get Precision from datadispatcher*/
            mCountryCode = jsonObject.getString("countryCode");
            mPrecision = DataDispatcher.getInstantce().getPrecisionRealm().getRealmValue(jsonObject.getString("precisionCode"));
            mLieudit = jsonObject.getString("lieudit");
            mSwissCoordinatesX = jsonObject.getDouble("swissCoordinatesX");
            mSwissCoordinatesY = jsonObject.getDouble("swissCoordinatesY");

            mWGSCoordinatesLat = jsonObject.getDouble("wgsCoordinatesLat");
            mWGSCoordinatesLng = jsonObject.getDouble("wgsCoordinatesLng");

            mAltitude = jsonObject.getInt("altitude");
        }  catch (JSONException e) {
            Log.e("Location - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("WebfaunaRealm - putJSON: ", "JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON() throws Exception{
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("countryCode", mCountryCode);
            jsonObject.put("precisionCode", mPrecision.getRestID());
            jsonObject.put("lieudit", mLieudit);
            jsonObject.put("swissCoordinatesX", mSwissCoordinatesX);
            jsonObject.put("swissCoordinatesY", mSwissCoordinatesY);
            jsonObject.put("wgsCoordinatesLat",mWGSCoordinatesLat);
            jsonObject.put("wgsCoordinatesLng",mWGSCoordinatesLng);
            jsonObject.put("altitude", mAltitude);
        } catch (Exception e) {
            Log.e("WebfaunaRealm - toJSON: ", "JSON", e);
            throw e;
        }

        return jsonObject;
    }

    /*
    WebfaunaValidatable
     */

    @Override
    public WebfaunaValidationResult getValidationResult(Resources res) {
        if(res != null) {
            WebfaunaValidationResult validationResult = new WebfaunaValidationResult(true, "");

            String validationMessage = "";

            if(mPrecision == null) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.location_validation_precision_null);
            }

            if(mLieudit == null || mLieudit.equals("")) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.location_validation_lieudit_null);
            } else if(mLieudit.length() > 60) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.location_validation_lieudit_tolong);
            }

            if(mAltitude != null && (mAltitude > 5000 || mAltitude < 0)) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.location_validation_altitude_invalid);
            }

            if(mSwissCoordinatesY == -1 || mSwissCoordinatesX == -1 || mSwissCoordinatesX <= mSwissCoordinatesY) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.location_validation_coordinates_null);
            }

           validationResult.setValidationMessage(validationMessage);

            return validationResult;
        } else {
            return null;
        }
    }
}
