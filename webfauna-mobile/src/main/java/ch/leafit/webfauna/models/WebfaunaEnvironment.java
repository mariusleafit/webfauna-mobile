package ch.leafit.webfauna.models;

import android.util.Log;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaEnvironment  extends WebfaunaBaseModel{
    private WebfaunaRealmValue mEnvironment;
    private WebfaunaRealmValue mMilieu;
    private WebfaunaRealmValue mSubstrat;
    private WebfaunaRealmValue mStructure;

    public WebfaunaEnvironment() {}

    public WebfaunaEnvironment(JSONObject jsonObject) {
        super(jsonObject);
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
    public void putJSON(JSONObject jsonObject) {
        try {
            /*
            get RealmValues from DataDispatcher
             */
            if(DataDispatcher.getInstantce().isInitialized()) {
                mEnvironment = DataDispatcher.getInstantce().getEnvironmentRealm().getRealmValue(jsonObject.getString("environmentCode"));
                mMilieu = DataDispatcher.getInstantce().getMilieuRealm().getRealmValue(jsonObject.getString("milieuCode"));
                mSubstrat = DataDispatcher.getInstantce().getSubstratRealm().getRealmValue(jsonObject.getString("substratCode"));
                mStructure = DataDispatcher.getInstantce().getStructureRealm().getRealmValue(jsonObject.getString("structureCode"));
            }
        } catch (JSONException e) {
            Log.e("WebfaunaEnvironment - putJSON: ", "JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("environmentCode", mEnvironment.getRestID());
            jsonObject.put("milieuCode",mMilieu.getRestID());
            jsonObject.put("substratCode",mSubstrat.getRestID());
            jsonObject.put("structureCode",mStructure.getRestID());
        } catch (JSONException e) {
            Log.e("WebfaunaEnvironment - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }
}
