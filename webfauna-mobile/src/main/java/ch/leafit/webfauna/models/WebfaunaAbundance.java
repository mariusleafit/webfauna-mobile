package ch.leafit.webfauna.models;

import android.util.Log;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaAbundance extends WebfaunaBaseModel {

    public Integer mIndividuals;
    public Integer mCollection;
    public Integer mMales;
    public Integer mFemales;
    public Integer mEggs;
    public Integer mLarvae;
    public Integer mExuviae;
    public Integer mNymphs;
    public Integer mSubadults;
    public Integer mMating;
    public Integer mTandem;
    public Integer mClutch;

    public WebfaunaAbundance(JSONObject jsonObject) {
        super(jsonObject);
        putJSON(jsonObject);
    }

    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            mIndividuals = jsonObject.getInt("individuals");
            mCollection = jsonObject.getInt("collection");
            mMales = jsonObject.getInt("males");
            mFemales = jsonObject.getInt("females");
            mEggs = jsonObject.getInt("eggs");
            mLarvae = jsonObject.getInt("larvae");
            mExuviae = jsonObject.getInt("exuviae");
            mNymphs = jsonObject.getInt("nymphs");
            mSubadults = jsonObject.getInt("subadults");
            mMating = jsonObject.getInt("mating");
            mTandem = jsonObject.getInt("tandem");
            mClutch = jsonObject.getInt("clutch");
        } catch (JSONException e) {
            Log.e("WebfaunaRealm - putJSON: ", "JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("individuals", mIndividuals);
            jsonObject.put("collection", mCollection);
            jsonObject.put("males", mMales);
            jsonObject.put("females", mFemales);
            jsonObject.put("eggs", mEggs);
            jsonObject.put("larvae", mLarvae);
            jsonObject.put("exuviae", mExuviae);
            jsonObject.put("nymphs", mNymphs);
            jsonObject.put("subadults", mSubadults);
            jsonObject.put("mating", mMating);
            jsonObject.put("tandem", mTandem);
            jsonObject.put("clutch", mClutch);
        } catch (JSONException e) {
            Log.e("WebfaunaAbundance - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }
}
