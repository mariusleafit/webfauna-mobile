package ch.leafit.webfauna.gdc;

import android.graphics.Color;
import ch.leafit.gdc.GDCSectionTitleDataField;
import ch.leafit.gdc.styles.GDCDataFieldStyle;
import ch.leafit.webfauna.config.Config;

/**
 * Created by marius on 07/07/14.
 */
public class GDCSectionTitleDataFieldStyle  extends GDCDataFieldStyle<GDCSectionTitleDataField> {
    @Override
    public void applyStyleToField(GDCSectionTitleDataField field) {
        field.mLblTitle.setTextColor(Config.gdcDataFieldSeperatorFontColor);
        field.mLblTitle.setAllCaps(true);

        field.mLblTitle.setTextSize(15);

        field.mView.setBackgroundColor(Config.gdcDataFieldSeperatorBackgroundColor);
        field.mView.setPadding(0,25,0,0);
    }

}