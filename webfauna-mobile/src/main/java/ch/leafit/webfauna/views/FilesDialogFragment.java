package ch.leafit.webfauna.views;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.leafit.webfauna.R;

/**
 * Created by marius on 07/07/14.
 */
public class FilesDialogFragment extends BaseDialogFragment {

    public static final String TAG = "FilesFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.files_fragment, null);
        return contentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //lock side-menu
        mParentActivityCallback.lockSideMenu();
    }

    @Override
    public void onDetach() {
        super.onDetach();

        //unlock side-menu
        mParentActivityCallback.unlockSideMenu();
    }
}
