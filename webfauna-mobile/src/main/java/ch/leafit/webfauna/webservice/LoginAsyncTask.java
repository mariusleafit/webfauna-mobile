package ch.leafit.webfauna.webservice;

import android.os.AsyncTask;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.models.WebfaunaUser;

/**
 * Created by marius on 18/07/14.
 */
public class LoginAsyncTask extends AsyncTask<Void,Void,Void>{

    private String mEmail;
    private String mPassword;

    private WebfaunaUser mUser;
    private Exception mException;

    private Callback mCallback;

    public LoginAsyncTask(String email, String password, Callback callback) {
        mEmail = email;
        mPassword = password;

        mCallback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        OutParam<Exception> outEx = new OutParam<Exception>();

        mUser = WebfaunaWebserviceLogin.checkLogin(mEmail,mPassword,outEx);
        mException = outEx.getValue();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(mUser == null || mException != null) {
            mCallback.loginError(mException);
        } else {
            mCallback.loginSuccessful(mUser);
        }
    }

    public static interface Callback {
        public void loginSuccessful(WebfaunaUser user);
        public void loginError(Exception ex);
    }
}
