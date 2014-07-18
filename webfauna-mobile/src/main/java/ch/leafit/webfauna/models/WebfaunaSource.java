package ch.leafit.webfauna.models;

import android.content.res.Resources;
import android.util.Log;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaSource extends WebfaunaBaseModel implements WebfaunaValidatable {

    private String mAppCode;

    public WebfaunaSource(){super();}

    public WebfaunaSource(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        putJSON(jsonObject);
    }

    public WebfaunaSource(WebfaunaSource toCopy) {
        if(toCopy == null) {
            toCopy = new WebfaunaSource();
        }
        mAppCode = toCopy.mAppCode;
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
    public void putJSON(JSONObject jsonObject) throws Exception{
        try {
            mAppCode = jsonObject.getString("appCode");
        }  catch (JSONException e) {
            Log.e("Source - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("WebfaunaEnvironment - putJSON: ", "JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON() throws Exception{
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("appCode", mAppCode);
        } catch (Exception e) {
            Log.e("WebfaunaSource - toJSON: ", "JSON", e);
            throw e;
        }

        return jsonObject;
    }

    /*
    WebfaunaValidatable
     */

    @Override
    public WebfaunaValidationResult getValidationResult(Resources res) {
        WebfaunaValidationResult validationResult = new WebfaunaValidationResult(true,"");
        return validationResult;
    }
}
