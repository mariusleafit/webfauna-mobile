package ch.leafit.webfauna.views;

import android.support.v4.app.Fragment;
import ch.leafit.webfauna.Utils.NetworkManager;
import ch.leafit.webfauna.models.WebfaunaObservation;

/**
 * Created by marius on 08/07/14.
 */
public interface ParentActivityCallback {
    /**
     * disables the side-menu
     */
    public void lockSideMenu();

    /**
     * enables the side-menu
     */
    public void unlockSideMenu();

    public void showObservationListFragment();

    public void showObservationFragment(WebfaunaObservation observation, boolean isInEditMode);

    /**
     * can be used by the fragments to communicate with other fragments
     * */
    public Fragment getFragmentWithTag(String tag);

    public NetworkManager getNetworkManager();
}
