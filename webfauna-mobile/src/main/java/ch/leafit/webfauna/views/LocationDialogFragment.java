package ch.leafit.webfauna.views;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ch.leafit.gdc.*;
import ch.leafit.gdc.callback.GDCDataFieldCallback;
import ch.leafit.iac.BundleDatastore;
import ch.leafit.ul.list_items.ULListItemBaseModel;
import ch.leafit.webfauna.R;

import java.util.ArrayList;

/**
 * Created by marius on 07/07/14.
 */
public class LocationDialogFragment extends BaseDialogFragment {

    public static final String TAG = "LocationFragment";

    /*data field ids*/
    protected static final int lieudit_data_field_id = 0;
    protected static final int coordinates_data_field_id = 1;
    protected static final int precision_data_field_id = 2;
    protected static final int altitude_data_field_id = 3;

    /*UI-elements*/
    protected ListView mListView;

    /*list-stuff*/
    protected GDCListAdapter mListAdapter;

    /*list-datafields*/
    GDCStringDataField mLieuditField;


    protected Callback mParentFragmentCallback;

    protected ViewModel mViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.location_fragment,null);

        Resources res = getResources();
        getDialog().setTitle(res.getString(R.string.location_dialog_title));

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
            mViewModel = new ViewModel();
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

        //lieudit
        String lieuDitDefaultValue = mViewModel.getLieudit();
        mLieuditField = new GDCStringDataField(getActivity(),lieudit_data_field_id,res.getString(R.string.location_lieudit_title),lieuDitDefaultValue,new GDCDataFieldCallback<String>() {
            @Override
            public void valueChanged(int tag, String value) {
                mViewModel.setLieudit(value);
                mLieuditField.setMarking(GDCDataField.GDCDataFieldMarking.NOT_MARKED);
                mParentFragmentCallback.locationChanged(mViewModel);
            }
        });
        dataFields.add(mLieuditField);

        //precision
        dataFields.add(new GDCListDataField(getActivity(),precision_data_field_id,res.getString(R.string.location_precision_title),new ArrayList<ULListItemBaseModel>(),
                true,ListView.CHOICE_MODE_SINGLE));

        //altitude
        Integer altitudeDefaultValue = 0;
        if(mViewModel.getAltitude() != null) {
            altitudeDefaultValue = mViewModel.getAltitude();
        }
        dataFields.add(new GDCIntegerDataField(getActivity(),altitude_data_field_id,res.getString(R.string.location_altitude_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.setAltitude(value);
                mParentFragmentCallback.locationChanged(mViewModel);
            }
        },altitudeDefaultValue));



        return dataFields;
    }

    /*
    * Callback & BundleDatastore & ViewModel
    */

    public static interface Callback {
        public void locationChanged(ViewModel viewModel);
    }

    /**
     * contains the LocationFragment.ViewModel-data
     */
    public static class Datastore extends BundleDatastore{
        /*
        * data-ids
        */
        private static final String viewmodel_id = "viewmodel";

        /*
         * data members
         */
        public ViewModel mViewModel;

        public Datastore(ViewModel viewModel) {
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

    public static class ViewModel implements Parcelable {

        public ViewModel(){}

        public ViewModel(String lieudit, Integer altitude) {
            mLieudit = lieudit;
            mAltitude = altitude;
        }

        public ViewModel(Parcel in) {
            readFromParcel(in);
        }

        private String mLieudit;
        //coordinates
        //precision
        private Integer mAltitude;

        public String getLieudit() {
            return mLieudit;
        }
        public void setLieudit(String lieudit) {
            mLieudit = lieudit;
        }

        public Integer getAltitude() {
            return mAltitude;
        }
        public void setAltitude(Integer altitude) {
            mAltitude = altitude;
        }

    /*
	 * @Parcelable
	 */

        public static final Parcelable.Creator<ViewModel> CREATOR = new Parcelable.Creator<ViewModel>() {
            public ViewModel createFromParcel(Parcel in ) {
                return new ViewModel(in);
            }

            public ViewModel[] newArray(int size) {
                return new ViewModel[size];
            }
        };

        public void writeToParcel(Parcel dest, int flags) {
            if(mLieudit != null) {
                dest.writeString(mLieudit);
            }
            if(mAltitude != null) {
                dest.writeInt(mAltitude);
            }
        }

        private void readFromParcel(Parcel in) {
            mLieudit = in.readString();
            mAltitude = in.readInt();
        }

        public int describeContents() {
            return 0;
        }
    }

}
