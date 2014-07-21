package ch.leafit.webfauna.views;


import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.support.v4.app.*;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ch.leafit.webfauna.R;
import ch.leafit.webfauna.Utils.NetworkManager;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaObservation;
import ch.leafit.webfauna.webservice.GetSystematicsAsyncTask;
import ch.leafit.webfauna.webservice.ImageUploadTestAsyncTask;
import ch.leafit.webfauna.webservice.WebfaunaWebserviceSystematics;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity implements ParentActivityCallback, NetworkManager.NetworkManagerCallback{

    static {
        /*initialize config*/
        Config.init();
    }

    private DrawerLayout mDrawerLayout;
    private ListView mMenuDrawerList;
    private ActionBarDrawerToggle mMenuDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mMenuItems;


    /**
     * currently visible fragment
     */
    private Fragment mCurrentFragment;

    private NetworkManager mNetworkManager;

    private Dialog mOfflineModeDialog;

    /*
        consts
     */
    private static final int MENU_ADD_POSITION = 0;
    private static final int MENU_OBSERVATIONS_POSITION = 1;
    private static final int MENU_OFFLINE_MAP_POSITION = 2;
    private static final int MENU_PREFERENCES_POSITION = 3;
    private static final int MENU_ABOUT_POSITION = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        /*test*/
        ImageUploadTestAsyncTask tes = new ImageUploadTestAsyncTask();
        tes.execute();


        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mMenuItems = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mMenuDrawerList = (ListView) findViewById(R.id.menu_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mMenuDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuItems));
        mMenuDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayShowHomeEnabled(true);
        getActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mMenuDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.access_menu_drawer_open,  /* "open drawer" description for accessibility */
                R.string.access_menu_drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mMenuDrawerToggle);

        if (savedInstanceState == null) {
            selectMenuItem(0);
        }

        /*set actionbar color*/
        getActionBar().setBackgroundDrawable(new ColorDrawable(Config.actionBarColor));

        /*create networkmanager*/
        NetworkManager.initializeInstance(this,this);
        mNetworkManager = NetworkManager.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*register network receiver*/
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkManager, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mNetworkManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectMenuItem(position);
        }
    }

    private void selectMenuItem(int position) {
        String tag = null;
        switch (position) {
            case MENU_ADD_POSITION:
                mCurrentFragment = new ObservationFragment();
                tag = ObservationFragment.TAG;
                break;
            case MENU_OBSERVATIONS_POSITION:
                mCurrentFragment = new ObservationListFragment();
                tag = ObservationListFragment.TAG;
                break;
            case MENU_OFFLINE_MAP_POSITION:
                mCurrentFragment = new OfflineMapFragment();
                tag = OfflineMapFragment.TAG;
                break;
            case MENU_PREFERENCES_POSITION:
                mCurrentFragment = new PreferencesFragment();
                tag = PreferencesFragment.TAG;
                break;
            case MENU_ABOUT_POSITION:
                mCurrentFragment = new AboutFragment();
                tag = AboutFragment.TAG;
                break;
            default:
                mCurrentFragment = null;
                break;
        }
        if(mCurrentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, mCurrentFragment,tag).commit();

            // update selected item and title, then close the drawer
            mMenuDrawerList.setItemChecked(position, true);
            setTitle(mMenuItems[position]);
            mDrawerLayout.closeDrawer(mMenuDrawerList);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mMenuDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mMenuDrawerToggle.onConfigurationChanged(newConfig);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*call visible sub-fragment*/
        if(mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onBackPressed() {
        // If the fragment exists and has some back-stack entry
        if (mCurrentFragment != null && mCurrentFragment.getChildFragmentManager().getBackStackEntryCount() > 0){
            // Get the fragment fragment manager - and pop the backstack
            mCurrentFragment.getChildFragmentManager().popBackStack();
        }
        // Else, nothing in the direct fragment back stack
        else{
            // Let super handle the back press
            super.onBackPressed();
        }
    }

    /*ParentActivityCallback*/

    @Override
    public void lockSideMenu() {
        if(mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void unlockSideMenu() {
        if(mDrawerLayout != null) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public void showObservationListFragment() {
        mCurrentFragment = new ObservationListFragment();
        String tag = ObservationListFragment.TAG;
        if(mCurrentFragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, mCurrentFragment,tag).commit();
        }
    }

    @Override
    public void showObservationFragmentForEditting(WebfaunaObservation observation) {
        try {
            mCurrentFragment = new ObservationFragment(observation);
            String tag = ObservationFragment.TAG;
            if(mCurrentFragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, mCurrentFragment,tag).commit();
            }
        } catch (CloneNotSupportedException e) {
            Log.e("MainActivity", "showObservationFragmentForEditting", e);
        }

    }

    @Override
    public Fragment getFragmentWithTag(String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment returnFragment = fragmentManager.findFragmentByTag(tag);
        return returnFragment;
    }

    /* NetworkManager.NetworkManagerCallback*/

    @Override
    public void networkConnectionStatusChanged(boolean isConnected) {

        Log.i("statuschanged", "" + isConnected);

        //show alert dialog if necessary
        if(!isConnected && mOfflineModeDialog == null || (mOfflineModeDialog != null && !mOfflineModeDialog.isShowing())) {
            Resources res = getResources();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(res.getString(R.string.offline_mode_dialog_message))
                    .setPositiveButton(res.getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            /*cancel*/
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            mOfflineModeDialog = builder.create();
            mOfflineModeDialog.show();
        } else if(isConnected && mOfflineModeDialog != null && mOfflineModeDialog.isShowing()) {
            mOfflineModeDialog.dismiss();
        }
    }
}