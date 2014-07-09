package ch.leafit.webfauna.views;

import android.app.Activity;
import android.support.v4.app.ListFragment;

/**
 * Created by marius on 08/07/14.
 */
public abstract class BaseListFragment extends ListFragment {

    protected ParentActivityCallback mParentActivityCallback;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mParentActivityCallback = (ParentActivityCallback)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ParentActivityCallback");
        }
    }
}
