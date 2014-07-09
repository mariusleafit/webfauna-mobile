package ch.leafit.webfauna.data;

import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaRealm;
import ch.leafit.webfauna.models.WebfaunaSpecies;
import ch.leafit.webfauna.webservice.GetSystematicsAsyncTask;
import ch.leafit.webfauna.webservice.GetThesaurusAsyncTask;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marius on 09/07/14.
 *
 * Central point for data interaction
 */
public class DataDispatcher implements GetSystematicsAsyncTask.Callback, GetThesaurusAsyncTask.Callback{

    public DataDispatcher() {
        mSubscribers = new ArrayList<DataDispatcherBroadcastSubscriber>();

        mWebfaunaGroups = new ArrayList<WebfaunaGroup>();
        mWebfaunaSpeciesOfGroups = new HashMap<String, ArrayList<WebfaunaSpecies>>();
    }

    /*
    Singleton
     */
    private static DataDispatcher sSharedInstance;

    public static DataDispatcher getInstantce() {
        if(sSharedInstance == null) {
            sSharedInstance = new DataDispatcher();
        }
        return sSharedInstance;
    }

    /*
    Maintenance
     */

    /**
     *
     * @return true if thesaurus & systematics are initialized
     */
    public boolean isInitialized() {
        if(isSystematicsInitialized() && isThesaurusInitialized()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 1. get systematics & thesaurus from local DB if possible
     * 2. if(not possible to get data from local BD & internet connection available) --> download data from webservice
     */
    public void initialize() {
        if(!isInitialized()) {
            getSystematicsFromDB();
            getThesaurusFromDB();

            if(!isInitialized()/* & internet connection???*/) {
                if(!isSystematicsInitialized()) {
                    updateSystematicsFromWebservice();
                }
                if(!isThesaurusInitialized()) {
                    updateThesaurusFromWebservice();
                }
            } else if(false /*no internet connection*/) {
                informSubscribersAboutSystematicsUpdateError(null);
                informSubscribersAboutThesaurusUpdateError(null);
            }
        }
    }

    /*
    Broadcast mechanism
    (objects can subscribe for data-changes)
     */
    private ArrayList<DataDispatcherBroadcastSubscriber> mSubscribers;

    public void subscribe(DataDispatcherBroadcastSubscriber subscriber) {
        if(!mSubscribers.contains(subscriber)) {
            mSubscribers.add(subscriber);
        }
    }

    public void unsubscribe(DataDispatcherBroadcastSubscriber subscriber) {
        if(mSubscribers.contains(subscriber)) {
            mSubscribers.remove(subscriber);
        }
    }

    private void informSubscribersAboutThesaurusChange() {
        for(DataDispatcherBroadcastSubscriber subscriber : mSubscribers) {
            subscriber.dataDispatcherThesaurusChanged();
        }
    }

    private void informSubscribersAboutSystematicsChange() {
        for(DataDispatcherBroadcastSubscriber subscriber : mSubscribers) {
            subscriber.dataDispatcherSystematicsChanged();
        }
    }

    private void informSubscribersAboutThesaurusUpdateError(Exception ex) {
        for(DataDispatcherBroadcastSubscriber subscriber : mSubscribers) {
            subscriber.dataDispatcherThesaurusUpdateError(ex);
        }
    }

    private void informSubscribersAboutSystematicsUpdateError(Exception ex) {
        for(DataDispatcherBroadcastSubscriber subscriber : mSubscribers) {
            subscriber.dataDispatcherSystematicsUpdateError(ex);
        }
    }

    public static interface DataDispatcherBroadcastSubscriber {
        public void dataDispatcherThesaurusChanged();
        public void dataDispatcherSystematicsChanged();

        public void dataDispatcherThesaurusUpdateError(Exception ex);
        public void dataDispatcherSystematicsUpdateError(Exception ex);
    }

    /*
    Observations
     */

    /*
    Thesaurus
     */
    private WebfaunaRealm mIdentificationMethodRealm;
    private WebfaunaRealm mPrecisionRealm;
    private WebfaunaRealm mEnvironmentRealm;
    private WebfaunaRealm mMilieuRealm;
    private WebfaunaRealm mStructureRealm;
    private WebfaunaRealm mSubstratRealm;

    public WebfaunaRealm getIdentificationMethodRealm() {
        return mIdentificationMethodRealm;
    }

    public WebfaunaRealm getPrecisionRealm() {
        return mPrecisionRealm;
    }

    public WebfaunaRealm getEnvironmentRealm() {
        return mEnvironmentRealm;
    }

    public WebfaunaRealm getMilieuRealm() {
        return mMilieuRealm;
    }

    public WebfaunaRealm getStructureRealm() {
        return mStructureRealm;
    }

    public WebfaunaRealm getSubstratRealm() {
        return mSubstratRealm;
    }

    /**
     *
     * @return true if DataDispatcher contains thesaurus-data
     */
    public boolean isThesaurusInitialized() {
        if(mIdentificationMethodRealm != null && mPrecisionRealm != null && mEnvironmentRealm != null && mMilieuRealm != null &&
                mStructureRealm != null && mSubstratRealm != null) {
            return true;
        } else {
            return false;
        }
    }

    /*get data*/
    public void updateThesaurusFromWebservice() {
        GetThesaurusAsyncTask asyncTask = new GetThesaurusAsyncTask(this,"en");
        asyncTask.execute();
    }

    private void getThesaurusFromDB() {

    }

    /*GetThesaurusAsyncTask.Callback*/

    @Override
    public void gotThesaurus(WebfaunaRealm identificationMethodRealm, WebfaunaRealm precisionRealm, WebfaunaRealm environmentRealm, WebfaunaRealm milieuRealm,
                             WebfaunaRealm structureRealm, WebfaunaRealm substratRealm) {

        mIdentificationMethodRealm = identificationMethodRealm;
        mPrecisionRealm = precisionRealm;
        mEnvironmentRealm = environmentRealm;
        mMilieuRealm = milieuRealm;
        mStructureRealm = structureRealm;
        mSubstratRealm = substratRealm;

        informSubscribersAboutThesaurusChange();
    }

    @Override
    public void couldNotGetThesaurus(Exception ex) {
        informSubscribersAboutThesaurusUpdateError(ex);
    }

    /*
            Systematics
             */
    private ArrayList<WebfaunaGroup> mWebfaunaGroups;
    /**
     * key: webfauna.restID
     * */
    private HashMap<String,ArrayList<WebfaunaSpecies>> mWebfaunaSpeciesOfGroups;

    public ArrayList<WebfaunaGroup> getWebfaunaGroups() {
        return mWebfaunaGroups;
    }

    public WebfaunaGroup getWebfaunaGroup(String groupRestID) {
        WebfaunaGroup returnObject = null;

        if(groupRestID != null && groupRestID != "") {
            for (WebfaunaGroup webfaunaGroup : mWebfaunaGroups) {
                if (webfaunaGroup.getRestID() == groupRestID) {
                    returnObject = webfaunaGroup;
                    break;
                }
            }
        }

        return returnObject;
    }

    public ArrayList<WebfaunaSpecies> getSpecies(String groupRestID) {
        ArrayList<WebfaunaSpecies> returnSpecies = null;

        if(groupRestID != null && groupRestID != "") {
            if (mWebfaunaSpeciesOfGroups.containsKey(groupRestID)) {
                returnSpecies = mWebfaunaSpeciesOfGroups.get(groupRestID);
            }
        }
        return returnSpecies;
    }

    public WebfaunaSpecies getSpecies(String groupRestID, String speciesRestID) {
        WebfaunaSpecies returnSpecies = null;

        if(groupRestID != null && groupRestID != "" && speciesRestID != null && speciesRestID != "") {
            ArrayList<WebfaunaSpecies> webfaunaSpeciesesArray = getSpecies(groupRestID);
            if(webfaunaSpeciesesArray != null){
                for(WebfaunaSpecies webfaunaSpecies:webfaunaSpeciesesArray) {
                    if(webfaunaSpecies.getRestID() == speciesRestID) {
                        returnSpecies = webfaunaSpecies;
                        break;
                    }
                }
            }
        }

        return returnSpecies;
    }

    /*get data*/
    public void updateSystematicsFromWebservice() {
        GetSystematicsAsyncTask asyncTask = new GetSystematicsAsyncTask(this);
        asyncTask.execute();
    }

    private void getSystematicsFromDB() {

    }

    /**
     *
     * @return true if DataDispatcher contains Systematics-data / else false
     */
    public boolean isSystematicsInitialized() {
        if(!(mWebfaunaGroups != null && mWebfaunaGroups.size() > 0)) {
            return false;
        } else {
            return true;
        }
    }

    /*GetSystematicsAsyncTask.Callback*/
    public void gotSystematics(ArrayList<WebfaunaGroup> webfaunaGroups, HashMap<String,ArrayList<WebfaunaSpecies>> speciesOfGroups) {
        mWebfaunaGroups = webfaunaGroups;
        mWebfaunaSpeciesOfGroups = speciesOfGroups;

        informSubscribersAboutSystematicsChange();
    }

    public void couldNotGetSystematics(Exception ex) {
        informSubscribersAboutSystematicsUpdateError(ex);
    }
}
