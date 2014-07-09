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
public class WebfaunaSpecies extends WebfaunaBaseModel {

    private String mRestID;
    private String mGroupRestID;
    private String mFamily;
    private String mGenus;
    private String mSpecies;
    private HashMap<String,String> mVernacularNames;
    private String mSubSpecies;


    public WebfaunaSpecies(JSONObject jsonObject, String groupRestID) {
        super(jsonObject);
        putJSON(jsonObject);
        mGroupRestID = groupRestID;
    }

    public String getTitle(String twoLetterIsoLng) {
        String returnTitle = mGenus + " " + mSpecies;

        if(mSubSpecies != null && mSubSpecies != "") {
            returnTitle += " " + mSubSpecies;
        }

        String vernacularName = getVernacularName(twoLetterIsoLng);
        if(vernacularName != null && vernacularName != "") {
            returnTitle += " (" + vernacularName + " )";
        }

        return returnTitle;
    }

    public String getRestID() {
        return mRestID;
    }

    public String getGroupRestID() {
        return mGroupRestID;
    }

    public String getFamily() {
        return mFamily;
    }

    public String getGenus() {
        return mGenus;
    }

    public String getSpecies() {
        return mSpecies;
    }

    public String getSubSpecies() {
        return mSubSpecies;
    }

    public String getVernacularName(String twoLetterISOLng) {
        if(mVernacularNames != null && mVernacularNames.containsKey(twoLetterISOLng)) {
            return mVernacularNames.get(twoLetterISOLng);
        } else {
            return null;
        }
    }


    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            mRestID = jsonObject.getString("REST-ID");
            if(jsonObject.has("family"))
                mFamily = jsonObject.getString("family");
            if(jsonObject.has("genus"))
                mGenus = jsonObject.getString("genus");
            if(jsonObject.has("species"))
                mSpecies = jsonObject.getString("species");
            if(jsonObject.has("subSpecies"))
                mSubSpecies = jsonObject.getString("subSpecies");

            /*get vernacularNames hashmap from json*/
            mVernacularNames = new HashMap<String, String>();

            if(jsonObject.has("vernacularNames")) {
                JSONObject jsonVernacularNames = jsonObject.getJSONObject("vernacularNames");
                Iterator<String> keysIterator = jsonVernacularNames.keys();
                if (keysIterator != null) {
                    while (keysIterator.hasNext()) {
                        String key = keysIterator.next();
                        String value = jsonVernacularNames.getString(key);

                        mVernacularNames.put(key, value);
                    }
                }
            }

        } catch (JSONException e) {
            Log.e("WebfaunaSpecies - putJSON: ","JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("REST-ID", mRestID);
            jsonObject.put("family", mFamily);
            jsonObject.put("genus",mGenus);
            jsonObject.put("species",mSpecies);
            jsonObject.put("subSpecies",mSubSpecies);

            JSONObject jsonVernacularNames = new JSONObject();
            for(String key: mVernacularNames.keySet()) {
                jsonVernacularNames.put(key,mVernacularNames.get(key));
            }
            jsonObject.put("vernacularNames",jsonVernacularNames);
        } catch (Exception e) {
            Log.e("WebfaunaSpecies - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }
}
