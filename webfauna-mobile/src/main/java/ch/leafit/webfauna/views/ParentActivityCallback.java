package ch.leafit.webfauna.views;

import android.support.v4.app.Fragment;

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

    /**
     * can be used by the fragments to communicate with other fragments
     * */
    public Fragment getFragmentWithTag(String tag);
}
