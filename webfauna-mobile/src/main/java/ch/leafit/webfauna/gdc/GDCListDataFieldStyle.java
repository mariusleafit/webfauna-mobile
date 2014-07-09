package ch.leafit.webfauna.gdc;

import android.graphics.Color;
import android.graphics.Typeface;
import ch.leafit.gdc.GDCListDataField;
import ch.leafit.gdc.styles.GDCDataFieldStyle;
import ch.leafit.webfauna.config.Config;

/**
 * Created by marius on 07/07/14.
 */
public class GDCListDataFieldStyle  extends GDCDataFieldStyle<GDCListDataField> {
    @Override
    public void applyStyleToField(GDCListDataField field) {
        field.mView.setBackgroundColor(Config.gdcDataFieldBackgroundColor);


        field.mLblFieldName.setTextSize(20);
        field.mLblFieldName.setTypeface(Typeface.DEFAULT_BOLD);

        field.mLblValue.setTextSize(15);
        field.mLblValue.setTypeface(Typeface.DEFAULT);
        field.mLblValue.setTextColor(Config.gdcDataFieldValueFontColor);

        field.mLblDisclosureIndicator.setTextColor(Config.gdcDataFieldDisclosureIndicatorColor);
        field.mLblDisclosureIndicator.setTextSize(20);
        field.mLblDisclosureIndicator.setTypeface(Typeface.DEFAULT_BOLD);


        /*marking*/
        switch (field.getMarking()) {
            case MARKED_AS_INVALID:
                field.mLblFieldName.setTextColor(Config.gdcDataFieldNameInValidFontColor);
                break;
            case MARKED_AS_VALID:
                field.mLblFieldName.setTextColor(Config.gdcDataFieldNameValidFontColor);
                break;
            case NOT_MARKED:
                field.mLblFieldName.setTextColor(Config.gdcDataFieldNameFontColor);
                break;
        }

        super.applyStyleToField(field);
    }
}
