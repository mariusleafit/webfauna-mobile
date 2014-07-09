package ch.leafit.webfauna.models;

import android.util.Log;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaSource extends WebfaunaBaseModel {

    private String mAppCode;

    public WebfaunaSource(){super();}

    public WebfaunaSource(JSONObject jsonObject) {
        super(jsonObject);
        putJSON(jsonObject);
    }

    /*static instance*/
    private static WebfaunaSource sConstSource;
    public static WebfaunaSource getConstSource() {
        return sConstSource;
    }

    static {
        sConstSource = new WebfaunaSource();
        sConstSource.mAppCode = Config.webfaunaAppCodeForWebservice;
    }

    public String getAppCode() {
        return mAppCode;
    }

    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            mAppCode = jsonObject.getString("appCode");
        } catch (JSONException e) {
            Log.e("WebfaunaEnvironment - putJSON: ", "JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("appCode", mAppCode);
        } catch (JSONException e) {
            Log.e("WebfaunaSource - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }
}
