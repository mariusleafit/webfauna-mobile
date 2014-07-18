package ch.leafit.webfauna.data.db.models;

/**
 * Created by marius on 17/07/14.
 */
public class DBRealm {
    String mRestID;
    String mJSON;

    public DBRealm(String restID, String json) {
        mRestID = restID;
        mJSON = json;
    }

    public DBRealm() {}

    public String getRestID() {
        return mRestID;
    }

    public void setRestID(String restID) {
        mRestID = restID;
    }

    public String getJSON() {
        return mJSON;
    }
    public void setJSON(String json) {
        mJSON = json;
    }
}
