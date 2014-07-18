package ch.leafit.webfauna.data.db;

import android.util.Log;
import ch.leafit.webfauna.data.db.models.*;
import ch.leafit.webfauna.models.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

/**
 * Created by marius on 17/07/14.
 */
public final class ModelMapper {

    /*Log-tag*/
    private static String LOG = "ModelMapper";

    /*
    Group
     */
    public static WebfaunaGroup getWebfaunaGroup(DBGroup dbGroup) {
        WebfaunaGroup webfaunaGroup = null;

        if(dbGroup != null && dbGroup.getJSON() != null && dbGroup.getRestID() != null) {
            try {
                JSONObject jsonObject = new JSONObject(dbGroup.getJSON());

                webfaunaGroup = new WebfaunaGroup(jsonObject);
            } catch (Exception e) {
                Log.e(LOG,"getWebfaunaGroup",e);
            }
        }

        return webfaunaGroup;
    }

    public static DBGroup getDBGroup(WebfaunaGroup webfaunaGroup) {
        DBGroup returnValue = null;
        if(webfaunaGroup != null) {
            try {
                JSONObject jsonData = webfaunaGroup.toJSON();
                returnValue = new DBGroup(webfaunaGroup.getRestID(),jsonData.toString());
            } catch (Exception e) {
                Log.e(LOG, "getDBGroup", e);
            }
        }

        return returnValue;
    }

    /*
    Species
     */

    public static WebfaunaSpecies getWebfaunaSpecies(DBSpecies dbSpecies) {
        WebfaunaSpecies returnValue = null;

        if(dbSpecies != null) {
            try {
                JSONObject jsonSpecies = new JSONObject(dbSpecies.getJSON());
                returnValue = new WebfaunaSpecies(jsonSpecies, dbSpecies.getParentRestID());
            } catch (Exception e) {
                Log.e(LOG, "getWebfaunaSpecies", e);
            }
        }

        return returnValue;
    }

    public static DBSpecies getDBSpecies(WebfaunaSpecies webfaunaSpecies) {
        DBSpecies returnValue = null;

        if(webfaunaSpecies != null) {
            try {
                JSONObject jsonData = webfaunaSpecies.toJSON();
                returnValue = new DBSpecies(webfaunaSpecies.getRestID(),webfaunaSpecies.getGroupRestID(),jsonData.toString());
            } catch (Exception e) {
                Log.e(LOG, "getDBSpecies", e);
            }
        }

        return returnValue;
    }

    /*
    Realm
     */
    public static WebfaunaRealm getWebfaunaRealm(DBRealm dbRealm) {
        WebfaunaRealm webfaunaRealm = null;

        if(dbRealm != null && dbRealm.getJSON() != null && dbRealm.getRestID() != null) {
            try {
                JSONObject jsonObject = new JSONObject(dbRealm.getJSON());

                webfaunaRealm = new WebfaunaRealm(jsonObject);
            } catch (Exception e) {
                Log.e(LOG,"getWebfaunaRealm",e);
            }
        }

        return webfaunaRealm;
    }

    public static DBRealm getDBRealm(WebfaunaRealm webfaunaRealm) {
        DBRealm returnValue = null;
        if(webfaunaRealm != null) {
            try {
                JSONObject jsonData = webfaunaRealm.toJSON();
                returnValue = new DBRealm(webfaunaRealm.getRestID(),jsonData.toString());
            } catch (Exception e) {
                Log.e(LOG, "getDBRealm", e);
            }
        }

        return returnValue;
    }

    /*
    RealmValue
     */
    public static WebfaunaRealmValue getWebfaunaRealmValue(DBRealmValue dbRealmValue) {
        WebfaunaRealmValue returnValue = null;

        if(dbRealmValue != null) {
            try {
                JSONObject jsonObject = new JSONObject(dbRealmValue.getJSON());
                returnValue = new WebfaunaRealmValue(jsonObject);
            } catch (Exception e) {
                Log.e(LOG, "getWebfaunaRealmValue", e);
            }
        }

        return returnValue;
    }

    public static DBRealmValue getDBRealmValue(WebfaunaRealmValue webfaunaRealmValue, String realmRestID) {
        DBRealmValue returnValue = null;

        if(webfaunaRealmValue != null) {
            try {
                JSONObject jsonData = webfaunaRealmValue.toJSON();
                returnValue = new DBRealmValue(webfaunaRealmValue.getRestID(),realmRestID,jsonData.toString());
            } catch (Exception e) {
                Log.e(LOG, "getDBRealmValue", e);
            }
        }

        return returnValue;
    }

    /*
    Observation
     */

    public static WebfaunaObservation getWebfaunaObservation(DBObservation dbObservation) {
        WebfaunaObservation returnValue = null;

        if(dbObservation != null) {
            try {
                JSONObject jsonObject = new JSONObject(dbObservation.getJSON());
                returnValue = new WebfaunaObservation(jsonObject);

                UUID guid = UUID.fromString(dbObservation.getGUID());
                returnValue.setGUID(guid);
            } catch (Exception e) {
                Log.e(LOG, "getWebfaunaObservation", e);
                returnValue = null;
            }
        }

        return returnValue;
    }

    public static DBObservation getDBObservation(WebfaunaObservation webfaunaObservation) {
        DBObservation returnValue = null;

        if(webfaunaObservation != null) {
            try {
                JSONObject jsonData = webfaunaObservation.toJSON();
                String guid = null;
                if(webfaunaObservation.getGUID() != null) {
                    guid = webfaunaObservation.getGUID().toString();
                }

                returnValue = new DBObservation(guid,jsonData.toString());
            } catch (Exception e) {
                Log.e(LOG, "getDBRealmValue", e);
            }
        }

        return returnValue;
    }

    /*
    ObservationFile
     */
    public static WebfaunaObservationFile getWebfaunaObservationFile(DBObservationFile dbObservationFile) {
        WebfaunaObservationFile returnValue = null;

        if(dbObservationFile != null) {
            try {
                UUID guid = UUID.fromString(dbObservationFile.getGUID());
                UUID observationGUID = UUID.fromString(dbObservationFile.getObservationGUID());

                returnValue = new WebfaunaObservationFile();
                returnValue.setGUID(guid);
                returnValue.setObservationGUID(observationGUID);
                returnValue.setData(dbObservationFile.getData());
                returnValue.setType(WebfaunaObservationFile.ObservationFileType.getType(dbObservationFile.getType().getId()));
            } catch (Exception e) {
                Log.e(LOG, "getWebfaunaObservationFile", e);
                returnValue = null;
            }
        }

        return returnValue;
    }

    public static DBObservationFile getDBObservationFile(WebfaunaObservationFile webfaunaObservationFile) {
        DBObservationFile returnValue = null;

        if(webfaunaObservationFile != null) {
            try{

                returnValue = new DBObservationFile(webfaunaObservationFile.getGUID().toString(),webfaunaObservationFile.getObservationGUID().toString(),
                    webfaunaObservationFile.getData(), DBObservationFile.DBObservationFileType.getType(webfaunaObservationFile.getType().getId()));
            } catch (Exception e) {
                Log.e(LOG, "getDBObservationFile", e);
            }
        }

        return returnValue;
    }

}
