package ch.leafit.webfauna.gdc.styles;

import android.graphics.Typeface;
import ch.leafit.gdc.GDCDateDataField;
import ch.leafit.gdc.styles.GDCDataFieldStyle;
import ch.leafit.webfauna.config.Config;

/**
 * Created by marius on 07/07/14.
 */
public class GDCDateDataFieldStyle extends GDCDataFieldStyle<GDCDateDataField> {
    @Override
    public void applyStyleToField(GDCDateDataField field) {
        field.mView.setBackgroundColor(Config.gdcDataFieldBackgroundColor);

        field.mLblFieldName.setTextSize(20);

        field.mLblDateTitle.setTextColor(Config.gdcDataFieldNameFontColor);
        field.mLblDateTitle.setTextSize(15);
        field.mLblDateTitle.setTypeface(Typeface.DEFAULT_BOLD);

        field.mLblTimeTitle.setTextColor(Config.gdcDataFieldNameFontColor);
        field.mLblTimeTitle.setTextSize(15);
        field.mLblTimeTitle.setTypeface(Typeface.DEFAULT_BOLD);

        field.mLblTime.setTextColor(Config.gdcDataFieldValueFontColor);
        field.mLblTime.setTextSize(15);

        field.mLblDate.setTextColor(Config.gdcDataFieldValueFontColor);
        field.mLblDate.setTextSize(15);

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
