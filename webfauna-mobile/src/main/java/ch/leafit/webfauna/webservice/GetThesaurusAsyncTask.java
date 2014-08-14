package ch.leafit.webfauna.webservice;

import android.os.AsyncTask;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaRealm;
import ch.leafit.webfauna.models.WebfaunaRealmValue;
import ch.leafit.webfauna.models.WebfaunaSpecies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by marius on 09/07/14.
 */
public class GetThesaurusAsyncTask extends AsyncTask<Void,Void,Void> {
    private Callback mCallback;

    private Exception mException;

    private String mLanguageCode;

    private WebfaunaRealm mIdentificationMethodRealm;
    private WebfaunaRealm mPrecisionRealm;
    private WebfaunaRealm mEnvironmentRealm;
    private WebfaunaRealm mMilieuRealm;
    private WebfaunaRealm mStructureRealm;
    private WebfaunaRealm mSubstratRealm;

    public GetThesaurusAsyncTask(Callback callback, String languageCode) {
        mCallback = callback;
        mLanguageCode = languageCode;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        /*download realms*/
        OutParam<Exception> outEx = new OutParam<Exception>();

        /*identificationMethod*/
        mIdentificationMethodRealm = WebfaunaWebserviceThesaurus.getRealmFromWebservice(Config.webfaunaIdentificationMethodRealmRestID,outEx);
        if(mIdentificationMethodRealm != null && outEx.getValue() == null) {
            ArrayList<WebfaunaRealmValue> tmpRealmValues = WebfaunaWebserviceThesaurus.getRealmValuesFromWebservice(Config.webfaunaIdentificationMethodRealmRestID,mLanguageCode,outEx);
            if(tmpRealmValues != null && outEx.getValue() == null) {

                //sort realmValues
                Collections.sort(tmpRealmValues,new Comparator<WebfaunaRealmValue>() {
                    @Override
                    public int compare(WebfaunaRealmValue lhs, WebfaunaRealmValue rhs) {
                        if (lhs.getTitle() != null && rhs.getTitle() != null) {
                            return lhs.getTitle().compareTo(rhs.getTitle());
                        } else {
                            return 0;
                        }

                    }
                });

                mIdentificationMethodRealm.setRealmValues(tmpRealmValues);
            }
        }


        /*precision*/
        if(outEx.getValue() == null) {
            mPrecisionRealm = WebfaunaWebserviceThesaurus.getRealmFromWebservice(Config.webfaunaPrecisionRealmRestID,outEx);
            if(mPrecisionRealm != null && outEx.getValue() == null) {
                ArrayList<WebfaunaRealmValue> tmpRealmValues = WebfaunaWebserviceThesaurus.getRealmValuesFromWebservice(Config.webfaunaPrecisionRealmRestID,mLanguageCode,outEx);
                if(tmpRealmValues != null && outEx.getValue() == null) {

                    //sort realmValues
                    Collections.sort(tmpRealmValues,new Comparator<WebfaunaRealmValue>() {
                        @Override
                        public int compare(WebfaunaRealmValue lhs, WebfaunaRealmValue rhs) {
                            if(lhs.getTitle() != null && rhs.getTitle() != null) {
                                return lhs.getTitle().compareTo(rhs.getTitle());
                            } else {
                                return 0;
                            }

                        }
                    });

                    mPrecisionRealm.setRealmValues(tmpRealmValues);
                }
            }
        }

        /*environment*/
        if(outEx.getValue() == null) {
            mEnvironmentRealm = WebfaunaWebserviceThesaurus.getRealmFromWebservice(Config.webfaunaEnvironmentRealmRestID,outEx);
            if(mEnvironmentRealm != null && outEx.getValue() == null) {
                ArrayList<WebfaunaRealmValue> tmpRealmValues = WebfaunaWebserviceThesaurus.getRealmValuesFromWebservice(Config.webfaunaEnvironmentRealmRestID,mLanguageCode,outEx);
                if(tmpRealmValues != null && outEx.getValue() == null) {


                    //sort realmValues
                    Collections.sort(tmpRealmValues,new Comparator<WebfaunaRealmValue>() {
                        @Override
                        public int compare(WebfaunaRealmValue lhs, WebfaunaRealmValue rhs) {
                            if(lhs.getTitle() != null && rhs.getTitle() != null) {
                                return lhs.getTitle().compareTo(rhs.getTitle());
                            } else {
                                return 0;
                            }

                        }
                    });

                    mEnvironmentRealm.setRealmValues(tmpRealmValues);
                }
            }
        }

        /*milieu*/
        if(outEx.getValue() == null) {
            mMilieuRealm = WebfaunaWebserviceThesaurus.getRealmFromWebservice(Config.webfaunaMilieuRealmRestID,outEx);
            if(mMilieuRealm != null && outEx.getValue() == null) {
                ArrayList<WebfaunaRealmValue> tmpRealmValues = WebfaunaWebserviceThesaurus.getRealmValuesFromWebservice(Config.webfaunaMilieuRealmRestID,mLanguageCode,outEx);
                if(tmpRealmValues != null && outEx.getValue() == null) {

                    //sort realmValues
                    Collections.sort(tmpRealmValues,new Comparator<WebfaunaRealmValue>() {
                        @Override
                        public int compare(WebfaunaRealmValue lhs, WebfaunaRealmValue rhs) {
                            if(lhs.getTitle() != null && rhs.getTitle() != null) {
                                return lhs.getTitle().compareTo(rhs.getTitle());
                            } else {
                                return 0;
                            }

                        }
                    });

                    mMilieuRealm.setRealmValues(tmpRealmValues);
                }
            }
        }

        /*structure*/
        if(outEx.getValue() == null) {
            mStructureRealm = WebfaunaWebserviceThesaurus.getRealmFromWebservice(Config.webfaunaStructureRealmRestID,outEx);
            if(mStructureRealm != null && outEx.getValue() == null) {
                ArrayList<WebfaunaRealmValue> tmpRealmValues = WebfaunaWebserviceThesaurus.getRealmValuesFromWebservice(Config.webfaunaStructureRealmRestID,mLanguageCode,outEx);
                if(tmpRealmValues != null && outEx.getValue() == null) {

                    //sort realmValues
                    Collections.sort(tmpRealmValues,new Comparator<WebfaunaRealmValue>() {
                        @Override
                        public int compare(WebfaunaRealmValue lhs, WebfaunaRealmValue rhs) {
                            if(lhs.getTitle() != null && rhs.getTitle() != null) {
                                return lhs.getTitle().compareTo(rhs.getTitle());
                            } else {
                                return 0;
                            }

                        }
                    });

                    mStructureRealm.setRealmValues(tmpRealmValues);
                }
            }
        }

        /*substrat*/
        if(outEx.getValue() == null) {
            mSubstratRealm = WebfaunaWebserviceThesaurus.getRealmFromWebservice(Config.webfaunaSubstratRealmRestID,outEx);
            if(mSubstratRealm != null && outEx.getValue() == null) {
                ArrayList<WebfaunaRealmValue> tmpRealmValues = WebfaunaWebserviceThesaurus.getRealmValuesFromWebservice(Config.webfaunaSubstratRealmRestID,mLanguageCode,outEx);
                if(tmpRealmValues != null && outEx.getValue() == null) {

                    //sort realmValues
                    Collections.sort(tmpRealmValues,new Comparator<WebfaunaRealmValue>() {
                        @Override
                        public int compare(WebfaunaRealmValue lhs, WebfaunaRealmValue rhs) {
                            if(lhs.getTitle() != null && rhs.getTitle() != null) {
                                return lhs.getTitle().compareTo(rhs.getTitle());
                            } else {
                                return 0;
                            }

                        }
                    });

                    mSubstratRealm.setRealmValues(tmpRealmValues);
                }
            }
        }


        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if(mException == null && mIdentificationMethodRealm != null && mPrecisionRealm != null && mEnvironmentRealm != null && mMilieuRealm != null &&
                mStructureRealm != null && mSubstratRealm != null) {
            mCallback.gotThesaurus(mIdentificationMethodRealm,mPrecisionRealm,mEnvironmentRealm,mMilieuRealm,mStructureRealm,mSubstratRealm);
        } else {
            mCallback.couldNotGetThesaurus(mException);
        }
    }


    public static interface Callback {
        public void gotThesaurus(WebfaunaRealm identificationMethodRealm, WebfaunaRealm precisionRealm, WebfaunaRealm environmentRealm, WebfaunaRealm milieuRealm,
                                 WebfaunaRealm structureRealm, WebfaunaRealm substratRealm);

        public void couldNotGetThesaurus(Exception ex);
    }
}
