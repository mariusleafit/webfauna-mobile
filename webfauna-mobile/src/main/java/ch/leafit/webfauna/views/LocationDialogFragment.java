package ch.leafit.webfauna.views;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import ch.leafit.gdc.*;
import ch.leafit.gdc.callback.GDCDataFieldCallback;
import ch.leafit.iac.BundleDatastore;
import ch.leafit.ul.activities.intent_datastores.ULListActivityReturnIntentDatastore;
import ch.leafit.ul.list_items.ULListItemModel;
import ch.leafit.ul.list_items.ULOneFieldListItemModel;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.gdc.GDCCoordinatesDataField;
import ch.leafit.webfauna.models.WebfaunaRealmValue;

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
    GDCCoordinatesDataField mCoordinatesField;
    GDCListDataField mPrecisionField;
    GDCIntegerDataField mAltitudeField;

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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //get received data
        ULListActivityReturnIntentDatastore datastore = new ULListActivityReturnIntentDatastore(data);
        switch (requestCode) {
            case precision_data_field_id:
                if (datastore != null && datastore.mSelectedItems != null && datastore.mSelectedItems.size() > 0) {
                    if (datastore.mSelectedItems.get(0) instanceof WebfaunaRealmValue) {
                        WebfaunaRealmValue selectedRealmValue = (WebfaunaRealmValue) datastore.mSelectedItems.get(0);
                        mViewModel.setPrecision(selectedRealmValue);
                        mPrecisionField.setCurrentSelection(selectedRealmValue);
                        mPrecisionField.setMarking(GDCDataField.GDCDataFieldMarking.NOT_MARKED);
                        mParentFragmentCallback.locationChanged(mViewModel);
                    }
                } else {
                    mViewModel.setPrecision(null);
                    mPrecisionField.setCurrentSelection(null);
                    mPrecisionField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    protected ArrayList<GDCDataField> createMenu() {
        ArrayList<GDCDataField> dataFields = new ArrayList<GDCDataField>();

        Resources res = getResources();



        //coordinates
        mCoordinatesField = new GDCCoordinatesDataField(getActivity(),coordinates_data_field_id,res.getString(R.string.location_coordinates_title),new GDCCoordinatesDataField.Callback() {
            @Override
            public void coordinatesDataFieldChanged(double chX, double chY, double lat, double lng) {
                mViewModel.mCHx = chX;
                mViewModel.mCHy = chY;
                mViewModel.mLat = lat;
                mViewModel.mLng = lng;
                mParentFragmentCallback.locationChanged(mViewModel);
            }

            @Override
            public void coordinatesDataFieldClicked() {
                /*oopen map-activtiy*/
            }
        });
        /*default value...*/
        if(mViewModel.mCHx != -1 && mViewModel.mCHy != -1) {
            mCoordinatesField.setCHcoordinates(mViewModel.mCHx,mViewModel.mCHy);
        }

        dataFields.add(mCoordinatesField);



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
        if(lieuDitDefaultValue == null) {
            mLieuditField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
        }
        dataFields.add(mLieuditField);


        //precision
        WebfaunaRealmValue precisionDefaultValue = mViewModel.getPrecision();
        ArrayList<ULOneFieldListItemModel> precisionListElements = ULOneFieldListItemModel.getListWithItemData(DataDispatcher.getInstantce().getPrecisionRealm().getRealmValues());
        mPrecisionField = new GDCListDataField(getActivity(),precision_data_field_id,res.getString(R.string.location_precision_title),precisionListElements, precisionDefaultValue,
                true,ListView.CHOICE_MODE_SINGLE);
        if(precisionDefaultValue == null) {
            mPrecisionField.setMarking(GDCDataField.GDCDataFieldMarking.MARKED_AS_INVALID);
        }
        dataFields.add(mPrecisionField);

        //altitude
        Integer altitudeDefaultValue = 0;
        if(mViewModel.getAltitude() != null) {
            altitudeDefaultValue = mViewModel.getAltitude();
        }
        mAltitudeField = new GDCIntegerDataField(getActivity(),altitude_data_field_id,res.getString(R.string.location_altitude_title),new GDCDataFieldCallback<Integer>() {
            @Override
            public void valueChanged(int tag, Integer value) {
                mViewModel.setAltitude(value);
                mParentFragmentCallback.locationChanged(mViewModel);
            }
        },altitudeDefaultValue);
        dataFields.add(mAltitudeField);



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

        private String mLieudit;
        //coordinates
        private Integer mAltitude;
        private WebfaunaRealmValue mPrecision;

        public double mCHx = -1;
        public double mCHy = -1;
        public double mLat = -1;
        public double mLng = -1;

        public ViewModel(){}

        public ViewModel(String lieudit, Integer altitude, WebfaunaRealmValue precision, double chX, double chY) {
            this(lieudit,altitude,precision);
            mCHx = chX;
            mCHy = chY;
        }

        public ViewModel(String lieudit, Integer altitude, WebfaunaRealmValue precision) {
            mLieudit = lieudit;
            mAltitude = altitude;
            mPrecision = precision;
        }

        public ViewModel(Parcel in) {
            readFromParcel(in);
        }

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

        public WebfaunaRealmValue getPrecision() {
            return mPrecision;
        }
        public void setPrecision(WebfaunaRealmValue precision) {
            mPrecision = precision;
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
            if(mPrecision != null) {
                dest.writeParcelable(mPrecision,flags);
            }
        }

        private void readFromParcel(Parcel in) {
            mLieudit = in.readString();
            mAltitude = in.readInt();
            mPrecision = in.readParcelable(WebfaunaRealmValue.class.getClassLoader());
        }

        public int describeContents() {
            return 0;
        }
    }

}
