package ch.leafit.webfauna.models;

import android.util.Log;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.spi.CalendarNameProvider;

/**
 * Created by marius on 04/07/14.
 */
public class WebfaunaObservation extends WebfaunaBaseModel implements Cloneable {

    private WebfaunaGroup mWebfaunaGroup;
    private WebfaunaSpecies mWebfaunaSpecies;
    private WebfaunaRealmValue mIdentificationMethod;
    private Date mObservationDate;
    private WebfaunaEnvironment mEnvironment;
    private WebfaunaLocation mLocation;
    private WebfaunaAbundance mAbundance;
    private WebfaunaSource mSource;

    public WebfaunaObservation() {
        mEnvironment = new WebfaunaEnvironment();
        mSource = WebfaunaSource.getConstSource();
    }

    public WebfaunaObservation(JSONObject jsonObject) {
        super(jsonObject);
        putJSON(jsonObject);
    }

    /*getters & setters*/

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

    public void setEnvironmentRealmValue(WebfaunaRealmValue environmentRealmValue) {
        mEnvironment.setEnvironment(environmentRealmValue);
    }
    public void setMilieuRealmValue(WebfaunaRealmValue milieuRealmValue) {
        mEnvironment.setMilieu(milieuRealmValue);
    }
    public void setStructureRealmValue(WebfaunaRealmValue structureRealmValue) {
        mEnvironment.setStructure(structureRealmValue);
    }
    public void setSubstratRealmValue(WebfaunaRealmValue substratRealmValue) {
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
    /*
    Files stuff
     */
    public void addFile() {

    }

    public void setFiles() {

    }

    public void clearFiles() {

    }

    @Override
    public void putJSON(JSONObject jsonObject) {
        try {
            String groupRestID = jsonObject.getString("groupId");
            mWebfaunaGroup = DataDispatcher.getInstantce().getWebfaunaGroup(groupRestID);
            mWebfaunaSpecies = DataDispatcher.getInstantce().getSpecies(groupRestID,jsonObject.getString("speciesId"));
            mIdentificationMethod = DataDispatcher.getInstantce().getIdentificationMethodRealm().getRealmValue(jsonObject.getString("identificationMethodCode"));

            /*seperate date*/
            Integer year = jsonObject.getInt("dateYear");
            Integer month = jsonObject.getInt("dateMonth");
            Integer date = jsonObject.getInt("dateDay");


            Calendar cal = Calendar.getInstance();

            cal.set(year, month, date);
            mObservationDate = cal.getTime();

            mEnvironment = new WebfaunaEnvironment(jsonObject.getJSONObject("environment"));
            mLocation = new WebfaunaLocation(jsonObject.getJSONObject("location"));
            mAbundance = new WebfaunaAbundance(jsonObject.getJSONObject("abundance"));
            mSource = new WebfaunaSource(jsonObject.getJSONObject("source"));

        } catch (JSONException e) {
            Log.e("WebfaunaObservation - toJSON: ", "JSON", e);
        }
    }

    @Override
    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("groupId", mWebfaunaGroup.getRestID());
            jsonObject.put("speciesId", mWebfaunaSpecies.getRestID());
            jsonObject.put("identificationMethodCode", mIdentificationMethod.getRestID());

            /*seperate date*/
            Calendar cal = Calendar.getInstance();
            cal.setTime(mObservationDate);

            jsonObject.put("dateDay",cal.get(Calendar.DAY_OF_MONTH));
            jsonObject.put("dateMonth",cal.get(Calendar.MONTH));
            jsonObject.put("dateYear",cal.get(Calendar.YEAR));

            jsonObject.put("environment",mEnvironment.toJSON());
            jsonObject.put("location",mLocation.toJSON());
            jsonObject.put("abundance",mAbundance.toJSON());
            jsonObject.put("source",mSource.toJSON());

        } catch (JSONException e) {
            Log.e("WebfaunaRealm - toJSON: ", "JSON", e);
        }

        return jsonObject;
    }

    /*
    Cloneable
     */
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
