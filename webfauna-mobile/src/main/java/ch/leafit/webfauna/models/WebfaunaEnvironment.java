package ch.leafit.webfauna.models;

import android.content.res.Resources;
import android.util.Log;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaEnvironment  extends WebfaunaBaseModel implements WebfaunaValidatable{
    private WebfaunaRealmValue mEnvironment;
    private WebfaunaRealmValue mMilieu;
    private WebfaunaRealmValue mSubstrat;
    private WebfaunaRealmValue mStructure;

    public WebfaunaEnvironment() {}

    public WebfaunaEnvironment(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        putJSON(jsonObject);
    }

    public WebfaunaEnvironment(WebfaunaEnvironment toCopy) {
        if(toCopy == null) {
            toCopy = new WebfaunaEnvironment();
        }
        mEnvironment = new WebfaunaRealmValue(toCopy.mEnvironment);
        mMilieu = new WebfaunaRealmValue(toCopy.mMilieu);
        mSubstrat = new WebfaunaRealmValue(toCopy.mSubstrat);
        mStructure = new WebfaunaRealmValue(toCopy.mStructure);
    }


    public WebfaunaRealmValue getEnvironment() {
        return mEnvironment;
    }
    public void setEnvironment(WebfaunaRealmValue environment) {
        mEnvironment = environment;
    }

    public WebfaunaRealmValue getMilieu() {
        return mMilieu;
    }
    public void setMilieu(WebfaunaRealmValue milieu) {
        mMilieu = milieu;
    }

    public WebfaunaRealmValue getSubstrat() {
        return mSubstrat;
    }
    public void setSubstrat(WebfaunaRealmValue substrat) {
        mSubstrat = substrat;
    }

    public WebfaunaRealmValue getStructure() {
        return mStructure;
    }
    public void setStructure(WebfaunaRealmValue structure) {
        mStructure = structure;
    }

    @Override
    public void putJSON(JSONObject jsonObject) throws Exception{
        try {
            /*
            get RealmValues from DataDispatcher
             */
            if(DataDispatcher.getInstantce().isInitialized()) {
                if(jsonObject.has("environmentCode"))
                    mEnvironment = DataDispatcher.getInstantce().getEnvironmentRealm().getRealmValue(jsonObject.getString("environmentCode"));
                if(jsonObject.has("milieuCode"))
                    mMilieu = DataDispatcher.getInstantce().getMilieuRealm().getRealmValue(jsonObject.getString("milieuCode"));
                if(jsonObject.has("substratCode"))
                    mSubstrat = DataDispatcher.getInstantce().getSubstratRealm().getRealmValue(jsonObject.getString("substratCode"));
                if(jsonObject.has("structureCode"))
                    mStructure = DataDispatcher.getInstantce().getStructureRealm().getRealmValue(jsonObject.getString("structureCode"));
            }
        }catch (JSONException e) {
            Log.e("WebfaunaEnvironment - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("WebfaunaEnvironment - putJSON: ", "JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON() throws Exception{
        JSONObject jsonObject = new JSONObject();

        try {
            if(mEnvironment != null)
            jsonObject.put("environmentCode", mEnvironment.getRestID());
            if(mMilieu != null)
            jsonObject.put("milieuCode",mMilieu.getRestID());
            if(mSubstrat != null)
            jsonObject.put("substratCode",mSubstrat.getRestID());
            if(mStructure != null)
            jsonObject.put("structureCode",mStructure.getRestID());
        } catch (Exception e) {
            Log.e("WebfaunaEnvironment - toJSON: ", "JSON", e);
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
