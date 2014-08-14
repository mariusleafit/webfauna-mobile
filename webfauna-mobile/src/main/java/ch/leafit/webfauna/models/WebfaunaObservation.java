package ch.leafit.webfauna.models;

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateFormat;
import android.util.Log;
import ch.leafit.ul.list_items.ULListItemDataModel;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.spi.CalendarNameProvider;

/**
 * Created by marius on 04/07/14.
 */
public class WebfaunaObservation extends WebfaunaBaseModel implements WebfaunaValidatable {

    /*used to identify WebfaunaObservation in DB*/
    private UUID mGUID;

    private WebfaunaGroup mWebfaunaGroup;
    private WebfaunaSpecies mWebfaunaSpecies;
    private WebfaunaRealmValue mIdentificationMethod;
    private Date mObservationDate;
    private WebfaunaEnvironment mEnvironment;
    private WebfaunaLocation mLocation;
    private WebfaunaAbundance mAbundance;
    private WebfaunaSource mSource;

    private boolean mIsOnline;

    public WebfaunaObservation() {
        mEnvironment = new WebfaunaEnvironment();
        mSource = WebfaunaSource.getConstSource();
        mObservationDate = new Date();
        mGUID = UUID.randomUUID();
    }

    public WebfaunaObservation(JSONObject jsonObject) throws Exception{
        super(jsonObject);
        putJSON(jsonObject);

        if (mEnvironment == null)
            mEnvironment = new WebfaunaEnvironment();
        if (mSource == null)
            mSource = new WebfaunaSource();

        if(mObservationDate == null)
            mObservationDate = new Date();

        if(mGUID == null)
            mGUID = UUID.randomUUID();
    }

    public WebfaunaObservation(WebfaunaObservation toCopy) {
        if(toCopy == null)
            toCopy = new WebfaunaObservation();

        if(toCopy.mGUID != null)
            mGUID = UUID.fromString(toCopy.mGUID.toString());

        if(toCopy.mWebfaunaGroup != null)
            mWebfaunaGroup = new WebfaunaGroup(toCopy.mWebfaunaGroup);
        if(toCopy.mWebfaunaSpecies != null)
            mWebfaunaSpecies = new WebfaunaSpecies(toCopy.mWebfaunaSpecies);
        if(toCopy.mIdentificationMethod != null)
            mIdentificationMethod = new WebfaunaRealmValue(toCopy.mIdentificationMethod);
        if(toCopy.mObservationDate != null)
            mObservationDate = (Date) toCopy.mObservationDate.clone();
        if(toCopy.mEnvironment != null)
            mEnvironment = new WebfaunaEnvironment(toCopy.mEnvironment);
        if(toCopy.mLocation != null)
            mLocation = new WebfaunaLocation(toCopy.mLocation);
        if(toCopy.mAbundance != null)
            mAbundance = new WebfaunaAbundance(toCopy.mAbundance);
        if(toCopy.mSource != null)
            mSource = new WebfaunaSource(toCopy.mSource);

        mIsOnline = toCopy.mIsOnline;

        if (mEnvironment == null) {
            mEnvironment = new WebfaunaEnvironment();
        }
    }


    /*getters & setters*/

    public UUID getGUID() {
        return mGUID;
    }

    public void setGUID(UUID guid) {
        mGUID = guid;
    }

    public WebfaunaGroup getWebfaunaGroup() {
        return mWebfaunaGroup;
    }

    public void setWebfaunaGroup(WebfaunaGroup webfaunaGroup) {
        mWebfaunaGroup = webfaunaGroup;
    }

    public WebfaunaSpecies getWebfaunaSpecies() {
        return mWebfaunaSpecies;
    }

    public void setWebfaunaSpecies(WebfaunaSpecies webfaunaSpecies) {
        mWebfaunaSpecies = webfaunaSpecies;
    }

    public WebfaunaRealmValue getIdentificationMethod() {
        return mIdentificationMethod;
    }

    public void setIdentificationMethod(WebfaunaRealmValue identificationMethod) {
        mIdentificationMethod = identificationMethod;
    }

    public Date getObservationDate() {
        return mObservationDate;
    }

    public void setObservationDate(Date observationDate) {
        mObservationDate = observationDate;
    }

    public WebfaunaEnvironment getWebfaunaEnvironment() {
        return mEnvironment;
    }

    public void setWebfaunaEnvironment(WebfaunaEnvironment webfaunaEnvironment) {
        mEnvironment = webfaunaEnvironment;
    }

    public WebfaunaRealmValue getEnvironmentRealmValue() {
        if(mEnvironment != null)
            return mEnvironment.getEnvironment();
        else
            return null;
    }

    public void setEnvironmentRealmValue(WebfaunaRealmValue environmentRealmValue) {
        if(mEnvironment != null)
            mEnvironment.setEnvironment(environmentRealmValue);
    }

    public WebfaunaRealmValue getMilieuRealmValue() {
        if(mEnvironment != null)
            return mEnvironment.getMilieu();
        else
            return null;
    }

    public void setMilieuRealmValue(WebfaunaRealmValue milieuRealmValue) {
        if(mEnvironment != null)
            mEnvironment.setMilieu(milieuRealmValue);
    }

    public WebfaunaRealmValue getStructureRealmValue() {
        if(mEnvironment != null)
            return mEnvironment.getStructure();
        else
            return null;
    }

    public void setStructureRealmValue(WebfaunaRealmValue structureRealmValue) {
        if(mEnvironment != null)
            mEnvironment.setStructure(structureRealmValue);
    }

    public WebfaunaRealmValue getSubstratRealmValue() {
        if(mEnvironment != null)
            return mEnvironment.getSubstrat();
        else
            return null;
    }

    public void setSubstratRealmValue(WebfaunaRealmValue substratRealmValue) {
        if(mEnvironment != null)
            mEnvironment.setSubstrat(substratRealmValue);
    }


    public WebfaunaLocation getWebfaunaLocation() {
        return mLocation;
    }

    public void setWebfaunaLocation(WebfaunaLocation webfaunaLocation) {
        mLocation = webfaunaLocation;
    }

    public WebfaunaAbundance getWebfaunaAbundance() {
        return mAbundance;
    }

    public void setWebfaunaAbundance(WebfaunaAbundance webfaunaAbundance) {
        mAbundance = webfaunaAbundance;
    }

    public WebfaunaSource getWebfaunaSource() {
        return mSource;
    }

    public void setIsOnline(boolean isOnline) {
        mIsOnline = isOnline;
    }

    public boolean isOnline() {
        return mIsOnline;
    }

    /**
     *
     * */
    public WebfaunaObservationULListDataModel getListItemDataModel() {
        WebfaunaObservationULListDataModel returnData = null;

        try {
            returnData = new WebfaunaObservationULListDataModel(this);
        } catch (Exception e) {
            Log.e("WebfaunaObservation","getListItemDataModel",e);
        }

        return returnData;
    }

    @Override
    public void putJSON(JSONObject jsonObject) throws Exception{
        try {

            String groupRestID = jsonObject.getString("groupId");
            mWebfaunaGroup = DataDispatcher.getInstantce().getWebfaunaGroup(groupRestID);
            mWebfaunaSpecies = DataDispatcher.getInstantce().getSpecies(groupRestID, jsonObject.getString("speciesId"));
            mIdentificationMethod = DataDispatcher.getInstantce().getIdentificationMethodRealm().getRealmValue(jsonObject.getString("identificationMethodCode"));

            /*seperate date*/
            Integer year = jsonObject.getInt("dateYear");
            /*because the months in the super duper java date mess start with 0*/
            Integer month = jsonObject.getInt("dateMonth") - 1;
            Integer date = jsonObject.getInt("dateDay");


            Calendar cal = Calendar.getInstance();

            cal.set(year, month, date);
            mObservationDate = cal.getTime();

            mEnvironment = new WebfaunaEnvironment(jsonObject.getJSONObject("environment"));
            mLocation = new WebfaunaLocation(jsonObject.getJSONObject("location"));
            if(jsonObject.has("abundance"))
            mAbundance = new WebfaunaAbundance(jsonObject.getJSONObject("abundance"));
            mSource = new WebfaunaSource(jsonObject.getJSONObject("source"));

        }  catch (JSONException e) {
            Log.e("Observation - putJSON: ", "JSON", e);
        } catch (Exception e) {
            Log.e("WebfaunaObservation - toJSON: ", "JSON", e);
            throw e;
        }
    }

    @Override
    public JSONObject toJSON() throws Exception{
        JSONObject jsonObject = new JSONObject();

        try {

            jsonObject.put("groupId", mWebfaunaGroup.getRestID());
            jsonObject.put("speciesId", mWebfaunaSpecies.getRestID());
            jsonObject.put("identificationMethodCode", mIdentificationMethod.getRestID());

            /*seperate date*/
            Calendar cal = Calendar.getInstance(Locale.GERMANY);
            cal.setTime(mObservationDate);

            jsonObject.put("dateDay", cal.get(Calendar.DAY_OF_MONTH));
            /*because the months in the super duper java date mess start with 0*/
            jsonObject.put("dateMonth", cal.get(Calendar.MONTH) + 1);
            jsonObject.put("dateYear", cal.get(Calendar.YEAR));

            if(mEnvironment != null)
            jsonObject.put("environment", mEnvironment.toJSON());
            if(mLocation != null)
            jsonObject.put("location", mLocation.toJSON());
            if(mAbundance != null)
            jsonObject.put("abundance", mAbundance.toJSON());
            if(mSource != null)
            jsonObject.put("source", mSource.toJSON());

        } catch (Exception e) {
            Log.e("WebfaunaRealm - toJSON: ", "JSON", e);
            throw e;
        }

        return jsonObject;
    }

    @Override
    public WebfaunaValidationResult getValidationResult(Resources res) {
        if (res != null) {
            WebfaunaValidationResult validationResult = new WebfaunaValidationResult(true, "");

            String validationMessage = "";

            if (mObservationDate == null) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.observation_validation_date_null);
            } else {
                /*date must be between 1900 and present*/
                Calendar cal = Calendar.getInstance();
                Date currentDate = cal.getTime();
                cal.set(Calendar.YEAR, 1900);
                if (mObservationDate.before(cal.getTime()) || mObservationDate.after(currentDate)) {
                    validationResult.setIsValid(false);
                    validationMessage += "\r- " + res.getString(R.string.observation_validation_date_invalid);
                }
            }

            if (mLocation == null) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.observation_validation_location_null);
            } else {
                WebfaunaValidationResult locationValidationResult = mLocation.getValidationResult(res);
                if (!locationValidationResult.isValid()) {
                    validationResult.setIsValid(false);
                    validationMessage += locationValidationResult.getValidationMessage();
                }
            }

            if (mWebfaunaGroup == null) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.observation_validation_group_null);
            }

            if (mWebfaunaSpecies == null) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.observation_validation_species_null);
            }

            if (mIdentificationMethod == null) {
                validationResult.setIsValid(false);
                validationMessage += "\r- " + res.getString(R.string.observation_validation_identificationmethod_null);
            }

            validationResult.setValidationMessage(validationMessage);

            return validationResult;
        } else {
            return null;
        }
    }

    /**
     * contains the data to show in the ULList (to minimize the data to parcel)
     */
    public static class WebfaunaObservationULListDataModel implements ULListItemDataModel {

        private String mGUID;
        private String mTitle;
        private String mSubtitle;
        private boolean mIsSelected;
        private boolean mIsOnline;

        public WebfaunaObservationULListDataModel(Parcel in) {
            readFromParcel(in);
        }

        public WebfaunaObservationULListDataModel(WebfaunaObservation observation) throws Exception{
            if(observation != null && observation.getGUID() != null && observation.getWebfaunaSpecies() != null && observation.getObservationDate() != null) {
                mGUID = observation.getGUID().toString();
                mTitle = observation.getWebfaunaSpecies().getTitle();


                mSubtitle = DateFormat.format("dd-MM-yyyy",observation.getObservationDate()).toString();
                mIsOnline = observation.isOnline();
            } else {
                throw new Exception("Cannot create WebfaunaObservationULListDataModel: argument null");
            }
        }

        public String getGUID() {
            return mGUID;
        }

        public boolean isSelected() {
            return mIsSelected;
        }
        public void setIsSelected(boolean isSelected) {
            mIsSelected = isSelected;
        }

        public boolean isOnline(){
            return mIsOnline;
        }
        public void setIsOnline(boolean isOnline) {
            mIsOnline = isOnline;
        }
        /*
            ULListItemDataModel
         */

        @Override
        public String getTitle() {
            return mTitle;
        }

        @Override
        public String getSubtitle() {
            return mSubtitle;
        }

        @Override
        public int getImageResId() {
            return 0;
        }



        /*
         Parcelable
         */
        public static final Parcelable.Creator<WebfaunaObservationULListDataModel> CREATOR = new Parcelable.Creator<WebfaunaObservationULListDataModel>() {
            public WebfaunaObservationULListDataModel createFromParcel(Parcel in) {
                return new WebfaunaObservationULListDataModel(in);
            }

            public WebfaunaObservationULListDataModel[] newArray(int size) {
                return new WebfaunaObservationULListDataModel[size];
            }
        };

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(mGUID);
            dest.writeString(mTitle);
            dest.writeString(mSubtitle);
            dest.writeByte((byte) (mIsSelected ? 1 : 0));
            dest.writeByte((byte) (mIsOnline ? 1 : 0));
        }

        private void readFromParcel(Parcel in) {
            mGUID = in.readString();
            mTitle = in.readString();
            mSubtitle = in.readString();
            mIsSelected = in.readByte() != 0;
            mIsOnline = in.readByte() != 0;
        }

        public int describeContents() {
            return 0;
        }
    }
}
