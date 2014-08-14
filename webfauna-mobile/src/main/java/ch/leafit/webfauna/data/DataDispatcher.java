package ch.leafit.webfauna.data;

import android.content.Context;
import ch.leafit.webfauna.Utils.NetworkManager;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.db.DBManager;
import ch.leafit.webfauna.data.db.ModelMapper;
import ch.leafit.webfauna.data.db.models.*;
import ch.leafit.webfauna.data.settings.SettingsManager;
import ch.leafit.webfauna.models.*;
import ch.leafit.webfauna.webservice.GetSystematicsAsyncTask;
import ch.leafit.webfauna.webservice.GetThesaurusAsyncTask;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by marius on 09/07/14.
 *
 * Central point for data interaction
 */
public final class DataDispatcher implements GetSystematicsAsyncTask.Callback, GetThesaurusAsyncTask.Callback, SettingsManager.SettingsManagerBroadcastSubscriber{

    private static Context sContext;

    /**
     * has to be called once in runtime before first usage (by WebfaunaApplication class)
     * @param context
     */
    public static void setContext(Context context) {
        sContext = context;
    }

    private DBManager dbManager;

    public DataDispatcher() {
        mSubscribers = new ArrayList<DataDispatcherBroadcastSubscriber>();

        mWebfaunaGroups = new ArrayList<WebfaunaGroup>();
        mWebfaunaSpeciesOfGroups = new HashMap<String, ArrayList<WebfaunaSpecies>>();

        dbManager = new DBManager(sContext);

        /*subscribe SettingsChanges*/
        SettingsManager.getInstance().subscribe(this);
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

            if(!isInitialized() && NetworkManager.getInstance().isConnected()) {
                if(NetworkManager.getInstance().isConnected()) {
                    if (!isSystematicsInitialized()) {
                        updateSystematicsFromWebservice();
                    }
                    if (!isThesaurusInitialized()) {
                        updateThesaurusFromWebservice();
                    }
                } else {
                    informSubscribersAboutSystematicsUpdateError(null);
                    informSubscribersAboutThesaurusUpdateError(null);
                }
            } else {
                informSubscribersAboutSystematicsChange();
                informSubscribersAboutThesaurusChange();
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

    public ArrayList<WebfaunaObservation> getObservations() {
        ArrayList<WebfaunaObservation> webfaunaObservations = new ArrayList<WebfaunaObservation>();

        List<DBObservation> dbObservations = dbManager.getObservations();
        for(DBObservation dbObservation: dbObservations) {
            WebfaunaObservation webfaunaObservation = ModelMapper.getWebfaunaObservation(dbObservation);
            if(webfaunaObservation != null) {
                webfaunaObservations.add(webfaunaObservation);
            }
        }
        return webfaunaObservations;
    }

    public WebfaunaObservation getObservation(String guid) {
        WebfaunaObservation returnObservation = null;

        DBObservation dbObservation = dbManager.getObservation(guid);
        if(dbObservation != null) {
            returnObservation = ModelMapper.getWebfaunaObservation(dbObservation);
        }

        return returnObservation;
    }

    public void addObservation(WebfaunaObservation observation) {

        if(observation.getGUID() == null)
            observation.setGUID(UUID.randomUUID());

        //delete existing observation
        dbManager.deleteObservation(observation.getGUID().toString());

        DBObservation dbObservation = ModelMapper.getDBObservation(observation);
        if(dbObservation != null) {
            dbManager.createObservation(dbObservation);
        }
    }

    public void editObservation(WebfaunaObservation observation) {
        if(observation.getGUID() != null) {
            DBObservation dbObservation = ModelMapper.getDBObservation(observation);
            if(dbObservation != null) {
                dbManager.editObservation(dbObservation);
            }
        } else {
            addObservation(observation);
        }
    }

    public void deleteObservation(String guid) {
        dbManager.deleteObservation(guid);
    }


    /*
    ObservationFiles
     */
    public List<WebfaunaObservationFile> getObservationFiles(String observationGUID) {
        List<WebfaunaObservationFile> returnFiles = new ArrayList<WebfaunaObservationFile>();

        List<DBObservationFile> dbFiles = dbManager.getObservationFiles(observationGUID);

        for(DBObservationFile dbFile : dbFiles) {
            WebfaunaObservationFile tmpFile = ModelMapper.getWebfaunaObservationFile(dbFile);
            if(tmpFile != null)
                returnFiles.add(tmpFile);
        }

        return returnFiles;
    }

    public void addObservationFile(WebfaunaObservationFile file) throws Exception{
        ByteBuffer data = file.getData();

        DBObservationFile.DBObservationFileType type = null;
        if(file.getType() != null)
            type = DBObservationFile.DBObservationFileType.getType(file.getType().getId());

        String observationGUID = null;
        if(file.getObservationGUID() != null)
            observationGUID = file.getObservationGUID().toString();

        if(file.getGUID() == null)
            file.setGUID(UUID.randomUUID());

        if(data != null && type != null && observationGUID != null && !observationGUID.equals(""))
            dbManager.createObservationFile(data,type,observationGUID);
        else
            throw new Exception("DataDispatcher-addObservationFile: could not add observationfiles (invalid params");
    }

    public void deleteObservationFile(String fileGUID) {
        dbManager.deleteObservationFile(fileGUID);
    }

    public void deleteObservationFiles(String observationGUID) {
        dbManager.deleteObservationFiles(observationGUID);
    }


    /*
    Thesaurus
     */

    private GetThesaurusAsyncTask getThesaurusAsyncTask;

    private WebfaunaRealm mIdentificationMethodRealm;
    private WebfaunaRealm mPrecisionRealm;
    private WebfaunaRealm mEnvironmentRealm;
    private WebfaunaRealm mMilieuRealm;
    private WebfaunaRealm mStructureRealm;
    private WebfaunaRealm mSubstratRealm;

    public WebfaunaRealm getIdentificationMethodRealm() {
        return mIdentificationMethodRealm;
    }

    /**
     *
     * @return IdentificationMethods applicable for a given WebfaunaGroup
     */
    public WebfaunaRealm getIdentificationMethodRealm(WebfaunaGroup group) {
        WebfaunaRealm returnRealm = null;

        if(mIdentificationMethodRealm != null && group != null) {
            returnRealm = new WebfaunaRealm(mIdentificationMethodRealm);

            //get applicable realmValues
            ArrayList<WebfaunaRealmValue> applicableRealmValues = new ArrayList<WebfaunaRealmValue>();
            for(WebfaunaRealmValue realmValue: mIdentificationMethodRealm.getRealmValues()) {
                if(group.getValidIdentificationMethodRestIDs().contains(realmValue.getRestID()))
                    applicableRealmValues.add(realmValue);
            }

            returnRealm.setRealmValues(applicableRealmValues);
        }

        return returnRealm;
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
        /*to avoid multiple executen at the same time*/
        if(getThesaurusAsyncTask == null) {

            String languageCode = "en";
            Locale locale = SettingsManager.getInstance().getLocale();
            if(locale != null) {
                languageCode = locale.getLanguage();
            }

            getThesaurusAsyncTask = new GetThesaurusAsyncTask(this,languageCode);
            getThesaurusAsyncTask.execute();
        }
    }

    private void getThesaurusFromDB() {

        /*get all realms & their values*/

        List<DBRealm> dbRealms = dbManager.getRealms();
        for(DBRealm dbRealm: dbRealms) {
            WebfaunaRealm webfaunaRealm = ModelMapper.getWebfaunaRealm(dbRealm);
            if(webfaunaRealm != null) {
                /*get realmvalues*/

                List<DBRealmValue> dbRealmValues = dbManager.getRealmValues(webfaunaRealm.getRestID());
                for(DBRealmValue dbRealmValue: dbRealmValues) {
                    WebfaunaRealmValue webfaunaRealmValue = ModelMapper.getWebfaunaRealmValue(dbRealmValue);
                    if(webfaunaRealmValue != null) {
                        webfaunaRealm.addRealmValue(webfaunaRealmValue);
                    }
                }

                /*save realm at the right place!!*/
                if(webfaunaRealm.getRestID().equals(Config.webfaunaIdentificationMethodRealmRestID)) {
                    mIdentificationMethodRealm = webfaunaRealm;
                } else if(webfaunaRealm.getRestID().equals(Config.webfaunaPrecisionRealmRestID)) {
                    mPrecisionRealm = webfaunaRealm;
                } else if(webfaunaRealm.getRestID().equals(Config.webfaunaEnvironmentRealmRestID)) {
                    mEnvironmentRealm = webfaunaRealm;
                } else if(webfaunaRealm.getRestID().equals(Config.webfaunaMilieuRealmRestID)) {
                    mMilieuRealm = webfaunaRealm;
                } else if(webfaunaRealm.getRestID().equals(Config.webfaunaStructureRealmRestID)) {
                    mStructureRealm = webfaunaRealm;
                } else if(webfaunaRealm.getRestID().equals(Config.webfaunaSubstratRealmRestID)) {
                    mSubstratRealm = webfaunaRealm;
                }
            }
        }
    }

    private void stockThesaurusInDB() {
        dbManager.deleteAllRealms();
        dbManager.deleteAllRealmValues();

        stockRealmInDB(mIdentificationMethodRealm);
        stockRealmInDB(mPrecisionRealm);
        stockRealmInDB(mEnvironmentRealm);
        stockRealmInDB(mMilieuRealm);
        stockRealmInDB(mStructureRealm);
        stockRealmInDB(mSubstratRealm);
    }

    private void stockRealmInDB(WebfaunaRealm webfaunaRealm) {
        //stock realm
        DBRealm dbRealm = ModelMapper.getDBRealm(webfaunaRealm);
        if(dbRealm != null) {
            dbManager.createRealm(dbRealm);

            //stock realmvalues
            for(WebfaunaRealmValue webfaunaRealmValue: webfaunaRealm.getRealmValues()) {
                DBRealmValue dbRealmValue = ModelMapper.getDBRealmValue(webfaunaRealmValue, webfaunaRealm.getRestID());
                if(dbRealmValue != null) {
                    dbManager.createRealmValue(dbRealmValue);
                }
            }
        }
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

        /*enable reexecution of task*/
        getThesaurusAsyncTask = null;

        stockThesaurusInDB();

        informSubscribersAboutThesaurusChange();
    }

    @Override
    public void couldNotGetThesaurus(Exception ex) {
        /*enable reexecution of task*/
        getThesaurusAsyncTask = null;

        informSubscribersAboutThesaurusUpdateError(ex);
    }

    /*
    Systematics
     */

    private GetSystematicsAsyncTask getSystematicsAsyncTask;

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

        if(groupRestID != null && !groupRestID.equals("")) {
            for (WebfaunaGroup webfaunaGroup : mWebfaunaGroups) {
                if (webfaunaGroup.getRestID().equals(groupRestID)) {
                    returnObject = webfaunaGroup;
                    break;
                }
            }
        }

        return returnObject;
    }

    public ArrayList<WebfaunaSpecies> getSpecies(String groupRestID) {
        ArrayList<WebfaunaSpecies> returnSpecies = null;

        if(groupRestID != null && !groupRestID.equals("")) {
            if (mWebfaunaSpeciesOfGroups.containsKey(groupRestID)) {
                returnSpecies = mWebfaunaSpeciesOfGroups.get(groupRestID);
            }
        }
        return returnSpecies;
    }

    public WebfaunaSpecies getSpecies(String groupRestID, String speciesRestID) {
        WebfaunaSpecies returnSpecies = null;

        if(groupRestID != null && !groupRestID.equals("") && speciesRestID != null && !speciesRestID.equals("")) {
            ArrayList<WebfaunaSpecies> webfaunaSpeciesesArray = getSpecies(groupRestID);
            if(webfaunaSpeciesesArray != null){
                for(WebfaunaSpecies webfaunaSpecies:webfaunaSpeciesesArray) {
                    if(webfaunaSpecies.getRestID().equals(speciesRestID)) {
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
        /*avoid multiple execution at the same time*/
        if(getSystematicsAsyncTask == null) {
            getSystematicsAsyncTask = new GetSystematicsAsyncTask(this);
            getSystematicsAsyncTask.execute();
        }
    }

    private void getSystematicsFromDB() {
        mWebfaunaSpeciesOfGroups = new HashMap<String, ArrayList<WebfaunaSpecies>>();
        mWebfaunaGroups = new ArrayList<WebfaunaGroup>();

        List<DBGroup> dbGroups = dbManager.getGroups();
        for(DBGroup dbGroup: dbGroups) {
            WebfaunaGroup webfaunaGroup = ModelMapper.getWebfaunaGroup(dbGroup);

            for(Config.NeededWebfaunaGroup group: Config.neededWebfaunaGroups ) {
                if(webfaunaGroup.getRestID().equals(group.groupRestID)) {
                    webfaunaGroup.setLocalImageResID(group.localImageResId);
                    break;
                }
            }

            if(webfaunaGroup != null) {
                mWebfaunaGroups.add(webfaunaGroup);

                //get species of group
                List<DBSpecies> dbSpecieses = dbManager.getSpecies(webfaunaGroup.getRestID());

                mWebfaunaSpeciesOfGroups.put(webfaunaGroup.getRestID(),new ArrayList<WebfaunaSpecies>());
                for(DBSpecies dbSpecies : dbSpecieses) {
                    WebfaunaSpecies webfaunaSpecies = ModelMapper.getWebfaunaSpecies(dbSpecies);
                    if(webfaunaSpecies != null) {
                        mWebfaunaSpeciesOfGroups.get(webfaunaGroup.getRestID()).add(webfaunaSpecies);
                    }
                }
            }
        }
    }

    private void stockSystematicsInDB() {
        if(mWebfaunaGroups != null && mWebfaunaGroups.size() > 0 && mWebfaunaSpeciesOfGroups != null) {
            dbManager.deleteAllGroups();
            dbManager.deleteAllSpecies();

            for(WebfaunaGroup webfaunaGroup: mWebfaunaGroups) {
                DBGroup dbGroup = ModelMapper.getDBGroup(webfaunaGroup);
                if(dbGroup != null) {
                    dbManager.createGroup(dbGroup);
                }
            }

            for(String groupRestID: mWebfaunaSpeciesOfGroups.keySet()) {
                for(WebfaunaSpecies webfaunaSpecies: mWebfaunaSpeciesOfGroups.get(groupRestID)) {
                    DBSpecies dbSpecies = ModelMapper.getDBSpecies(webfaunaSpecies);
                    if(dbSpecies != null) {
                        dbManager.createSpecies(dbSpecies);
                    }
                }
            }
        }
    }

    /**
     *
     * @return true if DataDispatcher contains Systematics-data / else false
     */
    public boolean isSystematicsInitialized() {
        if(mWebfaunaGroups == null || mWebfaunaGroups.size() == 0 || mWebfaunaSpeciesOfGroups == null || mWebfaunaSpeciesOfGroups.size() != mWebfaunaGroups.size()) {
            return false;
        } else {
            return true;
        }
    }

    /*GetSystematicsAsyncTask.Callback*/
    public void gotSystematics(ArrayList<WebfaunaGroup> webfaunaGroups, HashMap<String,ArrayList<WebfaunaSpecies>> speciesOfGroups) {
        mWebfaunaGroups = webfaunaGroups;
        mWebfaunaSpeciesOfGroups = speciesOfGroups;

        /*enable reexecution of the task*/
        getSystematicsAsyncTask = null;

        stockSystematicsInDB();

        informSubscribersAboutSystematicsChange();
    }

    public void couldNotGetSystematics(Exception ex) {

        /*enable reexecution of the task*/
        getSystematicsAsyncTask = null;

        informSubscribersAboutSystematicsUpdateError(ex);
    }

    /*SettingsManager.SettingsManagerBroadcastSubscriber*/

    @Override
    public void settingsLocaleChanged(Locale locale) {
        updateThesaurusFromWebservice();
    }

    @Override
    public void settingsUserChanged(WebfaunaUser user) {

    }
}
