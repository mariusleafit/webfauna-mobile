package ch.leafit.webfauna.models;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaAbundance extends WebfaunaBaseModel implements WebfaunaValidatable, Parcelable {

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

    public WebfaunaAbundance(JSONObject jsonObject)  throws Exception{
        super(jsonObject);
        putJSON(jsonObject);
    }

    public WebfaunaAbundance() {}

    public WebfaunaAbundance(Parcel in) {
        readFromParcel(in);
    }

    public WebfaunaAbundance(WebfaunaAbundance toCopy){
        if(toCopy == null) {
            toCopy = new WebfaunaAbundance();
        }
        mIndividuals = toCopy.mIndividuals;
        mCollection = toCopy.mCollection;
        mMales = toCopy.mMales;
        mFemales = toCopy.mFemales;
        mEggs = toCopy.mEggs;
        mLarvae = toCopy.mLarvae;
        mExuviae = toCopy.mExuviae;
        mNymphs = toCopy.mNymphs;
        mSubadults = toCopy.mSubadults;
        mMating = toCopy.mMating;
        mTandem = toCopy.mTandem;
        mClutch = toCopy.mClutch;
    }

    @Override
    public void putJSON(JSONObject jsonObject) throws Exception{
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
            Log.e("WebfaunaAbundance - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("Abundance - putJSON: ", "JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON()  throws Exception{
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


    /*
     Parcelable
     */
    public static final Parcelable.Creator<WebfaunaAbundance> CREATOR = new Parcelable.Creator<WebfaunaAbundance>() {
        public WebfaunaAbundance createFromParcel(Parcel in ) {
            return new WebfaunaAbundance(in);
        }

        public WebfaunaAbundance[] newArray(int size) {
            return new WebfaunaAbundance[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mIndividuals);
        dest.writeInt(mCollection);
        dest.writeInt(mMales);
        dest.writeInt(mFemales);
        dest.writeInt(mEggs);
        dest.writeInt(mLarvae);
        dest.writeInt(mExuviae);
        dest.writeInt(mNymphs);
        dest.writeInt(mSubadults);
        dest.writeInt(mMating);
        dest.writeInt(mTandem);
        dest.writeInt(mClutch);
    }

    private void readFromParcel(Parcel in) {
        mIndividuals = in.readInt();
        mCollection = in.readInt();
        mMales = in.readInt();
        mFemales = in.readInt();
        mEggs = in.readInt();
        mLarvae = in.readInt();
        mExuviae = in.readInt();
        mNymphs = in.readInt();
        mSubadults = in.readInt();
        mMating = in.readInt();
        mTandem = in.readInt();
        mClutch = in.readInt();
    }

    public int describeContents() {
        return 0;
    }
}
