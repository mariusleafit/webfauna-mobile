package ch.leafit.webfauna.models;

import android.content.res.Resources;

/**
 * Created by marius on 10/07/14.
 */
public interface WebfaunaValidatable {
    public WebfaunaValidationResult getValidationResult(Resources res);
}
