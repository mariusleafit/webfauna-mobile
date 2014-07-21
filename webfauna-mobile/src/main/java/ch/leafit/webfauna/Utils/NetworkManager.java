package ch.leafit.webfauna.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by marius on 21/07/14.
 */
public class NetworkManager extends BroadcastReceiver {
    private static final String LOG = "ConnectivityManager";

    private static NetworkManager sInstance;
    public static void initializeInstance(NetworkManagerCallback callback, Context context) {
        sInstance = new NetworkManager(callback,context);
    }
    public static NetworkManager getInstance() {
        return sInstance;
    }


    NetworkManagerCallback mCallback;
    Context mContext;

    public NetworkManager(NetworkManagerCallback callback, Context context) {
        super();
        mCallback = callback;
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG, "Action: " + intent.getAction());
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            String typeName = info.getTypeName();
            String subtypeName = info.getSubtypeName();
            boolean available = info.isAvailable();
            Log.i(LOG, "Network Type: " + typeName
                    + ", subtype: " + subtypeName
                    + ", available: " + available);

            mCallback.networkConnectionStatusChanged(available);
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static interface NetworkManagerCallback {
        public void networkConnectionStatusChanged(boolean isConnected);
    }
}
