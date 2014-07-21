package ch.leafit.webfauna.data.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import ch.leafit.webfauna.models.WebfaunaUser;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by marius on 09/07/14.
 */
public class SettingsManager {


    private static final String LOG = "SettingsManager";
    private static final String PREFERENCES_NAME = "webfauna";
    private static final String PREFERENCES_USER_KEY = "user";
    private static final String PREFERENCES_PASSWORD_KEY = "password";
    private static final String PREFERENCES_LOCALE_KEY = "locale";

    /*
    Singleton
     */
    private static Context sContext;
    public static void setContext(Context context) {
        sContext = context;
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

    private List<Locale> mSupportedLocales;

    public SettingsManager(Context context) {
        mContext = context;
        mSubscribers = new ArrayList<SettingsManagerBroadcastSubscriber>();
        initialize();
    }

    private void initialize() {
        mSettings = mContext.getSharedPreferences(PREFERENCES_NAME,0);

        //get user if possible
        if(mSettings.contains(PREFERENCES_USER_KEY) && mSettings.contains(PREFERENCES_PASSWORD_KEY)) {
            try {
                String password = mSettings.getString(PREFERENCES_PASSWORD_KEY, "");

                String userJSONString = mSettings.getString(PREFERENCES_USER_KEY,"");
                if(!userJSONString.equals("") && !password.equals("")) {
                    JSONObject userJSON = new JSONObject(userJSONString);
                    mUser = new WebfaunaUser(userJSON, password);
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
                    } else {
                        mLocale = locale;
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

    public WebfaunaUser getUser() {
        return mUser;
    }
    public void setUser(WebfaunaUser user) {
        mUser = user;

        //store user
        if(mUser != null) {
            try {
                JSONObject userJSON = mUser.toJSON();
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(PREFERENCES_USER_KEY,userJSON.toString());
                editor.putString(PREFERENCES_PASSWORD_KEY,mUser.getPassword());
                editor.commit();
            } catch (Exception ex) {
                Log.i(LOG,"Store user",ex);
            }
        } else {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.remove(PREFERENCES_USER_KEY);
            editor.remove(PREFERENCES_PASSWORD_KEY);
            editor.commit();
        }

        informSubscribersAboutUserChange();
    }

    public Locale getLocale() {
        return mLocale;
    }

    public void setLocale(Locale locale) {
        mLocale = locale;

        //store locale
        if(mLocale != null) {
            try {
                SharedPreferences.Editor editor = mSettings.edit();
                editor.putString(PREFERENCES_LOCALE_KEY,mLocale.getLanguage());
                editor.commit();
            } catch (Exception ex) {
                Log.i(LOG,"Store user",ex);
            }
        } else {
            SharedPreferences.Editor editor = mSettings.edit();
            editor.remove(PREFERENCES_LOCALE_KEY);
            editor.commit();
        }

        informSubscribersAboutLocaleChange();
    }

    public List<Locale> getSupportedLocales() {
        if(mSupportedLocales == null) {
            mSupportedLocales = new ArrayList<Locale>();
            mSupportedLocales.add(Locale.FRENCH);
            mSupportedLocales.add(Locale.GERMAN);
            mSupportedLocales.add(Locale.ITALIAN);
            mSupportedLocales.add(Locale.ENGLISH);
        }

        return mSupportedLocales;
    }

    /*
    Subscription mechanism
     */

    ArrayList<SettingsManagerBroadcastSubscriber> mSubscribers;

    public static interface SettingsManagerBroadcastSubscriber {
        public void settingsUserChanged(WebfaunaUser user);
        public void settingsLocaleChanged(Locale locale);
    }

    public void subscribe(SettingsManagerBroadcastSubscriber subscriber) {
        if(!mSubscribers.contains(subscriber)) {
            mSubscribers.add(subscriber);
        }
    }

    public void unsubscribe(SettingsManagerBroadcastSubscriber subscriber) {
        if(mSubscribers.contains(subscriber)) {
            mSubscribers.remove(subscriber);
        }
    }

    private void informSubscribersAboutUserChange() {
        for(SettingsManagerBroadcastSubscriber subscriber : mSubscribers) {
            subscriber.settingsUserChanged(mUser);
        }
    }

    private void informSubscribersAboutLocaleChange() {
        for(SettingsManagerBroadcastSubscriber subscriber : mSubscribers) {
            subscriber.settingsLocaleChanged(mLocale);
        }
    }
}
