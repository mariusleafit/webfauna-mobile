package ch.leafit.webfauna.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import ch.leafit.ul.list_items.ULListItemDataModel;
import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by marius on 18/07/14.
 */
public class LocaleListEntry implements ULListItemDataModel{

    private Locale mLocale;

    public LocaleListEntry(Locale locale) throws Exception{
        if(locale == null)
            throw new Exception("locale null");
        mLocale = locale;
    }

    public Locale getLocale() {
        return mLocale;
    }

    public LocaleListEntry(Parcel in) {
        readFromParcel(in);
    }
    /*
    ULListItemDataModel
     */

    @Override
    public String getTitle() {
        return mLocale.getDisplayLanguage();
    }

    @Override
    public String getSubtitle() {
        return "";
    }

    @Override
    public int getImageResId() {
        return 0;
    }

    /*
     Parcelable
     */
    public static final Parcelable.Creator<LocaleListEntry> CREATOR = new Parcelable.Creator<LocaleListEntry>() {
        public LocaleListEntry createFromParcel(Parcel in ) {
            return new LocaleListEntry(in);
        }

        public LocaleListEntry[] newArray(int size) {
            return new LocaleListEntry[size];
        }
    };

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mLocale);
    }

    private void readFromParcel(Parcel in) {
        mLocale = (Locale)in.readSerializable();
    }

    public int describeContents() {
        return 0;
    }
}
