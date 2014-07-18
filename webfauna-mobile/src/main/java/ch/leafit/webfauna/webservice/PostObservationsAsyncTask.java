package ch.leafit.webfauna.webservice;

import android.os.AsyncTask;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaObservation;
import ch.leafit.webfauna.models.WebfaunaSpecies;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by marius on 14/07/14.
 */
public class PostObservationsAsyncTask extends AsyncTask<Void,Void,Void> {
    private Callback mCallback;

    private Exception mException;

    private ArrayList<WebfaunaObservation> mObservationsToUpload;
    private ArrayList<WebfaunaObservation> mFailedObservations;

    public PostObservationsAsyncTask(Callback callback, ArrayList<WebfaunaObservation> observations) throws InvalidArgumentException{
        if(callback != null && observations != null) {
            mCallback = callback;
            mObservationsToUpload = observations;
            mFailedObservations = observations;
        } else {
            throw new InvalidArgumentException(new String[]{"PostObsrvationAsyncTask:","Argument is null"});
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        /*send request for each observation*/
        for(WebfaunaObservation observation: mObservationsToUpload) {
            if(observation != null) {
                OutParam<Exception> outEx = new OutParam<Exception>();
                if(observation.getGUID() != null && WebfaunaWebserviceObservation.postObservationToWebservice(observation.getGUID().toString(),outEx)) {
                    mFailedObservations.remove(observation);
                    /*delete from DataDispatcher*/
                    DataDispatcher.getInstantce().deleteObservation(observation.getGUID().toString());
                } else {
                    mException = outEx.getValue();
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mCallback.finishedObservationUpload(mFailedObservations,mException);
    }


    public static interface Callback {
        public void finishedObservationUpload(ArrayList<WebfaunaObservation> failedObservations, Exception ex);
    }
}
