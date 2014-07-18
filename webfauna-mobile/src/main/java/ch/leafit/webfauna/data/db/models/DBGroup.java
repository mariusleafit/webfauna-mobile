package ch.leafit.webfauna.data.db.models;

/**
 * Created by marius on 17/07/14.
 */
public class DBGroup {
    String mRestID;
    String mJSON;

    public DBGroup(String restID, String json) {
        mRestID = restID;
        mJSON = json;
    }

    public DBGroup() {}

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
