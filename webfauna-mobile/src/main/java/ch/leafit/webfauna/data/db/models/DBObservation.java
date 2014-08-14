package ch.leafit.webfauna.data.db.models;

/**
 * Created by marius on 17/07/14.
 */
public class DBObservation {
    String mGUID;
    String mJSON;
    boolean mIsOnline;

    public DBObservation(String guid, String json, boolean isOnline) {
        mGUID = guid;
        mJSON = json;
        mIsOnline = isOnline;
    }

    public DBObservation() {}

    public String getGUID() {
        return mGUID;
    }

    public void setGUID(String guid) {
        mGUID = guid;
    }

    public String getJSON() {
        return mJSON;
    }
    public void setJSON(String json) {
        mJSON = json;
    }

    public boolean isOnline() {
        return mIsOnline;
    }

    public void setIsOnline(boolean isOnline) {
        mIsOnline = isOnline;
    }
}
