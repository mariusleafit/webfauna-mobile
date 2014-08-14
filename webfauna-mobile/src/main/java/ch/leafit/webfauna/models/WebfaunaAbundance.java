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

    public WebfaunaAbundance() {
        mIndividuals = 0;
        mCollection = 0;
        mMales = 0;
        mFemales = 0;
        mEggs = 0;
        mLarvae = 0;
        mExuviae = 0;
        mNymphs = 0;
        mSubadults = 0;
        mMating = 0;
        mTandem = 0;
        mClutch = 0;
    }

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
            if(jsonObject.has("individuals"))
            mIndividuals = jsonObject.getInt("individuals");
            if(jsonObject.has("collection"))
            mCollection = jsonObject.getInt("collection");
            if(jsonObject.has("males"))
            mMales = jsonObject.getInt("males");
            if(jsonObject.has("females"))
            mFemales = jsonObject.getInt("females");
            if(jsonObject.has("eggs"))
            mEggs = jsonObject.getInt("eggs");
            if(jsonObject.has("larvae"))
            mLarvae = jsonObject.getInt("larvae");
            if(jsonObject.has("exuviae"))
            mExuviae = jsonObject.getInt("exuviae");
            if(jsonObject.has("nymphs"))
            mNymphs = jsonObject.getInt("nymphs");
            if(jsonObject.has("subadults"))
            mSubadults = jsonObject.getInt("subadults");
            if(jsonObject.has("mating"))
            mMating = jsonObject.getInt("mating");
            if(jsonObject.has("tandem"))
            mTandem = jsonObject.getInt("tandem");
            if(jsonObject.has("clutch"))
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
