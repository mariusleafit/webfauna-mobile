package ch.leafit.webfauna.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ch.leafit.ul.list_items.ULListItemDataModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaGroup extends WebfaunaBaseModel implements ULListItemDataModel {

    private String mRestID;
    private String mDefaultDesignation;
    /*translated Designations (key: twoletter-iso-language identifier)*/
    private HashMap<String,String> mDesignations;
    private ArrayList<String> mValidIdentificationMethodRestIDs;

    /*not in JSON*/
    private int mLocalImageResID;

    public WebfaunaGroup(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        putJSON(jsonObject);
    }


    public WebfaunaGroup(WebfaunaGroup toCopy) {
        if(toCopy == null) {
            toCopy = new WebfaunaGroup();
        }
        mRestID = toCopy.mRestID;
        mDefaultDesignation = toCopy.mDefaultDesignation;
        mDesignations = new HashMap<String, String>(toCopy.mDesignations);
        mValidIdentificationMethodRestIDs = new ArrayList<String>(toCopy.mValidIdentificationMethodRestIDs);
        mLocalImageResID = toCopy.mLocalImageResID;
    }

    public WebfaunaGroup(Parcel parcel) {
        readFromParcel(parcel);
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

    public int getLocalImageResID() {
        return mLocalImageResID;
    }
    public void setLocalImageResID(int localImageResID) {
        mLocalImageResID = localImageResID;
    }

    @Override
    public void putJSON(JSONObject jsonObject) throws Exception{
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

        }  catch (JSONException e) {
            Log.e("Group - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("WebfaunaGroup - putJSON: ","JSON", e);
            throw e;
        }

    }

    @Override
    public JSONObject toJSON() throws Exception{
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
        } catch (Exception e) {
            Log.e("WebfaunaGroup - toJSON: ","JSON", e);
            throw e;
        }

        return jsonObject;
    }

    /*
    ULListItemDataModel
     */

    @Override
    public String getTitle() {
        return getDesignation();
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
    public static final Parcelable.Creator<WebfaunaGroup> CREATOR = new Parcelable.Creator<WebfaunaGroup>() {
        public WebfaunaGroup createFromParcel(Parcel in ) {
            return new WebfaunaGroup(in);
        }

        public WebfaunaGroup[] newArray(int size) {
            return new WebfaunaGroup[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRestID);
        dest.writeString(mDefaultDesignation);
        dest.writeSerializable(mDesignations);
        dest.writeSerializable(mValidIdentificationMethodRestIDs);

        dest.writeInt(mLocalImageResID);
    }

    private void readFromParcel(Parcel in) {
        mRestID = in.readString();
        mDefaultDesignation = in.readString();
        try {
            mDesignations = (HashMap<String,String>)in.readSerializable();

            mValidIdentificationMethodRestIDs = (ArrayList<String>)in.readSerializable();
        } catch (Exception e) {
            Log.e("WebfaunaGroup","readFromParcel",e);
        }
        mLocalImageResID = in.readInt();
    }

    public int describeContents() {
        return 0;
    }
}
