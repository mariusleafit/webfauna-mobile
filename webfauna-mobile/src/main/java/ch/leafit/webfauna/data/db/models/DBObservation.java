package ch.leafit.webfauna.data.db.models;

/**
 * Created by marius on 17/07/14.
 */
public class DBObservation {
    String mGUID;
    String mJSON;

    public DBObservation(String guid, String json) {
        mGUID = guid;
        mJSON = json;
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
}
