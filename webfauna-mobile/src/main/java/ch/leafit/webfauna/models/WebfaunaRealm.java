package ch.leafit.webfauna.models;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaRealm extends WebfaunaBaseModel {

    private String mRestID;
    private String mDesignation;
    private String mDefaultLanguage;

    /*not from json*/
    private ArrayList<WebfaunaRealmValue> mRealmValues;

    public WebfaunaRealm(JSONObject jsonObject) {
        super(jsonObject);
        putJSON(jsonObject);
    }

    public String getRestID() {
        return mRestID;
    }

    public String getDesignation() {
        return mDesignation;
    }

    public String getDefaultLanguage() {
        return mDefaultLanguage;
    }

    public ArrayList<WebfaunaRealmValue> getRealmValues() {
        return mRealmValues;
    }

    public void setRealmValues(ArrayList<WebfaunaRealmValue> realmValues) {
        mRealmValues = realmValues;
    }

    public WebfaunaRealmValue getRealmValue(String restID) {
        WebfaunaRealmValue returnRealmValue = null;
        if(restID != null && restID != "" && mRealmValues != null) {
            for(WebfaunaRealmValue realmValue : mRealmValues) {
                if(realmValue.getRestID() == restID) {
                    returnRealmValue = realmValue;
                    break;
                }
            }
        }
        return returnRealmValue;
    }

    public void addRealmValue(WebfaunaRealmValue realmValue) {
        if(mRealmValues == null) {
            mRealmValues = new ArrayList<WebfaunaRealmValue>();
        }
        mRealmValues.add(realmValue);
    }

    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            mRestID = jsonObject.getString("REST-ID");
            mDesignation = jsonObject.getString("designation");
            mDefaultLanguage = jsonObject.getString("defaultLanguage");
        } catch (JSONException e) {
            Log.e("WebfaunaRealm - putJSON: ","JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("REST-ID", mRestID);
            jsonObject.put("designation",mDesignation);
            jsonObject.put("defaultLanguage",mDefaultLanguage);
        } catch (JSONException e) {
            Log.e("WebfaunaRealm - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }
}
