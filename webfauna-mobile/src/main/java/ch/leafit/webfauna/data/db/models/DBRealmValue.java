package ch.leafit.webfauna.data.db.models;

/**
 * Created by marius on 17/07/14.
 */
public class DBRealmValue {
    String mRestID;
    String mParentRestID;
    String mJSON;

    public DBRealmValue(String restID, String parentRestID, String json) {
        mRestID = restID;
        mParentRestID = parentRestID;
        mJSON = json;
    }

    public DBRealmValue() {}

    public String getRestID() {
        return mRestID;
    }
    public void setRestID(String restID) {
        mRestID = restID;
    }

    public String getParentRestID() {
        return mParentRestID;
    }
    public void setParentRestID(String parentRestID) {
        mParentRestID = parentRestID;
    }

    public String getJSON() {
        return mJSON;
    }
    public void setJSON(String json) {
        mJSON = json;
    }
}
