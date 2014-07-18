package ch.leafit.webfauna.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.*;
import android.widget.ListView;
import ch.leafit.gdc.*;
import ch.leafit.gdc.callback.GDCClickDataFieldCallback;
import ch.leafit.gdc.callback.GDCDataFieldCallback;
import ch.leafit.ul.activities.intent_datastores.ULListActivityReturnIntentDatastore;
import ch.leafit.ul.list_items.ULListItemDataModel;
import ch.leafit.ul.list_items.ULListItemModel;
import ch.leafit.ul.list_items.ULOneFieldListItemModel;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.*;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by marius on 25/06/14.
 */
public class ObservationFragment extends BaseFragment implements LocationDialogFragment.Callback, DataDispatcher.DataDispatcherBroadcastSubscriber, AbundanceDialogFragment.Callback{

    public static final String TAG = "ObservationFragment";

    /**
     * observation, which the user is currently working on, but he probably left the Fragment during the process
     */
    protected static WebfaunaObservation sCurrentWebfaunaObservation;

    /*data field ids*/
    protected static final int date_data_field_id = 0;

    protected static final int location_data_field_id = 1;

    protected static final int files_data_field_id = 10;

    protected static final int group_data_field_id = 2;
    protected static final int species_data_field_id = 3;
    protected static final int observation_method_data_field_id = 4;
    protected static final int abundance_data_field_id = 5;

    protected static final int environment_data_field_id = 6;
    protected static final int milieu_data_field_id = 7;
    protected static final int structure_data_field_id = 8;
    protected static final int substrat_data_field_id = 9;

    protected WebfaunaObservation mCurrentWebfaunaObservation;
    protected boolean mIsInEditMode;

    /*UI-elements*/
    protected ListView mListView;

    /*list-stuff*/
    protected GDCListAdapter mListAdapter;

    /*list-datafields*/

    protected GDCClickDataField mLocationField;

    //species section
    protected GDCListDataField mGroupField;
    protected GDCListDataField mSpeciesField;
    protected GDCListDataField mObservationMethodField;
    protected GDCClickDataField mAbundanceField;

    //environment section
    protected GDCListDataField mEnvironmentField;
    protected GDCListDataField mMilieuField;
    protected GDCListDataField mStructureField;
    protected GDCListDataField mSubstratField;

    /*Datadispatcher stuff*/
    protected boolean mThesaurusUpToDate;
    protected boolean mSystematicsUpToDate;


    /*dialog fragments*/
    protected LocationDialogFragment mLocationDialogFragment;
    protected AbundanceDialogFragment mAbundanceDialogFragment;

    /*dialos*/
    protected ProgressDialog mProgressDialog;
    protected Dialog mAreYouSureDialog;
    protected Dialog mValidationDialog;

    protected boolean mDismissCurrentObservation = false;
    /**
     * creates either a new Observation or takes the observation of the static member sCurentObservation, which contains
     * the observation, the user is currently working on
     */
    public ObservationFragment() {
        if(sCurrentWebfaunaObservation != null) {
            mCurrentWebfaunaObservation = sCurrentWebfaunaObservation;
        } else {
            mCurrentWebfaunaObservation = new WebfaunaObservation();
        }

        /*subscribe for data changes*/
        DataDispatcher.getInstantce().subscribe(this);

        mIsInEditMode = false;
    }

    /**
     * Create ObservationFragment to edit an observation
     *
     * @param webfaunaObservation observation to Edit
     */
    public ObservationFragment(WebfaunaObservation webfaunaObservation) throws CloneNotSupportedException{
        mCurrentWebfaunaObservation = new WebfaunaObservation(webfaunaObservation);

        /*subscribe for data changes*/
        DataDispatcher.getInstantce().subscribe(this);

        mIsInEditMode = true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.observation_fragment,null);

        /*
        get UI-elements
         */
        mListView = (ListView)contentView.findViewById(R.id.lstMain);
        mListView.setItemsCanFocus(true);

        /*
        get data from datadispatcher if initialized
         */
        if(DataDispatcher.getInstantce().isInitialized()) {
            mThesaurusUpToDate = true;
            mSystematicsUpToDate = true;
            createMenu();
        } else {
            /*initialize datadispatcher*/
            mThesaurusUpToDate = false;
            mSystematicsUpToDate = false;

            Resources res = getResources();

            showProgressDialog(res.getString(R.string.observation_download_progress_dialog_title),res.getString(R.string.observation_download_progress_dialog_message));

            DataDispatcher.getInstantce().initialize();
        }

        return contentView;
    }

    @Override
    public void onDetach() {
        /*called when the fragment is removed*/
        super.onDetach();

        //cache currentObservation if necessary
        if(mCurrentWebfaunaObservation != null && !mDismissCurrentObservation && !mIsInEditMode) {
            sCurrentWebfaunaObservation = mCurrentWebfaunaObservation;
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.observation_fragment_actionbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_cancel:
                cancelObservation();
                return true;
            case R.id.action_save:
                saveObservation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    /*method gets called when user makes a selection in a GDCListView*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        /*if a dialog is visible*/
        if(mLocationDialogFragment != null && mLocationDialogFragment.isVisible()) {
            mLocationDialogFragment.onActivityResult(requestCode,resultCode,data);
        } else if(mAbundanceDialogFragment != null && mAbundanceDialogFragment.isVisible()) {
            mAbundanceDialogFragment.onActivityResult(requestCode,resultCode,data);
        } else {

            //get received data
            ULListActivityReturnIntentDatastore datastore = new ULListActivityReturnIntentDatastore(data);

            switch (requestCode) {
            /*species section*/
                case group_data_field_id:
                /*get selected group and set in observation*/
                    if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                        if (datastore.mSelectedItems.get(0) instanceof WebfaunaGroup) {
                            WebfaunaGroup selectedGroup = (WebfaunaGroup) datastore.mSelectedItems.get(0);
                            mCurrentWebfaunaObservation.setWebfaunaGroup(selectedGroup);

                        /*set text of GroupField*/
                            mGroupField.setValue(selectedGroup.getTitle());
                            mGroupField.setMarking(GDCDataField.GDCDataFieldMarking.NOT_MARKED);
                        /*add species of group to the speciesFIeld*/
                            if (mCurrentWebfaunaObservation.getWebfaunaGroup().getRestID() != null) {
                                ArrayList<? extends ULListItemDataModel> speciesOfGroup = DataDispatcher.getInstantce().getSpecies(mCurrentWebfaunaObservation.getWebfaunaGroup().getRestID());
                                if (speciesOfGroup != null) {
                                    ArrayList<ULOneFieldListItemModel> speciesListElements = ULOneFieldListItemModel.getListWithItemData(speciesOfGroup);
                                    mSpeciesField.setListItems(speciesListElements);
                                }
                                mSpeciesField.setDisabled(false);
                            } else {
                                mSpeciesField.setDisabled(true);
                            }

                            mCurrentWebfaunaObservation.setWebfaunaSpecies(null);
                            mSpeciesField.setValue("");
                            mSpeciesField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
                        }
                    }else {
                        mCurrentWebfaunaObservation.setWebfaunaGroup(null);
                        mGroupField.setValue("");
                        mGroupField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);

                        mCurrentWebfaunaObservation.setWebfaunaSpecies(null);
                        mSpeciesField.setValue("");
                        mSpeciesField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);

                        mSpeciesField.setDisabled(true);
                    }
                    break;
                case species_data_field_id:
                    if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                        if (datastore.mSelectedItems.get(0) instanceof WebfaunaSpecies) {
                            WebfaunaSpecies selectedSpecies = (WebfaunaSpecies) datastore.mSelectedItems.get(0);
                            mCurrentWebfaunaObservation.setWebfaunaSpecies(selectedSpecies);

                        /*set text of Species*/
                            mSpeciesField.setValue(selectedSpecies.getTitle());
                            mSpeciesField.setMarking(GDCDataField.GDCDataFieldMarking.NOT_MARKED);
                        }
                    } else {
                        mCurrentWebfaunaObservation.setWebfaunaSpecies(null);
                        mSpeciesField.setValue("");
                        mSpeciesField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
                    }
                    break;
                case observation_method_data_field_id:
                    if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                        if (datastore.mSelectedItems.get(0) instanceof WebfaunaRealmValue) {
                            WebfaunaRealmValue selectedRealmValue = (WebfaunaRealmValue) datastore.mSelectedItems.get(0);
                            mCurrentWebfaunaObservation.setIdentificationMethod(selectedRealmValue);

                        /*set text of Species*/
                            mObservationMethodField.setValue(selectedRealmValue.getTitle());
                            mObservationMethodField.setMarking(GDCDataField.GDCDataFieldMarking.NOT_MARKED);
                        }
                    } else {
                        mCurrentWebfaunaObservation.setIdentificationMethod(null);
                        mObservationMethodField.setValue("");
                        mObservationMethodField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
                    }
                    break;
            /*environment section*/
                case environment_data_field_id:
                    if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                        if (datastore.mSelectedItems.get(0) instanceof WebfaunaRealmValue) {
                            WebfaunaRealmValue selectedRealmValue = (WebfaunaRealmValue) datastore.mSelectedItems.get(0);
                            mCurrentWebfaunaObservation.setEnvironmentRealmValue(selectedRealmValue);

                        /*set text of Species*/
                            mEnvironmentField.setValue(selectedRealmValue.getTitle());
                        }
                    } else {
                        mCurrentWebfaunaObservation.setEnvironmentRealmValue(null);
                        mEnvironmentField.setValue("");
                    }
                    break;
                case milieu_data_field_id:
                    if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                        if (datastore.mSelectedItems.get(0) instanceof WebfaunaRealmValue) {
                            WebfaunaRealmValue selectedRealmValue = (WebfaunaRealmValue) datastore.mSelectedItems.get(0);
                            mCurrentWebfaunaObservation.setMilieuRealmValue(selectedRealmValue);

                        /*set text of Species*/
                            mMilieuField.setValue(selectedRealmValue.getTitle());
                        }
                    } else {
                        mCurrentWebfaunaObservation.setMilieuRealmValue(null);
                        mMilieuField.setValue("");
                    }
                    break;
                case structure_data_field_id:
                    if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                        if (datastore.mSelectedItems.get(0) instanceof WebfaunaRealmValue) {
                            WebfaunaRealmValue selectedRealmValue = (WebfaunaRealmValue) datastore.mSelectedItems.get(0);
                            mCurrentWebfaunaObservation.setStructureRealmValue(selectedRealmValue);

                        /*set text of Species*/
                            mStructureField.setValue(selectedRealmValue.getTitle());
                        }
                    } else {
                        mCurrentWebfaunaObservation.setStructureRealmValue(null);
                        mStructureField.setValue("");
                    }
                    break;
                case substrat_data_field_id:
                    if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                        if (datastore.mSelectedItems.get(0) instanceof WebfaunaRealmValue) {
                            WebfaunaRealmValue selectedRealmValue = (WebfaunaRealmValue) datastore.mSelectedItems.get(0);
                            mCurrentWebfaunaObservation.setSubstratRealmValue(selectedRealmValue);

                        /*set text of Species*/
                            mSubstratField.setValue(selectedRealmValue.getTitle());
                        }
                    } else {
                        mCurrentWebfaunaObservation.setSubstratRealmValue(null);
                        mSubstratField.setValue("");
                    }
                    break;
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected void cancelObservation() {
        if(mAreYouSureDialog == null || !mAreYouSureDialog.isShowing()) {
            Resources res = getResources();

            /*Show Are you sure dialog*/
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(res.getString(R.string.observation_cancel_areyousure_dialog_title))
                    .setPositiveButton(R.string.observation_cancel_areyousure_dialog_positive_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /*cancel*/
                            mDismissCurrentObservation = true;
                            if (mIsInEditMode) {
                                sCurrentWebfaunaObservation = null;
                            }
                            dialog.dismiss();
                            mParentActivityCallback.showObservationListFragment();
                        }
                    })
                    .setNegativeButton(R.string.observation_cancel_areyousure_dialog_negative_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            mAreYouSureDialog = builder.create();
            mAreYouSureDialog.show();
        }
    }

    protected void saveObservation() {
        WebfaunaValidationResult validationResult = mCurrentWebfaunaObservation.getValidationResult(getResources());
        if(validationResult.isValid()) {
            if(mIsInEditMode) {
                DataDispatcher.getInstantce().editObservation(mCurrentWebfaunaObservation);
            } else {
                DataDispatcher.getInstantce().addObservation(mCurrentWebfaunaObservation);
            }

            mDismissCurrentObservation = true;
            sCurrentWebfaunaObservation = null;
            mParentActivityCallback.showObservationListFragment();
        } else {
            Resources res = getResources();

            /*Show Are you sure dialog*/
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(res.getString(R.string.observation_validation_dialog_title))
                    .setMessage(validationResult.getValidationMessage())
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            mValidationDialog = builder.create();
            mValidationDialog.show();
        }
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

    protected void showRetryDialog(String title, String message) {
        if(mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        Resources res = getResources();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(message);
        mProgressDialog.setTitle(title);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, res.getText(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDialog.dismiss();
            }
        });
        mProgressDialog.setButton(ProgressDialog.BUTTON_POSITIVE, res.getText(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DataDispatcher.getInstantce().initialize();
            }
        });
        mProgressDialog.show();
    }

    protected void hideRetryDialog() {
        if(mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }


    /*
    DataDispatcher.DataDispatcherBroadcastSubscriber
     */

    @Override
    public void dataDispatcherSystematicsChanged() {
        /*if ObservationFragment gets detatched before the update finished*/
        if(getActivity() != null && getResources() != null) {
            mSystematicsUpToDate = true;
            if (mThesaurusUpToDate) {
                hideProgressDialog();

                createMenu();
            }
        }
    }

    @Override
    public void dataDispatcherSystematicsUpdateError(Exception ex) {
        /*if ObservationFragment gets detatched before the update finished*/
        if(getActivity() != null && getResources() != null) {
            if (true /*has internetconnection*/) {
                Resources res = getResources();
                showRetryDialog(res.getString(R.string.observation_download_progress_dialog_title), res.getString(R.string.observation_download_progress_dialog_message));
            } else {
                hideProgressDialog();
            }
        }
    }

    @Override
    public void dataDispatcherThesaurusChanged() {
        /*if ObservationFragment gets detatched before the update finished*/
        if(getActivity() != null && getResources() != null) {
            mThesaurusUpToDate = true;
            if (mSystematicsUpToDate) {
                hideProgressDialog();

                createMenu();
            }
        }
    }

    @Override
    public void dataDispatcherThesaurusUpdateError(Exception ex) {
        /*if ObservationFragment gets detatched before the update finished*/
        if(getActivity() != null && getResources() != null) {
            if (true /*has internetconnection*/) {
                Resources res = getResources();
                showRetryDialog(res.getString(R.string.observation_download_progress_dialog_title), res.getString(R.string.observation_download_progress_dialog_message));
            } else {
                hideProgressDialog();
            }
        }
    }

    protected void createMenu() {
        ArrayList<GDCDataField> dataFields = new ArrayList<GDCDataField>();

        Resources res = getResources();

        //Date-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_date_section_title)));

        GDCDateDataField dateDataField = new GDCDateDataField(getActivity(),date_data_field_id,res.getString(R.string.observation_date_title),new GDCDataFieldCallback<Date>() {
            @Override
            public void valueChanged(int tag, Date value) {
                //set date
                mCurrentWebfaunaObservation.setObservationDate(value);
            }
        });
        //set default value if needed
        if(mCurrentWebfaunaObservation.getObservationDate() != null) {
            dateDataField.setDate(mCurrentWebfaunaObservation.getObservationDate());
        }
        dataFields.add(dateDataField);


        //location-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_location_section_title)));
        mLocationField = new GDCClickDataField(getActivity(),location_data_field_id,res.getString(R.string.observation_location_title),new GDCClickDataFieldCallback() {
            @Override
            public void fieldClicked(int tag) {
                //show LocationDialog
                mLocationDialogFragment =  new LocationDialogFragment();

                LocationDialogFragment.ViewModel locationViewModel = null;
                if(mCurrentWebfaunaObservation.getWebfaunaLocation() != null) {
                    WebfaunaLocation webfaunaLocation = mCurrentWebfaunaObservation.getWebfaunaLocation();
                    locationViewModel = new LocationDialogFragment.ViewModel(webfaunaLocation.getLieudit(),webfaunaLocation.getAltitude(),webfaunaLocation.getPrecision());
                    locationViewModel.mCHx = webfaunaLocation.getSwissCoordinatesX();
                    locationViewModel.mCHy = webfaunaLocation.getSwissCoordinatesY();
                    locationViewModel.mLat = webfaunaLocation.getWGSCoordinatesLat();
                    locationViewModel.mLng = webfaunaLocation.getWGSCoordinatesLng();
                } else {
                    locationViewModel = new LocationDialogFragment.ViewModel();
                }
                LocationDialogFragment.Datastore locatiomDatastore = new LocationDialogFragment.Datastore(locationViewModel);

                mLocationDialogFragment.setArguments(locatiomDatastore.getBundle());

                mLocationDialogFragment.show(getChildFragmentManager(), LocationDialogFragment.TAG);

            }
        });
        if(mCurrentWebfaunaObservation.getWebfaunaLocation() == null) {
            mLocationField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
        }
        dataFields.add(mLocationField);

        //files-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_files_section_title)));
        dataFields.add(new GDCClickDataField(getActivity(),files_data_field_id,res.getString(R.string.observation_images_title),new GDCClickDataFieldCallback() {
            @Override
            public void fieldClicked(int tag) {
                //show FilesDialog
                FilesDialogFragment filesFragment =  new FilesDialogFragment();

                filesFragment.show(getChildFragmentManager(), FilesDialogFragment.TAG);
            }
        }));

        //species-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_species_section_title)));

        /*group*/
        WebfaunaGroup groupDefaultValue = mCurrentWebfaunaObservation.getWebfaunaGroup();
        ArrayList<ULOneFieldListItemModel> groupListElements = ULOneFieldListItemModel.getListWithItemData(DataDispatcher.getInstantce().getWebfaunaGroups());
        mGroupField = new GDCListDataField(getActivity(),group_data_field_id,res.getString(R.string.observation_group_title),groupListElements, groupDefaultValue,
                true,ListView.CHOICE_MODE_SINGLE);
        if(groupDefaultValue == null) {
            mGroupField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
        }
        dataFields.add(mGroupField);

        /*species*/
        WebfaunaSpecies speciesDefaultValue = mCurrentWebfaunaObservation.getWebfaunaSpecies();
        mSpeciesField = new GDCListDataField(getActivity(),species_data_field_id,res.getString(R.string.observation_species_title),new ArrayList<ULListItemModel>(),speciesDefaultValue,
                true,ListView.CHOICE_MODE_SINGLE);
        /*if group is already set in observation -> activate speciesFiled else: disable*/
        if(mCurrentWebfaunaObservation.getWebfaunaGroup() != null && mCurrentWebfaunaObservation.getWebfaunaGroup().getRestID() != null) {
            ArrayList<? extends ULListItemDataModel> speciesOfGroup = DataDispatcher.getInstantce().getSpecies(mCurrentWebfaunaObservation.getWebfaunaGroup().getRestID());
            if(speciesOfGroup != null) {
                ArrayList<ULOneFieldListItemModel> speciesListElements = ULOneFieldListItemModel.getListWithItemData(speciesOfGroup);
                mSpeciesField.setListItems(speciesListElements);
            }
        } else {
            mSpeciesField.setDisabled(true);
        }
        if(speciesDefaultValue == null) {
            mSpeciesField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
        }
        dataFields.add(mSpeciesField);

        /*identification Method*/
        WebfaunaRealmValue observationDefaultValue = mCurrentWebfaunaObservation.getIdentificationMethod();
        ArrayList<ULOneFieldListItemModel> observationMethodListElements = ULOneFieldListItemModel.getListWithItemData(DataDispatcher.getInstantce().getIdentificationMethodRealm().getRealmValues());
        mObservationMethodField = new GDCListDataField(getActivity(),observation_method_data_field_id,res.getString(R.string.observation_observation_method_title),observationMethodListElements,observationDefaultValue,
                true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mObservationMethodField);
        if(observationDefaultValue == null) {
            mObservationMethodField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
        }


        mAbundanceField = new GDCClickDataField(getActivity(),abundance_data_field_id,res.getString(R.string.observation_abundance_title),new GDCClickDataFieldCallback() {
            @Override
            public void fieldClicked(int tag) {
                //show LocationDialog
                mAbundanceDialogFragment =  new AbundanceDialogFragment();

                WebfaunaAbundance abundanceViewModel = null;
                if(mCurrentWebfaunaObservation.getWebfaunaAbundance() != null) {
                    abundanceViewModel = mCurrentWebfaunaObservation.getWebfaunaAbundance();
                } else {
                    abundanceViewModel = new WebfaunaAbundance();
                }
                AbundanceDialogFragment.Datastore abundanceDatastore = new AbundanceDialogFragment.Datastore(abundanceViewModel);

                mAbundanceDialogFragment.setArguments(abundanceDatastore.getBundle());

                mAbundanceDialogFragment.show(getChildFragmentManager(), AbundanceDialogFragment.TAG);

            }
        });

        dataFields.add(mAbundanceField);

        //environment-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_environment_section_title)));

        WebfaunaRealmValue environmentDefaultValue = mCurrentWebfaunaObservation.getEnvironmentRealmValue();
        ArrayList<ULOneFieldListItemModel> environmentListElements = ULOneFieldListItemModel.getListWithItemData(DataDispatcher.getInstantce().getEnvironmentRealm().getRealmValues());
        mEnvironmentField = new GDCListDataField(getActivity(),environment_data_field_id,res.getString(R.string.observation_environment_title),environmentListElements,environmentDefaultValue,true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mEnvironmentField);

        WebfaunaRealmValue milieuDefaultValue = mCurrentWebfaunaObservation.getMilieuRealmValue();
        ArrayList<ULOneFieldListItemModel> milieuListElements = ULOneFieldListItemModel.getListWithItemData(DataDispatcher.getInstantce().getMilieuRealm().getRealmValues());
        mMilieuField = new GDCListDataField(getActivity(),milieu_data_field_id,res.getString(R.string.observation_milieu_title),milieuListElements,milieuDefaultValue,true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mMilieuField);

        WebfaunaRealmValue structureDefaultValue = mCurrentWebfaunaObservation.getStructureRealmValue();
        ArrayList<ULOneFieldListItemModel> structureListElements = ULOneFieldListItemModel.getListWithItemData(DataDispatcher.getInstantce().getStructureRealm().getRealmValues());
        mStructureField = new GDCListDataField(getActivity(),structure_data_field_id,res.getString(R.string.observation_structure_title),structureListElements,structureDefaultValue,true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mStructureField);

        WebfaunaRealmValue substratDefaultValue = mCurrentWebfaunaObservation.getSubstratRealmValue();
        ArrayList<ULOneFieldListItemModel> substratListElements = ULOneFieldListItemModel.getListWithItemData(DataDispatcher.getInstantce().getSubstratRealm().getRealmValues());
        mSubstratField = new GDCListDataField(getActivity(),substrat_data_field_id,res.getString(R.string.observation_substrat_title),substratListElements,substratDefaultValue,true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mSubstratField);


        /*create listadapter*/
        mListAdapter = new GDCListAdapter(dataFields);
        mListView.setAdapter(mListAdapter);
    }

    /*LocationDialogFragment.Callback*/

    @Override
    public void locationChanged(LocationDialogFragment.ViewModel viewModel){
        if(viewModel != null) {
            WebfaunaLocation tmpLocation = new WebfaunaLocation();
            tmpLocation.setLieudit(viewModel.getLieudit());
            tmpLocation.setAltitude(viewModel.getAltitude());
            tmpLocation.setPrecision(viewModel.getPrecision());
            tmpLocation.setSwissCoordinatesX(viewModel.mCHx);
            tmpLocation.setSwissCoordinatesY(viewModel.mCHy);
            tmpLocation.setWGSCoordinatesLat(viewModel.mLat);
            tmpLocation.setWGSCoordinatesLng(viewModel.mLng);

            mCurrentWebfaunaObservation.setWebfaunaLocation(tmpLocation);

            mLocationField.setMarking(GDCDataField.GDCDataFieldMarking.NOT_MARKED);
        }
    }

    /*AbundanceDialogFragment.Callback*/

    @Override
    public void abundanceChanged(WebfaunaAbundance viewModel) {
        if(viewModel != null) {
            mCurrentWebfaunaObservation.setWebfaunaAbundance(viewModel);
        }
    }
}
