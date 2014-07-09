package ch.leafit.webfauna.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.leafit.webfauna.R;

/**
 * Created by marius on 08/07/14.
 */
public class OfflineMapFragment extends BaseFragment {
    public static final String TAG = "OfflineMapFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.offline_map_fragment, null);
        return contentView;
    }
}
