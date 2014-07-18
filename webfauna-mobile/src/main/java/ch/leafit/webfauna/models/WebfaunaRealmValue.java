package ch.leafit.webfauna.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ch.leafit.ul.list_items.ULListItemDataModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaRealmValue extends WebfaunaBaseModel implements ULListItemDataModel {

    private String mRestID;
    private String mDesignation;
    private String mLanguageCode;

    public WebfaunaRealmValue(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        putJSON(jsonObject);
    }

    public WebfaunaRealmValue(WebfaunaRealmValue toCopy) {
        if(toCopy == null) {
            toCopy = new WebfaunaRealmValue();
        }
        mRestID = toCopy.mRestID;
        mDesignation = toCopy.mDesignation;
        mLanguageCode = toCopy.mLanguageCode;
    }

    public WebfaunaRealmValue() {

    }

    public WebfaunaRealmValue(Parcel in) {
        readFromParcel(in);
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
    public void putJSON(JSONObject jsonObject) throws Exception{
        try {
            mRestID = jsonObject.getString("REST-ID");
            mDesignation = jsonObject.getString("designation");
            mLanguageCode = jsonObject.getString("languageCode");
        }  catch (JSONException e) {
            Log.e("RealmValue - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("WebfaunaRealm - putJSON: ", "JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON() throws Exception{
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("REST-ID", mRestID);
            jsonObject.put("designation",mDesignation);
            jsonObject.put("languageCode",mLanguageCode);
        } catch (Exception e) {
            Log.e("WebfaunaRealm - toJSON: ", "JSON", e);
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
    public static final Parcelable.Creator<WebfaunaRealmValue> CREATOR = new Parcelable.Creator<WebfaunaRealmValue>() {
        public WebfaunaRealmValue createFromParcel(Parcel in ) {
            return new WebfaunaRealmValue(in);
        }

        public WebfaunaRealmValue[] newArray(int size) {
            return new WebfaunaRealmValue[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRestID);
        dest.writeString(mDesignation);
        dest.writeString(mLanguageCode);
    }

    private void readFromParcel(Parcel in) {
        mRestID = in.readString();
        mDesignation = in.readString();
        mLanguageCode = in.readString();
    }

    public int describeContents() {
        return 0;
    }
}
