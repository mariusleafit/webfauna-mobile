package ch.leafit.webfauna.views;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import ch.leafit.gdc.*;
import ch.leafit.gdc.callback.GDCClickDataFieldCallback;
import ch.leafit.gdc.callback.GDCDataFieldCallback;
import ch.leafit.ul.list_items.ULListItemBaseModel;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.models.WebfaunaObservation;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by marius on 25/06/14.
 */
public class ObservationFragment extends BaseFragment implements LocationDialogFragment.Callback{

    public static final String TAG = "ObservationFragment";

    /**
     * observation, which the user is currently working on, but he probably left the Fragment during the process
     */
    protected static WebfaunaObservation sCurrentWebfaunaObservation;

    /*data field ids*/
    protected static final int date_data_field_id = 0;

    protected static final int location_data_field_id = 1;

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
    protected FrameLayout mContentFrame;

    /*list-stuff*/
    protected GDCListAdapter mListAdapter;

    /*list-datafields*/
    protected GDCListDataField mGroupField;
    protected GDCListDataField mSpeciesField;
    protected GDCListDataField mObservationMethodField;
    protected GDCListDataField mAbundanceField;


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
        mIsInEditMode = false;
    }

    /**
     * Create ObservationFragment to edit an observation
     *
     * @param webfaunaObservation observation to Edit
     */
    public ObservationFragment(WebfaunaObservation webfaunaObservation) throws CloneNotSupportedException{
        try {
            mCurrentWebfaunaObservation = (WebfaunaObservation) webfaunaObservation.clone();
            mIsInEditMode = true;
        } catch (CloneNotSupportedException e) {
            Log.e("ObservationFragment","could not instanciate: " + e.getMessage());
            throw e;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.observation_fragment,null);

        mContentFrame = (FrameLayout)contentView.findViewById(R.id.content_frame);

        mListView = (ListView)contentView.findViewById(R.id.lstMain);
        mListView.setItemsCanFocus(true);

        /*create listadapter*/
        ArrayList<GDCDataField> dataFields = createMenu();

        mListAdapter = new GDCListAdapter(dataFields);
        mListView.setAdapter(mListAdapter);

        return contentView;
    }

    @Override
    public void onDetach() {
        /*called when the fragment is removed*/
        super.onDetach();

        //cache currentObservation if necessary
        if(mCurrentWebfaunaObservation != null) {
            sCurrentWebfaunaObservation = mCurrentWebfaunaObservation;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*method gets called when user makes a selection in a GDCListView*/
        switch (requestCode) {
            case group_data_field_id:
                //mGroupField.setValue("Gruppenvalue");
                break;
            case species_data_field_id:
                break;
            case observation_method_data_field_id:
                break;
            case environment_data_field_id:
                break;
            case milieu_data_field_id:
                break;
            case structure_data_field_id:
                break;
            case substrat_data_field_id:
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected ArrayList<GDCDataField> createMenu() {
        final ArrayList<GDCDataField> dataFields = new ArrayList<GDCDataField>();

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
        dataFields.add(new GDCClickDataField(getActivity(),location_data_field_id,res.getString(R.string.observation_location_title),new GDCClickDataFieldCallback() {
            @Override
            public void fieldClicked(int tag) {
                //show LocationFragment in content_frame of mainActivity
                LocationDialogFragment locationFragment =  new LocationDialogFragment();

                LocationDialogFragment.ViewModel locationViewModel = new LocationDialogFragment.ViewModel("Marius",123);
                LocationDialogFragment.Datastore locatiomDatastore = new LocationDialogFragment.Datastore(locationViewModel);
                locationFragment.setArguments(locatiomDatastore.getBundle());

                locationFragment.show(getChildFragmentManager(), LocationDialogFragment.TAG);

                /*FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                //add locationFragment to fragmentStack
                fragmentTransaction.add(R.id.content_frame, locationFragment, LocationFragment.TAG);
                //add transaction to backstack that ObservationFragment will be redisplayed once the user clicks back
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/

            }
        }));

        //files-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_files_section_title)));
        dataFields.add(new GDCClickDataField(getActivity(),location_data_field_id,res.getString(R.string.observation_images_title),new GDCClickDataFieldCallback() {
            @Override
            public void fieldClicked(int tag) {
                //show FilesFragment in content_frame of mainActivity
                FilesDialogFragment filesFragment =  new FilesDialogFragment();

                filesFragment.show(getChildFragmentManager(), FilesDialogFragment.TAG);

                /*FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                //add locationFragment to fragmentStack
                fragmentTransaction.add(R.id.content_frame, filesFragment, LocationFragment.TAG);
                //add transaction to backstack that ObservationFragment will be redisplayed once the user clicks back
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();*/
            }
        }));

        //species-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_species_section_title)));
        mGroupField = new GDCListDataField(getActivity(),group_data_field_id,res.getString(R.string.observation_group_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mGroupField);

        mSpeciesField = new GDCListDataField(getActivity(),species_data_field_id,res.getString(R.string.observation_species_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mSpeciesField);

        mObservationMethodField = new GDCListDataField(getActivity(),observation_method_data_field_id,res.getString(R.string.observation_observation_method_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mObservationMethodField);

        mAbundanceField = new GDCListDataField(getActivity(), abundance_data_field_id,res.getString(R.string.observation_abundance_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE);
        dataFields.add(mAbundanceField);

        //environment-section
        dataFields.add(new GDCSectionTitleDataField(getActivity(),res.getString(R.string.observation_environment_section_title)));
        dataFields.add(new GDCListDataField(getActivity(),environment_data_field_id,res.getString(R.string.observation_environment_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE));

        dataFields.add(new GDCListDataField(getActivity(),milieu_data_field_id,res.getString(R.string.observation_milieu_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE));

        dataFields.add(new GDCListDataField(getActivity(),structure_data_field_id,res.getString(R.string.observation_structure_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE));

        dataFields.add(new GDCListDataField(getActivity(),substrat_data_field_id,res.getString(R.string.observation_substrat_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE));

        return dataFields;
    }

    /*LocationFragmentCallback*/

    @Override
    public void locationChanged(LocationDialogFragment.ViewModel viewModel){
        Log.i("callback",viewModel.getLieudit());
    }


}
