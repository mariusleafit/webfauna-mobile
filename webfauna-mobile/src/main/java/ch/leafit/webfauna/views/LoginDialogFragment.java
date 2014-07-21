package ch.leafit.webfauna.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import ch.leafit.gdc.*;
import ch.leafit.gdc.callback.GDCDataFieldCallback;
import ch.leafit.iac.BundleDatastore;
import ch.leafit.ul.activities.intent_datastores.ULListActivityReturnIntentDatastore;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.data.settings.SettingsManager;
import ch.leafit.webfauna.gdc.GDCCoordinatesDataField;
import ch.leafit.webfauna.models.WebfaunaRealmValue;
import ch.leafit.webfauna.models.WebfaunaUser;
import ch.leafit.webfauna.webservice.LoginAsyncTask;

import java.util.ArrayList;

/**
 * Created by marius on 18/07/14.
 */
public class LoginDialogFragment extends BaseDialogFragment implements LoginAsyncTask.Callback{

    public static final String TAG = "LoginDialogFragment";

    /*data field ids*/
    protected static final int email_data_field_id = 0;
    protected static final int password_data_field_id = 1;

    /*UI-elements*/
    protected ListView mListView;
    protected Button mBtnLogin;
    protected Button mBtnLogout;

    /*list-stuff*/
    protected GDCListAdapter mListAdapter;

    /*list-datafields*/
    GDCStringDataField mEmailField;
    GDCStringDataField mPasswordField;

    protected Callback mParentFragmentCallback;

    protected WebfaunaUser mUser;
    protected String mEmail;
    protected String mPassword;

    protected LoginAsyncTask mLoginTask;

    /*dialos*/
    protected ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.login_dialog_fragment,null);

        Resources res = getResources();
        getDialog().setTitle(res.getString(R.string.login_dialog_title));

        mListView = (ListView)contentView.findViewById(R.id.lstMain);
        mListView.setItemsCanFocus(true);

        mBtnLogin = (Button)contentView.findViewById(R.id.btnLogin);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //login
                if(mLoginTask == null) {
                    if(mEmail != null && !mEmail.equals("") && mPassword != null && !mPassword.equals("")) {
                        mLoginTask = new LoginAsyncTask(mEmail,mPassword,LoginDialogFragment.this);

                        /*
                        show progress dialog
                         */
                        showProgressDialog(getResources().getString(R.string.login_dialog_progress_dialog_title),
                                getResources().getString(R.string.login_dialog_progress_dialog_title));

                        mLoginTask.execute();
                    }
                }
            }
        });

        mBtnLogout = (Button)contentView.findViewById(R.id.btnLogout);
        mBtnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUser = null;
                mEmail = null;
                mPassword = null;
                mEmailField.setValue("");
                mPasswordField.setValue("");

                v.setVisibility(View.INVISIBLE);
                mBtnLogin.setVisibility(View.VISIBLE);
                mEmailField.setDisabled(false);
                mPasswordField.setDisabled(false);

                SettingsManager.getInstance().setUser(null);

                mParentFragmentCallback.logOut();
            }
        });

        /*try to get current user from settingsmanager*/
        mUser = SettingsManager.getInstance().getUser();

        createMenu();

        if(mUser == null) {
            mBtnLogout.setVisibility(View.INVISIBLE);
            mBtnLogin.setVisibility(View.VISIBLE);
        } else {
            mBtnLogout.setVisibility(View.VISIBLE);
            mBtnLogin.setVisibility(View.INVISIBLE);

            mEmailField.setDisabled(true);
            mPasswordField.setDisabled(true);
        }



        return contentView;
    }

    protected void showProgressDialog(String title,String message) {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mProgressDialog = ProgressDialog.show(getActivity(), title, message, true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideProgressDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        /*get parentFragment callback*/
        Fragment parentFragment = getParentFragment();
        if(parentFragment != null) {
            try {
                mParentFragmentCallback = (Callback) parentFragment;
            } catch (ClassCastException e) {
                throw new ClassCastException(parentFragment.toString() + " must implement LocationFragmentCallback");
            }
        }

        //lock side-menu
        mParentActivityCallback.lockSideMenu();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //unlock side-menu
        mParentActivityCallback.unlockSideMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get received data
        ULListActivityReturnIntentDatastore datastore = new ULListActivityReturnIntentDatastore(data);
        switch (requestCode) {

        }
    }

    /*LoginAsyncTask.Callback*/

    @Override
    public void loginError(Exception ex) {
        hideProgressDialog();

        /*error dialog*/
        Log.e("LoginDialogFragment","loginError",ex);

        Resources res = getResources();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(res.getString(R.string.login_dialog_error_dialog_message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        AlertDialog errorDialog = builder.create();
        errorDialog.show();
    }

    @Override
    public void loginSuccessful(WebfaunaUser user) {
        hideProgressDialog();
        mUser = user;

        mBtnLogout.setVisibility(View.VISIBLE);
        mBtnLogin.setVisibility(View.INVISIBLE);
        mEmailField.setDisabled(true);
        mPasswordField.setDisabled(true);

        SettingsManager.getInstance().setUser(user);

        mParentFragmentCallback.logIn(mUser);
    }

    protected void createMenu() {
        ArrayList<GDCDataField> dataFields = new ArrayList<GDCDataField>();

        Resources res = getResources();

        String emailDefaultValue = "";
        if(mUser != null)  {
            emailDefaultValue = mUser.getEmail();
        }
        mEmailField = new GDCStringDataField(getActivity(),email_data_field_id,res.getString(R.string.login_dialog_email_field_title),emailDefaultValue,
                new GDCDataFieldCallback<String>() {
                    @Override
                    public void valueChanged(int tag, String value) {
                        mEmail = value;
                    }
                }, GDCStringDataField.GDCStringDataFieldType.GDCStringDataFieldTypeEMAIL);

        String passwordDefaultValue = "";
        if(mUser != null) {
            passwordDefaultValue = mUser.getPassword();
        }
        mPasswordField = new GDCStringDataField(getActivity(),password_data_field_id,res.getString(R.string.login_dialog_password_field_title),passwordDefaultValue,
                new GDCDataFieldCallback<String>() {
                    @Override
                    public void valueChanged(int tag, String value) {
                        mPassword = value;
                    }
                }, GDCStringDataField.GDCStringDataFieldType.GDCStringDataFieldTypePASSWORD);

        dataFields.add(mEmailField);
        dataFields.add(mPasswordField);

        mListAdapter = new GDCListAdapter(dataFields);
        mListView.setAdapter(mListAdapter);
    }


    public static interface Callback {
        public void logIn(WebfaunaUser user);
        public void logOut();
    }
}
