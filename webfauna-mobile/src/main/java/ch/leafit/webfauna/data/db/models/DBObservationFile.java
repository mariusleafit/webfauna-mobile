package ch.leafit.webfauna.data.db.models;

import android.util.SparseArray;

import java.nio.ByteBuffer;

/**
 * Created by marius on 17/07/14.
 */
public class DBObservationFile {
    String mGUID;
    String mObservationGUID;
    ByteBuffer mData;
    DBObservationFileType mType;

    public DBObservationFile(String guid, String observationGUID, ByteBuffer data, DBObservationFileType type) {
        mGUID = guid;
        mObservationGUID = observationGUID;
        mData = data;
        mType = type;
    }

    public DBObservationFile() {}

    public String getGUID() {
        return mGUID;
    }
    public void setGUID(String guid) {
        mGUID = guid;
    }

    public String getObservationGUID() {
        return mObservationGUID;
    }
    public void setObservationGUID(String observationGUID) {
        mObservationGUID = observationGUID;
    }


    public ByteBuffer getData() {
        return mData;
    }
    public void setData(ByteBuffer data) {
        mData = data;
    }

    public DBObservationFileType getType() {
        return mType;
    }
    public void setType(DBObservationFileType type) {
        mType = type;
    }


    public static enum DBObservationFileType {
        Image(0);

        private int mId;

        private DBObservationFileType(int id) {
            mId = id;
        }

        /*mapping*/

        private static SparseArray<DBObservationFileType> idToTypeMaping;

        public static DBObservationFileType getType(int id) {
            if(idToTypeMaping == null) {
                initializeMapping();
            }

            return idToTypeMaping.get(id);
        }

        private static void initializeMapping(){
            idToTypeMaping = new SparseArray<DBObservationFileType>();

            for(DBObservationFileType type: values()) {
                idToTypeMaping.put(type.mId, type);
            }
        }

        /*getters*/
        public int getId() {
            return mId;
        }
     }
}
