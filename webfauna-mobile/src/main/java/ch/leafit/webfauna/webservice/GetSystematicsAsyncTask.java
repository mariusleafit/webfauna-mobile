package ch.leafit.webfauna.webservice;

import android.os.AsyncTask;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaSpecies;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marius on 09/07/14.
 */
public class GetSystematicsAsyncTask extends AsyncTask<Void,Void,Void> {

    private Callback mCallback;

    private Exception mException;

    private ArrayList<WebfaunaGroup> mWebfaunaGroups;
    private HashMap<String,ArrayList<WebfaunaSpecies>> mWebfaunaSpecies;

    public GetSystematicsAsyncTask(Callback callback) {
        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        mWebfaunaGroups = new ArrayList<WebfaunaGroup>();
        mWebfaunaSpecies = new HashMap<String, ArrayList<WebfaunaSpecies>>();

        /*loop through needed groups*/
        for(Config.NeededWebfaunaGroup neededWebfaunaGroup : Config.neededWebfaunaGroups) {
            OutParam<Exception> outEx = new OutParam<Exception>();

            /*download group*/
            WebfaunaGroup webfaunaGroup = WebfaunaWebserviceSystematics.getGroupFromWebservice(neededWebfaunaGroup.groupRestID,outEx);
            if(outEx.getValue() == null && webfaunaGroup != null) {
                webfaunaGroup.setLocalImageResID(neededWebfaunaGroup.localImageResId);
                mWebfaunaGroups.add(webfaunaGroup);

                /*download species*/
                ArrayList<WebfaunaSpecies> webfaunaSpecieses = WebfaunaWebserviceSystematics.getSpeciesOfGroupFromWebservice(neededWebfaunaGroup.groupRestID,outEx);
                if(outEx.getValue() == null && webfaunaSpecieses != null) {
                    mWebfaunaSpecies.put(neededWebfaunaGroup.groupRestID,webfaunaSpecieses);
                } else {
                    mException = outEx.getValue();
                    break;
                }

            } else {
                mException = outEx.getValue();
                break;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(mException == null && mWebfaunaGroups != null && mWebfaunaGroups.size() > 0 && mWebfaunaSpecies != null && mWebfaunaSpecies.size() > 0) {
            mCallback.gotSystematics(mWebfaunaGroups,mWebfaunaSpecies);
        } else {
            mCallback.couldNotGetSystematics(mException);
        }
    }


    public static interface Callback {
        public void gotSystematics(ArrayList<WebfaunaGroup> webfaunaGroups, HashMap<String,ArrayList<WebfaunaSpecies>> speciesOfGroups);

        public void couldNotGetSystematics(Exception ex);
    }
}
