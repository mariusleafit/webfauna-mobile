package ch.leafit.webfauna.gdc.styles;


import android.graphics.Color;
import android.graphics.Typeface;
import ch.leafit.gdc.GDCStringDataField;
import ch.leafit.gdc.styles.GDCDataFieldStyle;
import ch.leafit.webfauna.config.Config;

/**
 * Created by marius on 07/07/14.
 */
public class GDCStringDataFieldStyle  extends GDCDataFieldStyle<GDCStringDataField> {
    @Override
    public void applyStyleToField(GDCStringDataField field) {
        field.mView.setBackgroundColor(Config.gdcDataFieldBackgroundColor);
        field.mLblFieldName.setTextSize(20);
        field.mLblFieldName.setTypeface(Typeface.DEFAULT_BOLD);

        field.mTxtValue.setTextColor(Config.gdcDataFieldValueFontColor);
        field.mTxtValue.setBackgroundColor(Color.argb(30,255,255,255));

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
            default:
                field.mLblFieldName.setTextColor(Config.gdcDataFieldNameFontColor);
                break;
        }

        super.applyStyleToField(field);
        if(field.isDisabled()) {
            field.mTxtValue.setEnabled(false);
        } else {
            field.mTxtValue.setEnabled(true);
        }

    }

}