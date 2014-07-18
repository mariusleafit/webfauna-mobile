package ch.leafit.webfauna.models;

import android.util.Log;
import android.util.SparseArray;
import ch.leafit.webfauna.data.DataDispatcher;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.UUID;

/**
 * Created by marius on 17/07/14.
 */
public class WebfaunaObservationFile  {
    UUID mGUID;
    UUID mObservationGUID;
    ByteBuffer mData;
    ObservationFileType mType;



    public UUID getGUID() {
        return mGUID;
    }
    public void setGUID(UUID guid) {
        mGUID = guid;
    }

    public UUID getObservationGUID() {
        return mObservationGUID;
    }
    public void setObservationGUID(UUID observationGUID) {
        mObservationGUID = observationGUID;
    }


    public ByteBuffer getData() {
        return mData;
    }
    public void setData(ByteBuffer data) {
        mData = data;
    }

    public ObservationFileType getType() {
        return mType;
    }
    public void setType(ObservationFileType type) {
        mType = type;
    }


    public static enum ObservationFileType {
        Image(0);

        private int mId;

        private ObservationFileType(int id) {
            mId = id;
        }

        /*mapping*/

        private static SparseArray<ObservationFileType> idToTypeMaping;

        public static ObservationFileType getType(int id) {
            if(idToTypeMaping == null) {
                initializeMapping();
            }

            return idToTypeMaping.get(id);
        }

        private static void initializeMapping(){
            idToTypeMaping = new SparseArray<ObservationFileType>();

            for(ObservationFileType type: values()) {
                idToTypeMaping.put(type.mId, type);
            }
        }

        /*getters*/
        public int getId() {
            return mId;
        }
    }
}
