package ch.leafit.webfauna.models;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaRealmValue extends WebfaunaBaseModel{

    private String mRestID;
    private String mDesignation;
    private String mLanguageCode;

    public WebfaunaRealmValue(JSONObject jsonObject) {
        super(jsonObject);
        putJSON(jsonObject);
    }

    public String getRestID() {
        return mRestID;
    }

    public String getDesignation() {
        return mDesignation;
    }

    public String getLanguageCode() {
        return mLanguageCode;
    }


    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            mRestID = jsonObject.getString("REST-ID");
            mDesignation = jsonObject.getString("designation");
            mLanguageCode = jsonObject.getString("languageCode");
        } catch (JSONException e) {
            Log.e("WebfaunaRealm - putJSON: ", "JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("REST-ID", mRestID);
            jsonObject.put("designation",mDesignation);
            jsonObject.put("languageCode",mLanguageCode);
        } catch (JSONException e) {
            Log.e("WebfaunaRealm - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }
}
