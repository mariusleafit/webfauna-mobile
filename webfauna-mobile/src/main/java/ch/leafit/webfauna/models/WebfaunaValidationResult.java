package ch.leafit.webfauna.models;

/**
 * Created by marius on 10/07/14.
 */
public class WebfaunaValidationResult {
    private boolean mIsValid;
    private String mValidationMessage;

    public WebfaunaValidationResult(boolean isValid, String validationMessage) {
        mIsValid = isValid;
        mValidationMessage = validationMessage;
    }

    public WebfaunaValidationResult() {

    }

    public boolean isValid() {
        return mIsValid;
    }
    public void setIsValid(boolean isValid) {
        mIsValid = isValid;
    }

    public String getValidationMessage() {
        return mValidationMessage;
    }
    public void setValidationMessage(String validationMessage) {
        mValidationMessage = validationMessage;
    }
}
