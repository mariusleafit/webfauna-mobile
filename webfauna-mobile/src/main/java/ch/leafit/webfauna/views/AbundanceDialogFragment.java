package ch.leafit.webfauna.views;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ch.leafit.gdc.*;
import ch.leafit.gdc.callback.GDCDataFieldCallback;
import ch.leafit.iac.BundleDatastore;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.models.WebfaunaAbundance;

import java.util.ArrayList;

/**
 * Created by marius on 13/07/14.
 */
public class AbundanceDialogFragment extends BaseDialogFragment {
    public static final String TAG = "AbundanceFragment";

    /*data field ids*/
    protected static final int individuals_field_tag = 0;
    protected static final int males_field_tag = 1;
    protected static final int females_field_tag = 2;
    protected static final int eggs_field_tag = 3;
    protected static final int larves_field_tag = 4;
    protected static final int nymphs_field_tag = 5;
    protected static final int youngs_field_tag = 6;
    protected static final int subadults_field_tag = 7;
    protected static final int couples_field_tag = 8;

    /*UI-elements*/
    protected ListView mListView;

    /*list-stuff*/
    protected GDCListAdapter mListAdapter;

    /*list-datafields*/


    protected Callback mParentFragmentCallback;

    protected WebfaunaAbundance mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.abundance_fragment,null);

        Resources res = getResources();
        getDialog().setTitle(res.getString(R.string.abundance_dialog_title));

        mListView = (ListView)contentView.findViewById(R.id.lstMain);
        mListView.setItemsCanFocus(true);

        /*create listadapter*/
        ArrayList<GDCDataField> dataFields = createMenu();

        mListAdapter = new GDCListAdapter(dataFields);
        mListView.setAdapter(mListAdapter);

        return contentView;
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


        /*get datastore from arguments*/
        Bundle bundle = getArguments();
        if(bundle != null) {
            Datastore datastore = new Datastore(bundle);
            if(datastore.mViewModel != null) {
                mViewModel = datastore.mViewModel;
            }
        }

        if(mViewModel == null) {
            mViewModel = new WebfaunaAbundance();
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected ArrayList<GDCDataField> createMenu() {
        ArrayList<GDCDataField> dataFields = new ArrayList<GDCDataField>();

        Resources res = getResources();


        //individuals
        Integer individualsDefaultValue = 0;
        if(mViewModel.mIndividuals != null) {
            individualsDefaultValue = mViewModel.mIndividuals;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),individuals_field_tag,res.getString(R.string.abundance_individuals_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mIndividuals = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },individualsDefaultValue));

        //males
        Integer malesDefaultValue = 0;
        if(mViewModel.mMales != null) {
            malesDefaultValue = mViewModel.mMales;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),males_field_tag,res.getString(R.string.abundance_males_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mMales = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },malesDefaultValue));

        //females
        Integer femalesDefaultValue = 0;
        if(mViewModel.mMales != null) {
            femalesDefaultValue = mViewModel.mFemales;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),females_field_tag,res.getString(R.string.abundance_females_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mFemales = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },femalesDefaultValue));


        //eggs
        Integer eggsDefaultValue = 0;
        if(mViewModel.mEggs != null) {
            eggsDefaultValue = mViewModel.mEggs;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),eggs_field_tag,res.getString(R.string.abundance_eggs_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mEggs = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },eggsDefaultValue));


        //larves
        Integer larvesDefaultValue = 0;
        if(mViewModel.mLarvae != null) {
            larvesDefaultValue = mViewModel.mLarvae;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),larves_field_tag,res.getString(R.string.abundance_larves_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mLarvae = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },larvesDefaultValue));

        //youngs
        Integer youngsDefaultValue = 0;
        if(mViewModel.mExuviae != null) {
            youngsDefaultValue = mViewModel.mExuviae;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),youngs_field_tag,res.getString(R.string.abundance_youngs_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mExuviae = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },youngsDefaultValue));



        //nymphs
        Integer nymphsDefaultValue = 0;
        if(mViewModel.mNymphs != null) {
            nymphsDefaultValue = mViewModel.mNymphs;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),nymphs_field_tag,res.getString(R.string.abundance_nymphs_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mNymphs = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },nymphsDefaultValue));

        //subadults
        Integer subadultsDefaultValue = 0;
        if(mViewModel.mSubadults != null) {
            subadultsDefaultValue = mViewModel.mSubadults;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),subadults_field_tag,res.getString(R.string.abundance_subadults_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mSubadults = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },subadultsDefaultValue));

        //couples
        Integer couplesDefaultValue = 0;
        if(mViewModel.mMating != null) {
            couplesDefaultValue = mViewModel.mMating;
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),couples_field_tag,res.getString(R.string.abundance_couples_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.mMating = value;
                mParentFragmentCallback.abundanceChanged(mViewModel);
            }
        },couplesDefaultValue));


        return dataFields;
    }

    /*
    * Callback & BundleDatastore & ViewModel
    */

    public static interface Callback {
        public void abundanceChanged(WebfaunaAbundance viewModel);
    }

    /**
     * contains the LocationFragment.ViewModel-data
     */
    public static class Datastore extends BundleDatastore {
        /*
        * data-ids
        */
        private static final String viewmodel_id = "viewmodel";

        /*
         * data members
         */
        public WebfaunaAbundance mViewModel;

        public Datastore(WebfaunaAbundance viewModel) {
            mViewModel = viewModel;
        }

        public Datastore(Bundle i){
            super(i);
            setBundle(i);
        }

        @Override
        public Bundle getBundle() {
            Bundle returnBundle = new Bundle();
            returnBundle.putParcelable(viewmodel_id,mViewModel);
            return returnBundle;
        }

        @Override
        public void setBundle(Bundle in) {
            mViewModel = in.getParcelable(viewmodel_id);
        }
    }

}
