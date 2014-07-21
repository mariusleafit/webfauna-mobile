package ch.leafit.webfauna.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import ch.leafit.webfauna.data.db.models.*;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marius on 15/07/14.
 */
public class DBManager extends SQLiteOpenHelper{

    /*
    Management Code
     */

    // Logcat tag
    private static final String LOG = "DBManager";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "webfauna";

    // Table Names
    private static final String TABLE_GROUP = "tbl_group";
    private static final String TABLE_SPECIES = "tbl_species";
    private static final String TABLE_REALM = "tbl_realm";
    private static final String TABLE_REALM_VALUE = "tbl_realmValue";
    private static final String TABLE_OBSERVATION = "tbl_observation";
    private static final String TABLE_OBSERVATION_FILE = "tbl_observationFile";


    // Common column names
    private static final String KEY_REST_ID = "restID";
    private static final String KEY_JSON = "json";
    private static final String KEY_PARENT_REST_ID = "parentRestID";
    private static final String KEY_GUID = "guid";


    // Group Table - column names: restID,json

    // Species Table - column names: restID, parentRestID, json

    // REALM Table - column names: restID, json

    // ReamValue Table - column names: restID, parentRestID, json

    // Observation Table - column names: guid, json

    // ObservationFile Table - column names: guid, observationGUID, data, type
    private static final String OBSERVATION_FILE_KEY_OBSERVATION_GUID = "observationGUID";
    private static final String OBSERVATION_FILE_KEY_TYPE = "type";
    private static final String OBSERVATION_FILE_KEY_DATA = "data";

    // Table Create Statements
    // group table create statement
    private static final String CREATE_TABLE_GROUP = "CREATE TABLE "
            + TABLE_GROUP + "(" + KEY_REST_ID + " TEXT," + KEY_JSON
            + " TEXT)";

    // Species table create statement
    private static final String CREATE_TABLE_SPECIES = "CREATE TABLE " + TABLE_SPECIES
            + "(" + KEY_REST_ID + " TEXT," + KEY_PARENT_REST_ID + " TEXT,"
            + KEY_JSON + " TEXT)";

    // REALM table create statement
    private static final String CREATE_TABLE_REALM = "CREATE TABLE "
            + TABLE_REALM + "(" + KEY_REST_ID + " TEXT," + KEY_JSON
            + " TEXT)";

    // REALM_VALUE table create statement
    private static final String CREATE_TABLE_REALM_VALUE = "CREATE TABLE " + TABLE_REALM_VALUE
            + "(" + KEY_REST_ID + " TEXT," + KEY_PARENT_REST_ID + " TEXT,"
            + KEY_JSON + " TEXT)";

    // OBSERVATION table create statement
    private static final String CREATE_TABLE_OBSERVATION = "CREATE TABLE " + TABLE_OBSERVATION
            + "(" + KEY_GUID + " TEXT," + KEY_JSON + " TEXT)";

    // OBSERVATION_FILE table create statement
    private static final String CREATE_TABLE_OBSERVATION_FILE = "CREATE TABLE " + TABLE_OBSERVATION_FILE
            + "(" + KEY_GUID + " TEXT," + OBSERVATION_FILE_KEY_OBSERVATION_GUID + " TEXT,"
            + OBSERVATION_FILE_KEY_DATA + " BLOB," + OBSERVATION_FILE_KEY_TYPE + " INTEGER)";

    public DBManager(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating required tablse
        db.execSQL(CREATE_TABLE_GROUP);
        db.execSQL(CREATE_TABLE_SPECIES);
        db.execSQL(CREATE_TABLE_REALM);
        db.execSQL(CREATE_TABLE_REALM_VALUE);
        db.execSQL(CREATE_TABLE_OBSERVATION);
        db.execSQL(CREATE_TABLE_OBSERVATION_FILE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop older table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SPECIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REALM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REALM_VALUE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OBSERVATION_FILE);

        //create new table
        onCreate(db);
    }

    /*
    CRUD Methods
     */

    /*
    *
    *    Systematics ( Group & Species) ******************************************************
    *
    */

    /*create one*/
    public void createGroup(DBGroup dbGroup) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REST_ID, dbGroup.getRestID());
        values.put(KEY_JSON, dbGroup.getJSON());

        //insert row
        db.insert(TABLE_GROUP,null,values);
    }

    public void createSpecies(DBSpecies dbSpecies) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REST_ID, dbSpecies.getRestID());
        values.put(KEY_PARENT_REST_ID, dbSpecies.getParentRestID());
        values.put(KEY_JSON, dbSpecies.getJSON());

        //insert row
        db.insert(TABLE_SPECIES,null,values);
    }

    /*create multiple*/
    public void createGroups(List<DBGroup> dbGroups) {
        for(DBGroup dbGroup: dbGroups) {
            createGroup(dbGroup);
        }
    }

    public void createSpecies(List<DBSpecies> dbSpecieses) {
        for(DBSpecies dbSpecies: dbSpecieses) {
            createSpecies(dbSpecies);
        }
    }

    /*get all*/
    public List<DBGroup> getGroups() {
        List<DBGroup> groups = new ArrayList<DBGroup>();
        String selectQuery = "SELECT * FROM " + TABLE_GROUP;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows
        if(c.moveToFirst()) {
            do{
                DBGroup group = new DBGroup();
                group.setRestID(c.getString(c.getColumnIndex(KEY_REST_ID)));
                group.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));

                groups.add(group);
            } while(c.moveToNext());
        }

        return groups;
    }

    public List<DBSpecies> getSpecies() {
        List<DBSpecies> specieses = new ArrayList<DBSpecies>();
        String selectQuery = "SELECT * FROM " + TABLE_SPECIES;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows
        if(c.moveToFirst()) {
            do{
                DBSpecies species = new DBSpecies();
                species.setRestID(c.getString(c.getColumnIndex(KEY_REST_ID)));
                species.setParentRestID(c.getString(c.getColumnIndex(KEY_PARENT_REST_ID)));
                species.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));

                specieses.add(species);
            } while(c.moveToNext());
        }

        return specieses;
    }

    public List<DBSpecies> getSpecies(String groupRestID) {
        List<DBSpecies> specieses = new ArrayList<DBSpecies>();
        String selectQuery = "SELECT * FROM " + TABLE_SPECIES
                +" WHERE " + KEY_PARENT_REST_ID + " = '" + groupRestID +"'";

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows
        if(c.moveToFirst()) {
            do{
                DBSpecies species = new DBSpecies();
                species.setRestID(c.getString(c.getColumnIndex(KEY_REST_ID)));
                species.setParentRestID(c.getString(c.getColumnIndex(KEY_PARENT_REST_ID)));
                species.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));

                specieses.add(species);
            } while(c.moveToNext());
        }

        return specieses;
    }

    /*delete all*/
    public void deleteAllGroups() {
        String deleteQuery = "DELETE FROM " + TABLE_GROUP;

        Log.d(LOG,deleteQuery);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteQuery);
    }

    public void deleteAllSpecies() {
        String deleteQuery = "DELETE FROM " + TABLE_SPECIES;

        Log.d(LOG,deleteQuery);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteQuery);
    }

    /*
    *
    *    Thesaurus (REALM, REALM_VALUE) **************************************************
    *
    */

    /*create one*/
    public void createRealm(DBRealm dbRealm) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REST_ID, dbRealm.getRestID());
        values.put(KEY_JSON, dbRealm.getJSON());

        //insert row
        db.insert(TABLE_REALM,null,values);
    }

    public void createRealmValue(DBRealmValue dbRealmValue) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REST_ID, dbRealmValue.getRestID());
        values.put(KEY_PARENT_REST_ID, dbRealmValue.getParentRestID());
        values.put(KEY_JSON, dbRealmValue.getJSON());

        //insert row
        db.insert(TABLE_REALM_VALUE,null,values);
    }

    /*create multiple*/
    public void createRealms(List<DBRealm> dbRealms) {
        for(DBRealm dbRealm: dbRealms) {
            createRealm(dbRealm);
        }
    }

    public void createRealmValues(List<DBRealmValue> dbRealmValues) {
        for(DBRealmValue dbRealmValue: dbRealmValues) {
            createRealmValue(dbRealmValue);
        }
    }

    /*get all*/
    public List<DBRealm> getRealms() {
        List<DBRealm> realms = new ArrayList<DBRealm>();
        String selectQuery = "SELECT * FROM " + TABLE_REALM;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows
        if(c.moveToFirst()) {
            do{
                DBRealm realm = new DBRealm();
                realm.setRestID(c.getString(c.getColumnIndex(KEY_REST_ID)));
                realm.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));

                realms.add(realm);
            } while(c.moveToNext());
        }

        return realms;
    }

    public List<DBRealmValue> getRealmValues() {
        List<DBRealmValue> realmValues = new ArrayList<DBRealmValue>();
        String selectQuery = "SELECT * FROM " + TABLE_REALM_VALUE;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows
        if(c.moveToFirst()) {
            do{
                DBRealmValue realmValue = new DBRealmValue();
                realmValue.setRestID(c.getString(c.getColumnIndex(KEY_REST_ID)));
                realmValue.setParentRestID(c.getString(c.getColumnIndex(KEY_PARENT_REST_ID)));
                realmValue.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));

                realmValues.add(realmValue);
            } while(c.moveToNext());
        }

        return realmValues;
    }

    public List<DBRealmValue> getRealmValues(String realmRestID) {
        List<DBRealmValue> realmValues = new ArrayList<DBRealmValue>();
        String selectQuery = "SELECT * FROM " + TABLE_REALM_VALUE
                +" WHERE " + KEY_PARENT_REST_ID + " = '" + realmRestID + "'";

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows
        if(c.moveToFirst()) {
            do{
                DBRealmValue realmValue = new DBRealmValue();
                realmValue.setRestID(c.getString(c.getColumnIndex(KEY_REST_ID)));
                realmValue.setParentRestID(c.getString(c.getColumnIndex(KEY_PARENT_REST_ID)));
                realmValue.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));

                realmValues.add(realmValue);
            } while(c.moveToNext());
        }

        return realmValues;
    }

    /*delete all*/
    public void deleteAllRealms() {
        String deleteQuery = "DELETE FROM " + TABLE_REALM;

        Log.d(LOG,deleteQuery);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteQuery);
    }

    public void deleteAllRealmValues() {
        String deleteQuery = "DELETE FROM " + TABLE_REALM_VALUE;

        Log.d(LOG,deleteQuery);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteQuery);
    }

   /*
    *
    *  Observation ***********************************************************************
    *
    */

    public void createObservation(DBObservation dbObservation) {
        SQLiteDatabase db = getWritableDatabase();

        //generate guid if necessary
        String observationGUID = null;
        if(dbObservation.getGUID() == null || dbObservation.getGUID().equals("")) {
            observationGUID = UUID.randomUUID().toString();
        } else {
            observationGUID = dbObservation.getGUID();
        }

        ContentValues values = new ContentValues();
        values.put(KEY_GUID, observationGUID);
        values.put(KEY_JSON, dbObservation.getJSON());

        //insert row
        db.insert(TABLE_OBSERVATION,null,values);
    }

    public void editObservation(DBObservation dbObservation) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_JSON, dbObservation.getJSON());

        // updating row
        db.update(TABLE_OBSERVATION, values, KEY_GUID + " = ?", new String[] { dbObservation.getGUID() });
    }

    public List<DBObservation> getObservations() {
        List<DBObservation> observations = new ArrayList<DBObservation>();
        String selectQuery = "SELECT * FROM " + TABLE_OBSERVATION;

        Log.d(LOG, selectQuery);

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows
        if(c.moveToFirst()) {
            do{
                DBObservation observation = new DBObservation();
                observation.setGUID(c.getString(c.getColumnIndex(KEY_GUID)));
                observation.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));

                observations.add(observation);
            } while(c.moveToNext());
        }

        return observations;
    }

    public DBObservation getObservation(String guid) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_OBSERVATION + " WHERE "
                + KEY_GUID + " = '" + guid + "'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null) {
            c.moveToFirst();

            DBObservation observation = new DBObservation();
            try {
                observation.setGUID(c.getString(c.getColumnIndex(KEY_GUID)));
                observation.setJSON(c.getString(c.getColumnIndex(KEY_JSON)));
            } catch (Exception ex) {
                Log.e(LOG,"Could not get observation", ex);
                observation = null;
            }

            return observation;
        } else {
            return null;
        }
    }

    public void deleteObservation(String guid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OBSERVATION, KEY_GUID + " = ?", new String[] { guid });
    }

    /*
    Observation Files
     */

    public void createObservationFile(ByteBuffer data, DBObservationFile.DBObservationFileType type, String observationGUID) {
        if(data != null && type != null && observationGUID != null) {
            //generate guid
            UUID fileGUID = UUID.randomUUID();

            DBObservationFile dbObservationFile = new DBObservationFile(fileGUID.toString(),observationGUID,data,type);

            //create observationFile
            SQLiteDatabase db = getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(KEY_GUID, dbObservationFile.getGUID());
            values.put(OBSERVATION_FILE_KEY_OBSERVATION_GUID, dbObservationFile.getObservationGUID());
            values.put(OBSERVATION_FILE_KEY_DATA, dbObservationFile.getData().array());
            values.put(OBSERVATION_FILE_KEY_TYPE, dbObservationFile.getType().getId());

            //insert row
            db.insert(TABLE_OBSERVATION_FILE,null,values);
        }
    }

    public List<DBObservationFile> getObservationFiles(String observationGUID) {
        List<DBObservationFile> observationFiles = new ArrayList<DBObservationFile>();

        String selectQuery = "SELECT  * FROM " + TABLE_OBSERVATION_FILE
                + " WHERE " + OBSERVATION_FILE_KEY_OBSERVATION_GUID + " = '" + observationGUID + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                DBObservationFile observationFile = new DBObservationFile();
                observationFile.setGUID(c.getString(c.getColumnIndex(KEY_GUID)));
                observationFile.setObservationGUID(c.getString(c.getColumnIndex(OBSERVATION_FILE_KEY_OBSERVATION_GUID)));

                // set data
                byte[] dataByteArray = c.getBlob(c.getColumnIndex(OBSERVATION_FILE_KEY_DATA));
                if(dataByteArray != null && dataByteArray.length > 0) {
                    observationFile.setData(ByteBuffer.wrap(dataByteArray));
                }


                //set type
                int typeId = c.getInt(c.getColumnIndex(OBSERVATION_FILE_KEY_TYPE));
                observationFile.setType(DBObservationFile.DBObservationFileType.getType(typeId));

                observationFiles.add(observationFile);
            } while (c.moveToNext());
        }

        return observationFiles;
    }

    public void deleteObservationFile(String fileGUID) {
        String deleteQuery = "DELETE FROM " + TABLE_OBSERVATION_FILE
                + " WHERE " + KEY_GUID + " = '" + fileGUID + "'";

        Log.d(LOG,deleteQuery);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteQuery);
    }

    public void deleteObservationFiles(String observationGUID) {
        String deleteQuery = "DELETE FROM " + TABLE_OBSERVATION_FILE
                + " WHERE " + OBSERVATION_FILE_KEY_OBSERVATION_GUID + " = '" + observationGUID + "'";

        Log.d(LOG,deleteQuery);

        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(deleteQuery);
    }
}