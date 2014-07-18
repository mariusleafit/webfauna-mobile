package ch.leafit.webfauna;

import android.app.Application;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.data.settings.SettingsManager;

/**
 * Created by marius on 17/07/14.
 */
public class WebfaunaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        SettingsManager.setContext(getApplicationContext());
        DataDispatcher.setContext(getApplicationContext());
    }
}
