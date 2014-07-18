package ch.leafit.webfauna.models;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 18/07/14.
 */
public class WebfaunaUser extends WebfaunaBaseModel{
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

    public WebfaunaUser(JSONObject jsonObject) throws Exception{
        putJSON(jsonObject);
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
        }  catch (JSONException e) {
            Log.e("User - putJSON: ", "JSON", e);
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
}
