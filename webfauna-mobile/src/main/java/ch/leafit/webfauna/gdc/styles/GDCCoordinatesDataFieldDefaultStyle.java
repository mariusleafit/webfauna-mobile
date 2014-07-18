package ch.leafit.webfauna.gdc.styles;

import android.graphics.Typeface;
import ch.leafit.gdc.styles.GDCDataFieldStyle;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.gdc.GDCCoordinatesDataField;

/**
 * Created by marius on 15/07/14.
 */
public class GDCCoordinatesDataFieldDefaultStyle  extends GDCDataFieldStyle<GDCCoordinatesDataField>{

    @Override
    public void applyStyleToField(GDCCoordinatesDataField field) {
        field.mView.setBackgroundColor(Config.gdcDataFieldBackgroundColor);

        //field.mLblFieldName.setTextSize(20);

        field.mLblCH03Title.setTextColor(Config.gdcDataFieldNameFontColor);
        field.mLblCH03Title.setTextSize(15);
        field.mLblCH03Title.setTypeface(Typeface.DEFAULT_BOLD);

        field.mLblWGSTitle.setTextColor(Config.gdcDataFieldNameFontColor);
        field.mLblWGSTitle.setTextSize(15);
        field.mLblWGSTitle.setTypeface(Typeface.DEFAULT_BOLD);

        field.mLblCH03.setTextColor(Config.gdcDataFieldValueFontColor);
        field.mLblCH03.setTextSize(15);

        field.mLblWGS.setTextColor(Config.gdcDataFieldValueFontColor);
        field.mLblWGS.setTextSize(15);

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
