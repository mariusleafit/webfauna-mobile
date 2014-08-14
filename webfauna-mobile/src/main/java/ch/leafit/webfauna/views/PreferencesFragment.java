package ch.leafit.webfauna.views;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ch.leafit.gdc.*;
import ch.leafit.gdc.callback.GDCClickDataFieldCallback;
import ch.leafit.ul.activities.intent_datastores.ULListActivityReturnIntentDatastore;
import ch.leafit.ul.list_items.ULOneFieldListItemModel;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.data.settings.SettingsManager;
import ch.leafit.webfauna.models.*;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by marius on 08/07/14.
 */
public class PreferencesFragment extends BaseFragment implements LoginDialogFragment.Callback{
    public static final String TAG = "PreferencesFragment";

    private  static final int login_data_field_id = 0;
    private  static final int locale_data_field_id = 0;


    /*UI-elements*/
    protected ListView mListView;

    /*list-stuff*/
    protected GDCListAdapter mListAdapter;

    /*ListElemetns*/
    GDCClickDataField mLoginDataField;
    GDCListDataField mLocaleDataField;

    LoginDialogFragment mLoginDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.preferences_fragment,null);

        /*
        get UI-elements
         */
        mListView = (ListView)contentView.findViewById(R.id.lstMain);
        mListView.setItemsCanFocus(true);

        createMenu();

        return contentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //get received data
        ULListActivityReturnIntentDatastore datastore = new ULListActivityReturnIntentDatastore(data);
        switch (requestCode) {
            case locale_data_field_id:{
                if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                    if (datastore.mSelectedItems.get(0) instanceof LocaleListEntry) {
                        LocaleListEntry localeListEntry = (LocaleListEntry)datastore.mSelectedItems.get(0);
                        Locale selectedLocale = localeListEntry.getLocale();

                        mLocaleDataField.setCurrentSelection(localeListEntry);

                        SettingsManager.getInstance().setLocale(selectedLocale);
                    }
                }
            }
        }
    }

    /*LoginDialogFragment.Callback*/

    @Override
    public void logIn(WebfaunaUser user) {
        mLoginDataField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_VALID);
    }

    @Override
    public void logOut() {
        mLoginDataField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
    }

    protected void createMenu() {
        ArrayList<GDCDataField> dataFields = new ArrayList<GDCDataField>();

        Resources res = getResources();

        mLoginDataField = new GDCClickDataField(getActivity(),login_data_field_id,res.getString(R.string.prefenrences_login_field_title),new GDCClickDataFieldCallback() {
            @Override
            public void fieldClicked(int tag) {
                if(mLoginDialog == null || (mLoginDialog != null && !mLoginDialog.isVisible())) {
                    mLoginDialog = new LoginDialogFragment();
                    mLoginDialog.show(getChildFragmentManager(), LoginDialogFragment.TAG);
                }
            }
        });
        dataFields.add(mLoginDataField);


        //get list entries
        ArrayList<LocaleListEntry> localeList= new ArrayList<LocaleListEntry>();
        for(Locale locale : SettingsManager.getInstance().getSupportedLocales()) {
            try {
                localeList.add(new LocaleListEntry(locale));
            } catch (Exception e) {
            }
        }
        ArrayList<ULOneFieldListItemModel> localeListEntries = ULOneFieldListItemModel.getListWithItemData(localeList);

        //get default value
        LocaleListEntry defaultLocale = null;
        try {
            defaultLocale = new LocaleListEntry(SettingsManager.getInstance().getLocale());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLocaleDataField = new GDCListDataField(getActivity(),locale_data_field_id,res.getString(R.string.prefenrences_locale_field_title),localeListEntries,defaultLocale,false,ListView.CHOICE_MODE_SINGLE);


        dataFields.add(mLocaleDataField);

       /*create listadapter*/
        mListAdapter = new GDCListAdapter(dataFields);
        mListView.setAdapter(mListAdapter);
    }
}
