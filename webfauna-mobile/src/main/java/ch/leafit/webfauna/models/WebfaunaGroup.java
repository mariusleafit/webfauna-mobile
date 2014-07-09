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
public class WebfaunaGroup extends WebfaunaBaseModel {

    private String mRestID;
    private String mDefaultDesignation;
    /*translated Designations (key: twoletter-iso-language identifier)*/
    private HashMap<String,String> mDesignations;
    private ArrayList<String> mValidIdentificationMethodRestIDs;

    /*not in JSON*/
    private String mLocalImageName;

    public WebfaunaGroup(JSONObject jsonObject) {
        super(jsonObject);
        putJSON(jsonObject);
    }

    protected WebfaunaGroup() {}

    public String getRestID() {
        return mRestID;
    }

    /**
     *
     * @return returns defaultDesignation
     */
    public String getDesignation() {
        return mDefaultDesignation;
    }

    /**
     *
     * @param twoLetterIsoLanguage
     * @return designation in wanted language if possible else defaultDesignation
     */
    public String getDesignation(String twoLetterIsoLanguage) {
        if(mDesignations != null && mDesignations.containsKey(twoLetterIsoLanguage)) {
            return mDesignations.get(twoLetterIsoLanguage);
        } else {
            return mDefaultDesignation;
        }
    }

    public ArrayList<String> getValidIdentificationMethodRestIDs() {
        return mValidIdentificationMethodRestIDs;
    }

    public String getLocalImageName() {
        return mLocalImageName;
    }
    public void setLocalImageName(String localImageName) {
        mLocalImageName = localImageName;
    }

    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            mRestID = jsonObject.getString("REST-ID");
            mDefaultDesignation = jsonObject.getString("defaultDesignation");

            /*get designations hashmap from json*/
            mDesignations = new HashMap<String, String>();

            JSONObject jsonDesignations = jsonObject.getJSONObject("designations");
            Iterator<String> keysIterator = jsonDesignations.keys();
            if(keysIterator != null) {
                while(keysIterator.hasNext()) {
                    String key = keysIterator.next();
                    String value = jsonDesignations.getString(key);

                    mDesignations.put(key,value);
                }
            }

            /*get validIdentificationMethods*/
            mValidIdentificationMethodRestIDs = new ArrayList<String>();

            JSONArray jsonValidIdentificationMethods = jsonObject.getJSONArray("validIdentifcationMethodCodes");

            for(int i = 0; i < jsonValidIdentificationMethods.length(); i++) {
                mValidIdentificationMethodRestIDs.add(jsonValidIdentificationMethods.getString(i));
            }

        } catch (JSONException e) {
            Log.e("WebfaunaGroup - putJSON: ","JSON", e);
        }

    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("REST-ID", mRestID);
            jsonObject.put("defaultDesignation",mDefaultDesignation);

            JSONObject jsonDesignations = new JSONObject();
            for(String key: mDesignations.keySet()) {
                jsonDesignations.put(key,mDesignations.get(key));
            }
            jsonObject.put("designations", jsonDesignations);

            JSONArray jsonValidIdentificationMethods = new JSONArray();
            for(String validIdentificationMethod : mValidIdentificationMethodRestIDs) {
                jsonValidIdentificationMethods.put(validIdentificationMethod);
            }
            jsonObject.put("validIdentifcationMethodCodes",jsonValidIdentificationMethods);
        } catch (JSONException e) {
            Log.e("WebfaunaGroup - toJSON: ","JSON", e);
        }

        return jsonObject;
    }
}
