package ch.leafit.webfauna;

import android.app.Application;
import ch.leafit.om.cache.OfflineMapManager;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.data.settings.SettingsManager;

/**
 * Created by marius on 17/07/14.
 */
public class WebfaunaApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Config.init();

        /*
        OfflineMap config
         */
        ch.leafit.om.config.Config.CACHED_ZOOM_LEVELS = new int[]{17};

        OfflineMapManager.initialize(this);

        SettingsManager.setContext(getApplicationContext());
        DataDispatcher.setContext(getApplicationContext());
    }
}
