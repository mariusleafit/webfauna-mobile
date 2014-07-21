package ch.leafit.webfauna.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by marius on 18/07/14.
 */
public class WebfaunaUser extends WebfaunaBaseModel implements Parcelable{
    private String mEmail;
    private String mPassword;
    private String mFirstName;
    private String mLastName;
    private String mRestID;


    public WebfaunaUser(String email, String password, String firstName, String lastName, String restID) {
        mEmail = email;
        mPassword = password;
        mFirstName = firstName;
        mLastName = lastName;
        mRestID = restID;
    }

    public WebfaunaUser(Parcel in) {
        readFromParcel(in);
    }

    public WebfaunaUser(JSONObject jsonObject, String password) throws Exception{
        putJSON(jsonObject);
        mPassword = password;
    }

    public String getEmail() {
        return mEmail;
    }
    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }
    public void setPassword(String password) {
        mPassword = password;
    }

    public String getFirstName() {
        return mFirstName;
    }
    public void setFirstName(String firstName) {
        mFirstName = firstName;
    }

    public String getLastName() {
        return mLastName;
    }
    public void setLastName(String lastName) {
        mLastName = lastName;
    }

    public String getRestID() {
        return mRestID;
    }
    public void setRestID(String restID) {
        mRestID = restID;
    }

    @Override
    public void putJSON(JSONObject jsonObject) throws Exception {
        try {
            mRestID = jsonObject.getString("REST-ID");
            mEmail = jsonObject.getString("email");
            mFirstName = jsonObject.getString("firstName");
            mLastName = jsonObject.getString("lastName");
        } catch (Exception e) {
            Log.e("User - putJSON: ","JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON() throws Exception {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("REST-ID", mRestID);
            jsonObject.put("email",mEmail);
            jsonObject.put("firstName",mFirstName);
            jsonObject.put("lastName",mLastName);
        } catch (Exception e) {
            Log.e("User - toJSON: ", "JSON", e);
            throw e;
        }

        return jsonObject;
    }

    /*
     Parcelable
     */
    public static final Parcelable.Creator<WebfaunaUser> CREATOR = new Parcelable.Creator<WebfaunaUser>() {
        public WebfaunaUser createFromParcel(Parcel in ) {
            return new WebfaunaUser(in);
        }

        public WebfaunaUser[] newArray(int size) {
            return new WebfaunaUser[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRestID);
        dest.writeString(mEmail);
        dest.writeString(mPassword);
        dest.writeString(mFirstName);
        dest.writeString(mLastName);
    }

    private void readFromParcel(Parcel in) {
        mRestID = in.readString();
        mEmail = in.readString();
        mPassword = in.readString();
        mFirstName = in.readString();
        mLastName = in.readString();
    }

    public int describeContents() {
        return 0;
    }
}
