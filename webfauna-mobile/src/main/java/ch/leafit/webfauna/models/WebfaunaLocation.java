package ch.leafit.webfauna.models;

import android.util.Log;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaLocation extends WebfaunaBaseModel {

    /*constant for switzerland*/
    private String mCountryCode = "SZ";

    private WebfaunaRealmValue mPrecision;
    private String mLieudit;
    private double mSwissCoordinatesX;
    private double mSwissCoordinatesY;
    private Integer mAltitude;

    public WebfaunaLocation(JSONObject jsonObject) {
        super(jsonObject);
        putJSON(jsonObject);
    }

    public String getCountryCode() {
        return mCountryCode;
    }
    public WebfaunaRealmValue getPrecision() {
        return mPrecision;
    }
    public String getLieudit() {
        return mLieudit;
    }
    public double getSwissCoordinatesX() {
        return mSwissCoordinatesX;
    }
    public double getSwissCoordinatesY() {
        return mSwissCoordinatesY;
    }
    public Integer getAltitude() {
        return mAltitude;
    }

    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            /*get Precision from datadispatcher*/
            mCountryCode = jsonObject.getString("countryCode");
            mPrecision = DataDispatcher.getInstantce().getPrecisionRealm().getRealmValue(jsonObject.getString("precisionCode"));
            mLieudit = jsonObject.getString("lieudit");
            mSwissCoordinatesX = jsonObject.getDouble("swissCoordinatesX");
            mSwissCoordinatesY = jsonObject.getDouble("swissCoordinatesY");
            mAltitude = jsonObject.getInt("altitude");
        } catch (JSONException e) {
            Log.e("WebfaunaRealm - putJSON: ", "JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("countryCode", mCountryCode);
            jsonObject.put("precisionCode", mPrecision.getRestID());
            jsonObject.put("lieudit", mLieudit);
            jsonObject.put("swissCoordinatesX", mSwissCoordinatesX);
            jsonObject.put("swissCoordinatesY", mSwissCoordinatesY);
            jsonObject.put("altitude", mAltitude);
        } catch (JSONException e) {
            Log.e("WebfaunaRealm - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }
}
