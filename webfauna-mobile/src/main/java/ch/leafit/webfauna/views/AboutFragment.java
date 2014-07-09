package ch.leafit.webfauna.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ch.leafit.webfauna.R;

/**
 * Created by marius on 08/07/14.
 */
public class AboutFragment extends BaseFragment {
    public static final String TAG = "AboutFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.about_fragment, null);
        return contentView;
    }
}
