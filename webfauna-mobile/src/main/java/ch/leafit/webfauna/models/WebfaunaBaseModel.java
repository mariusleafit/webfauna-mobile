package ch.leafit.webfauna.models;

import org.json.JSONObject;

/**
 * Created by marius on 09/07/14.
 */
public abstract class WebfaunaBaseModel{

    /**
     *
     * @param toCopy instance to copy
     */
    public WebfaunaBaseModel(WebfaunaBaseModel toCopy) {

    }

    public WebfaunaBaseModel(JSONObject jsonObject) {

    }

    protected WebfaunaBaseModel() {}

    /**
     *
     * @param jsonObject sets the members of the Object with data in the JSONObject
     */
    public abstract void putJSON(JSONObject jsonObject) throws Exception;

    /**
     *
     * @return JSONObject with the data of the object
     */
    public abstract JSONObject toJSON() throws Exception;
}
