package ch.leafit.webfauna.data.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ch.leafit.webfauna.models.WebfaunaUser;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Created by marius on 09/07/14.
 */
public class SettingsManager {


    private static final String LOG = "SettingsManager";
    private static final String PREFERENCES_NAME = "webfauna";
    private static final String PREFERENCES_USER_KEY = "user";
    private static final String PREFERENCES_LOCALE_KEY = "locale";

    /*
    Singleton
     */
    private static Context sContext;
    public static void setContext(Context context) {
        sContext = sContext;
    }

    private static SettingsManager instance;
    public static SettingsManager getInstance() {
        if(instance == null) {
            instance = new SettingsManager(sContext);
        }
        return instance;
    }


    private Context mContext;
    private SharedPreferences mSettings;

    /*
    Settings Fields
     */
    private WebfaunaUser mUser;
    private Locale mLocale;

    public SettingsManager(Context context) {
        mContext = context;
        initialize();
    }

    private void initialize() {
        mSettings = mContext.getSharedPreferences(PREFERENCES_NAME,0);

        //get user if possible
        if(mSettings.contains(PREFERENCES_USER_KEY)) {
            try {
                String userJSONString = mSettings.getString(PREFERENCES_USER_KEY,"");
                if(!userJSONString.equals("")) {
                    JSONObject userJSON = new JSONObject(userJSONString);
                    mUser = new WebfaunaUser(userJSON);
                }
            } catch (Exception ex) {
                Log.i(LOG,"initialize-user",ex);
                mUser = null;
            }
        }

        //get locale if possible (else take system locale)
        if(mSettings.contains(PREFERENCES_LOCALE_KEY)) {
            try {
                String localeString = mSettings.getString(PREFERENCES_LOCALE_KEY,"");
                if(!localeString.equals("")) {
                    Locale locale = new Locale(localeString);

                    if(!locale.equals(Locale.FRENCH) && !locale.equals(Locale.GERMAN) && !locale.equals(Locale.ITALIAN) && !locale.equals(Locale.ENGLISH)) {
                        throw new Exception("no locale specified in settings");
                    }
                } else {
                    throw new Exception("no locale specified in settings");
                }
            }catch (Exception ex) {
                Log.i(LOG,"initialize-locale",ex);
                mLocale = Locale.getDefault();
                if(mLocale == null) {
                    mLocale = Locale.ENGLISH;
                }
            }
        }
    }

}
