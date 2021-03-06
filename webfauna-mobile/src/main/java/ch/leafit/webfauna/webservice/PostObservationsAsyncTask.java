package ch.leafit.webfauna.webservice;

import android.os.AsyncTask;
import android.util.Log;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaObservation;
import ch.leafit.webfauna.models.WebfaunaObservationFile;

import java.util.ArrayList;

/**
 * Created by marius on 14/07/14.
 */
public class PostObservationsAsyncTask extends AsyncTask<Void,Void,Void> {
    private Callback mCallback;

    private Exception mException;

    private ArrayList<WebfaunaObservation> mObservationsToUpload;
    private ArrayList<WebfaunaObservation> mFailedObservations;

    private String mUsername;
    private String mPassword;

    public PostObservationsAsyncTask(Callback callback, ArrayList<WebfaunaObservation> observations, String username, String password) throws Exception{
        if(callback != null && observations != null) {
            mCallback = callback;
            mObservationsToUpload = observations;
            mFailedObservations = new ArrayList<WebfaunaObservation>();
            mUsername = username;
            mPassword = password;
        } else {
            throw new Exception("PostObsrvationAsyncTask: + Argument is null");
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {

        /*send request for each observation*/
        for(WebfaunaObservation observation: mObservationsToUpload) {
            if(observation != null) {
                OutParam<Exception> outEx = new OutParam<Exception>();
                String addedObservationRestID = WebfaunaWebserviceObservation.postObservationToWebservice(observation.getGUID().toString(),outEx, mUsername, mPassword);
                if(observation.getGUID() != null && addedObservationRestID != null) {
                    /*//set is online*/
                    //DataDispatcher.getInstantce().deleteObservation(observation.getGUID().toString());
                    observation.setIsOnline(true);
                    DataDispatcher.getInstantce().editObservation(observation);

                    //post images of posted observation
                    ArrayList<WebfaunaObservationFile> files = (ArrayList<WebfaunaObservationFile>)DataDispatcher.getInstantce().getObservationFiles(observation.getGUID().toString());
                    for(WebfaunaObservationFile file: files) {
                        OutParam<Exception> outExForFile = new OutParam<Exception>();

                        //retry once
                        if(!WebfaunaWebserviceObservationFile.postObservationFileToWebservice(file.getData(),addedObservationRestID, outExForFile, mUsername, mPassword))
                            WebfaunaWebserviceObservationFile.postObservationFileToWebservice(file.getData(),addedObservationRestID, outExForFile, mUsername, mPassword);

                        /*option: error handling if only file-upload failed */

                        if(outExForFile.getValue() != null) {
                            Log.e("PostObservationAsyncTask","Could not post image", outExForFile.getValue());
                        } else {

                            //DataDispatcher.getInstantce().deleteObservationFile(file.getGUID().toString());
                        }


                    }

                } else {
                    mException = outEx.getValue();
                    mFailedObservations.add(observation);
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
