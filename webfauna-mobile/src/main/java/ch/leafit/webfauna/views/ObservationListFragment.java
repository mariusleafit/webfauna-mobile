package ch.leafit.webfauna.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.widget.*;
import ch.leafit.ul.adapters.ULListAdapter;
import ch.leafit.ul.adapters.ULSimpleListAdapter;
import ch.leafit.ul.list_items.ULTwoFieldsListItemModel;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaObservation;
import ch.leafit.webfauna.webservice.PostObservationsAsyncTask;
import com.sun.javaws.exceptions.InvalidArgumentException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by marius on 07/07/14.
 */
public class ObservationListFragment extends BaseFragment implements PostObservationsAsyncTask.Callback{
    public static final String TAG = "ObservationListFragment";

    /*List stuff*/
    ObservationListAdapter mListAdapter;
    ListView mListView;

    /*dialogs*/
    private Dialog mAreYouSureDialog;
    private ProgressDialog mProgressDialog;
    private Dialog mErrorDialog;

    private PostObservationsAsyncTask mPostObservationAsyncTask;

    public ObservationListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.observation_list_fragment,null);

        /*UI-Elements*/
        mListView = (ListView)contentView.findViewById(R.id.lstMain);

        /*create ListAdapter*/

        /*get observation listItemDataModels*/
        ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel> observationListItemDataModels = reloadObservationList();

        mListAdapter = new ObservationListAdapter(getActivity(),R.layout.observation_list_item,observationListItemDataModels);

        mListView.setAdapter(mListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WebfaunaObservation.WebfaunaObservationULListDataModel clickedListItem = mListAdapter.getItem(position);
                if(clickedListItem != null && clickedListItem.getGUID() != null) {
                    WebfaunaObservation clickedObservation = DataDispatcher.getInstantce().getObservation(clickedListItem.getGUID());
                    if(clickedObservation != null) {
                        mParentActivityCallback.showObservationFragmentForEditting(clickedObservation);
                    }
                }
            }
        });

        return contentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu items for use in the action bar
        inflater.inflate(R.menu.observation_list_fragment_actionbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_upload:
                uploadCheckedObservations();
                return true;
            case R.id.action_remove:
                removeCheckedObservations();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void uploadCheckedObservations() {
        ArrayList<WebfaunaObservation> checkedObservations = getCheckedObservations();
        if(checkedObservations != null && checkedObservations.size() > 0) {
            if(mPostObservationAsyncTask == null) {
                try {
                    mPostObservationAsyncTask = new PostObservationsAsyncTask(this,checkedObservations);

                    /*show progressdialog*/
                    Resources res = getResources();
                    showProgressDialog(res.getString(R.string.observation_list_upload_progress_dialog_title),res.getString(R.string.observation_list_upload_progress_dialog_message));

                    mPostObservationAsyncTask.execute();
                } catch (Exception e) {
                    Log.e("ObservationListFragment","upload",e);
                }
            }
        }
    }

    private void removeCheckedObservations() {
        ArrayList<WebfaunaObservation> checkedObservations = getCheckedObservations();
        if(checkedObservations != null) {
            if(mAreYouSureDialog == null || !mAreYouSureDialog.isShowing()) {
                Resources res = getResources();

            /*Show Are you sure dialog*/
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(res.getString(R.string.observation_list_areyousure_delete_dialog_message))
                        .setPositiveButton(R.string.observation_list_areyousure_dialog_positive_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ArrayList<WebfaunaObservation> checkedObservations = getCheckedObservations();
                                for (WebfaunaObservation observation : checkedObservations) {
                                    DataDispatcher.getInstantce().deleteObservation(observation.getGUID().toString());
                                }

                                ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel> listItems = reloadObservationList();
                                mListAdapter.clear();
                                if (listItems.size() > 0) {
                                    mListAdapter.addAll(listItems);
                                    mListAdapter.mListItems = listItems;
                                }
                                mListAdapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(R.string.observation_list_areyousure_dialog_negative_button, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                mAreYouSureDialog = builder.create();
                mAreYouSureDialog.show();
            }
        }



    }

    private ArrayList<WebfaunaObservation> getCheckedObservations() {
        ArrayList<WebfaunaObservation> checkedObserations = new ArrayList<WebfaunaObservation>();

        if(mListAdapter != null && mListAdapter.mListItems != null && mListAdapter.mListItems.size() > 0) {
            for(WebfaunaObservation.WebfaunaObservationULListDataModel listItem: mListAdapter.mListItems) {
                if(listItem.isSelected()) {
                    WebfaunaObservation tmpObservation = DataDispatcher.getInstantce().getObservation(listItem.getGUID());
                    if (tmpObservation != null) {
                        checkedObserations.add(tmpObservation);
                    }
                }
            }
        }

        return checkedObserations;
    }

    protected ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel> reloadObservationList() {
        ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel> observationListItemDataModels = new ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel>();
        for(WebfaunaObservation observation : DataDispatcher.getInstantce().getObservations()) {
            WebfaunaObservation.WebfaunaObservationULListDataModel tmp = observation.getListItemDataModel();
            if(tmp != null) {
                observationListItemDataModels.add(tmp);
            }
        }

        return observationListItemDataModels;
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

    /*PostObservationsAsyncTask.Callback*/

    @Override
    public void finishedObservationUpload(ArrayList<WebfaunaObservation> failedObservationsGUIDs, Exception ex) {
        hideProgressDialog();

        if(ex != null || failedObservationsGUIDs.size() > 0) {
            if(mErrorDialog == null || !mErrorDialog.isShowing()) {
                Resources res = getResources();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(res.getString(R.string.observation_list_upload_error_dialog_message))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            /*cancel*/
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        }

        /*reload observation-list*/
        ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel> listItems = reloadObservationList();
        mListAdapter.clear();
        if (listItems.size() > 0) {
            mListAdapter.addAll(listItems);
            mListAdapter.mListItems = listItems;
        }
        mListAdapter.notifyDataSetChanged();


        mPostObservationAsyncTask = null;
    }

    private static class ObservationListAdapter extends ArrayAdapter<WebfaunaObservation.WebfaunaObservationULListDataModel> {

        ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel> mListItems;

        public ObservationListAdapter(Context context, int resourceId, ArrayList<WebfaunaObservation.WebfaunaObservationULListDataModel> listItems) {
            super(context,resourceId,listItems);
            mListItems = listItems;
        }

        private class ViewHolder {
            TextView txtTitle;
            TextView txtSubtitle;
            CheckBox checkbox;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.observation_list_item, null);

                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                holder.txtSubtitle = (TextView) convertView.findViewById(R.id.txtSubtitle);
                holder.checkbox = (CheckBox) convertView.findViewById(R.id.list_item_checkbox);
                convertView.setTag(holder);

                holder.checkbox.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v ;
                        WebfaunaObservation.WebfaunaObservationULListDataModel observation = (WebfaunaObservation.WebfaunaObservationULListDataModel) cb.getTag();
                        observation.setIsSelected(cb.isChecked());
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(mListItems.size() > position) {
                WebfaunaObservation.WebfaunaObservationULListDataModel observation = mListItems.get(position);
                holder.txtTitle.setText(observation.getTitle());
                holder.txtSubtitle.setText(observation.getSubtitle());
                holder.checkbox.setChecked(observation.isSelected());
                holder.checkbox.setTag(observation);
            }

            return convertView;

        }

    }
}
