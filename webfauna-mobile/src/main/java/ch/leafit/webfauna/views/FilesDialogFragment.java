package ch.leafit.webfauna.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.Utils.FileCompressor;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaObservation;
import ch.leafit.webfauna.models.WebfaunaObservationFile;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by marius on 07/07/14.
 */
public class FilesDialogFragment extends BaseDialogFragment {

    public static final String TAG = "FilesFragment";

    public static final String BUNDLE_KEY_GUID = "guid";

    protected  static final String LOG = "FilesDialogFragment";

    private static final int IMAGE_PICKER_REQUESTCODE = 0;

    /*UI-elements*/
    protected ListView mListView;
    protected Button mBtnAdd;
    protected WebfaunaFilesListAdapter mListAdapter;


    protected UUID mObservationGUID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.files_fragment, null);

        Resources res = getResources();
        getDialog().setTitle(res.getString(R.string.files_dialog_title));

        /*get UI-Elements*/
        mListView = (ListView)contentView.findViewById(R.id.lstMain);
        mBtnAdd = (Button)contentView.findViewById(R.id.btnAdd);

        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start image picker activity
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_PICKER_REQUESTCODE);
            }
        });

        //create list
        ArrayList<WebfaunaObservationFile> files = (ArrayList<WebfaunaObservationFile>)DataDispatcher.getInstantce().getObservationFiles(mObservationGUID.toString());
        mListAdapter = new WebfaunaFilesListAdapter(getActivity(),R.layout.file_list_item,files);

        mListView.setAdapter(mListAdapter);

        return contentView;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);

        //lock side-menu
        mParentActivityCallback.lockSideMenu();

        //get guid of observation
        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(BUNDLE_KEY_GUID)) {
            String guidString = bundle.getString(BUNDLE_KEY_GUID);
            try{
                mObservationGUID = UUID.fromString(guidString);
            } catch(Exception ex) {
                Log.e(LOG,"Could not parse given guid",ex);
            }
        }

        if(mObservationGUID == null)
            throw new ClassCastException("NO valid value for BUNDLE_KEY_GUID passed in bundle");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //unlock side-menu
        mParentActivityCallback.unlockSideMenu();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        //if ok -> add WebfaunaObservationFile to DB
        if (requestCode == IMAGE_PICKER_REQUESTCODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = data.getData();
            try {
                //compress bitmap
                Bitmap compressedBitmap = FileCompressor.compressImageToFitSize(getRealPathFromURI(imageUri), Config.MAX_OBSERVATION_IMAGE_SIZE_BYTES, Config.OBSERVATION_IMAGE_HEIGHT,Config.OBSERVATION_IMAGE_WIDTH);
                if(compressedBitmap != null) {
                    //get byte-array from bitmap
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                    byte[] bitmapByteArray = stream.toByteArray();
                    stream.flush();
                    compressedBitmap.recycle();

                    ByteBuffer bitmapData = ByteBuffer.wrap(bitmapByteArray);
                    if(bitmapData != null) {
                        WebfaunaObservationFile file = new WebfaunaObservationFile(mObservationGUID,bitmapData, WebfaunaObservationFile.ObservationFileType.Image);

                        DataDispatcher.getInstantce().addObservationFile(file);

                        //reload list
                        updateFilesList();
                    }
                }



            } catch (Exception ex) {
                Log.e(LOG,"Could not load image or add it to db",ex);

                //show error dialog
                Resources res = getResources();

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(res.getString(R.string.files_error_dialog_message))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                // Create the AlertDialog object and return it
                AlertDialog errorDialog = builder.create();
                errorDialog.show();
            }
        }
    }



    public void updateFilesList() {
        ArrayList<WebfaunaObservationFile> files = (ArrayList<WebfaunaObservationFile>)DataDispatcher.getInstantce().getObservationFiles(mObservationGUID.toString());
        mListAdapter.clear();
        if (files.size() > 0) {
            mListAdapter.addAll(files);
            mListAdapter.mListItems = files;
        }
        mListAdapter.notifyDataSetChanged();
    }

    /**
     *
     * @param uri
     * @return real path of uri (used for images)
     */
    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    private static class WebfaunaFilesListAdapter extends ArrayAdapter<WebfaunaObservationFile> {

        ArrayList<WebfaunaObservationFile> mListItems;

        public WebfaunaFilesListAdapter(Context context, int resourceId, ArrayList<WebfaunaObservationFile> listItems) {
            super(context,resourceId,listItems);
            mListItems = listItems;
        }

        private class ViewHolder {
            ImageView imgFile;
            TextView txtTitle;
            ImageButton btnDelete;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.file_list_item, null);

                holder = new ViewHolder();
                holder.imgFile = (ImageView) convertView.findViewById(R.id.imgFile);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                holder.btnDelete = (ImageButton) convertView.findViewById(R.id.btnDelete);
                convertView.setTag(holder);

                holder.btnDelete.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View v) {
                        ImageButton btn = (ImageButton) v ;
                        WebfaunaObservationFile file = (WebfaunaObservationFile) btn.getTag();


                        DataDispatcher.getInstantce().deleteObservationFile(file.getGUID().toString());

                        //update list
                        ArrayList<WebfaunaObservationFile> files = (ArrayList<WebfaunaObservationFile>)DataDispatcher.getInstantce().getObservationFiles(file.getObservationGUID().toString());
                        WebfaunaFilesListAdapter.this.clear();
                        if (files.size() > 0) {
                            WebfaunaFilesListAdapter.this.addAll(files);
                            WebfaunaFilesListAdapter.this.mListItems = files;
                        }
                        WebfaunaFilesListAdapter.this.notifyDataSetChanged();
                    }
                });
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(mListItems.size() > position) {
                WebfaunaObservationFile file = mListItems.get(position);

                //create bitmap from data
                if(file.getData() != null) {
                    try {
                        byte[] imageByteArray = file.getData().array();
                        Bitmap bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                        holder.imgFile.setImageBitmap(bmp);
                    } catch (Exception ex) {
                        Log.e(LOG, "could not get bitmap from data", ex);
                    }
                }


                holder.txtTitle.setText("");
                holder.btnDelete.setTag(file);
            }

            return convertView;

        }

    }
}
