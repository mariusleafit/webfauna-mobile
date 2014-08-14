package ch.leafit.webfauna.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ch.leafit.ul.list_items.ULListItemDataModel;
import ch.leafit.webfauna.data.settings.SettingsManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaSpecies extends WebfaunaBaseModel implements ULListItemDataModel {

    private String mRestID;
    private String mGroupRestID;
    private String mFamily;
    private String mGenus;
    private String mSpecies;
    private HashMap<String,String> mVernacularNames;
    private String mSubSpecies;


    public WebfaunaSpecies(JSONObject jsonObject, String groupRestID) throws Exception{
        super(jsonObject);
        putJSON(jsonObject);
        mGroupRestID = groupRestID;
    }

    public WebfaunaSpecies(WebfaunaSpecies toCopy) {
        if(toCopy == null)
            toCopy = new WebfaunaSpecies();

        mRestID = toCopy.mRestID;
        mGroupRestID = toCopy.mGroupRestID;
        mFamily = toCopy.mFamily;
        mGenus = toCopy.mGenus;
        mSpecies = toCopy.mSpecies;
        if(toCopy.mVernacularNames != null)
            mVernacularNames = new HashMap<String, String>(toCopy.mVernacularNames);
        mSubSpecies = toCopy.mSubSpecies;
    }

    public WebfaunaSpecies() {

    }

    public WebfaunaSpecies(Parcel in) {
        readFromParcel(in);
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
    public void putJSON(JSONObject jsonObject) throws Exception{
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

        }  catch (JSONException e) {
            Log.e("Species - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("WebfaunaSpecies - putJSON: ","JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON() throws Exception{
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
            throw e;
        }

        return jsonObject;
    }

    /*
    ULListItemDataModel
     */

    @Override
    public String getTitle() {
        Locale currentLocale = SettingsManager.getInstance().getLocale();
        return getTitle(currentLocale.getLanguage());
    }

    @Override
    public String getSubtitle() {
        return "";
    }

    @Override
    public int getImageResId() {
        return 0;
    }

    /*
     Parcelable
     */
    public static final Parcelable.Creator<WebfaunaSpecies> CREATOR = new Parcelable.Creator<WebfaunaSpecies>() {
        public WebfaunaSpecies createFromParcel(Parcel in ) {
            return new WebfaunaSpecies(in);
        }

        public WebfaunaSpecies[] newArray(int size) {
            return new WebfaunaSpecies[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRestID);
        dest.writeString(mFamily);
        dest.writeString(mGenus);
        dest.writeString(mSpecies);
        dest.writeSerializable(mVernacularNames);
        dest.writeString(mSubSpecies);
    }

    private void readFromParcel(Parcel in) {
        mRestID = in.readString();
        mFamily = in.readString();
        mGenus = in.readString();
        mSpecies = in.readString();
        try {
            mVernacularNames = (HashMap<String,String>)in.readSerializable();
        } catch (Exception e) {
            Log.e("WebfaunaGroup","readFromParcel",e);
        }
        mSubSpecies = in.readString();
    }

    public int describeContents() {
        return 0;
    }

}
