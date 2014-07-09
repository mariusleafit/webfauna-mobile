package ch.leafit.webfauna.webservice;

import android.util.Log;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaRealm;
import ch.leafit.webfauna.models.WebfaunaRealmValue;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.util.ArrayList;

/**
 * Created by marius on 09/07/14.
 */
public class WebfaunaWebserviceThesaurus {

    public static WebfaunaRealm getRealmFromWebservice(String realmRestID, OutParam<Exception> outException) {
        WebfaunaRealm returnRealm = null;

        if (realmRestID != null && realmRestID != "") {
            String url = Config.webfaunaWebserviceBaseURL + "/thesaurus/realms/" + realmRestID;

            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpResponse response;
            try {
                //accept all certificates if debug is enabled
                if(Config.debug) {
                    // Accept all certificate
                    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(null, null);

                    SSLSocketFactory sf = new TrustAllSSLSocketFactory(trustStore);
                    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                    SchemeRegistry registry = new SchemeRegistry();
                    registry.register(new Scheme("https", sf, Config.webfaunaWebservicePort));

                    ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpClient.getParams(), registry);
                    httpClient = new DefaultHttpClient(ccm, httpClient.getParams());
                    // End ------------------
                }

                /*set timeout*/
                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), Config.webfaunaWebserviceRequestTimeout);

                /*start request*/
                BasicHttpContext localContext = new BasicHttpContext();
                response = httpClient.execute(new HttpGet(url), localContext);

                /*check if request went well*/
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    /* get string response*/
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    String responseString = out.toString();

                    /*parse JSON*/
                    try{
                        JSONObject parsedJSON = new JSONObject(responseString);

                        /*navigate JSON to find Object*/
                        if(parsedJSON != null) {
                            JSONArray resource = parsedJSON.getJSONArray("resource");
                            if(resource != null && resource.length() > 0) {
                                JSONObject resultJSON = resource.getJSONObject(0);
                                if(resultJSON != null) {
                                    returnRealm = new WebfaunaRealm(resultJSON);
                                }
                            }
                        }

                        if(returnRealm == null) {
                            throw new Exception("WebfaunaWebserviceThesaurus - getRealmFromWebservice: Received malformed JSON from server");
                        }

                    } catch(JSONException jsonEx) {
                        Log.e("WebfaunaWebserviceThesaurus - getRealmFromWebservice", "could not parse JSON: " + responseString);
                        throw jsonEx;
                    }


                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception ex) {
                Log.e("WebfaunaWebserviceThesaurus - getRealmFromWebservice", "error", ex);
                outException.setValue(ex);
            }
        }
        return returnRealm;
    }

    public static ArrayList<WebfaunaRealmValue> getRealmValuesFromWebservice(String realmRestID, String languageCode, OutParam<Exception> outException) {
        ArrayList<WebfaunaRealmValue> returnRealmValues = null;

        if (realmRestID != null && realmRestID != "" && languageCode != null && languageCode != "") {
            String url = Config.webfaunaWebserviceBaseURL + "/thesaurus/realms/" + realmRestID + "/values/"+languageCode;

            DefaultHttpClient httpClient = new DefaultHttpClient();

            HttpResponse response;
            try {
                //accept all certificates if debug is enabled
                if(Config.debug) {
                    // Accept all certificate
                    KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(null, null);

                    SSLSocketFactory sf = new TrustAllSSLSocketFactory(trustStore);
                    sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                    SchemeRegistry registry = new SchemeRegistry();
                    registry.register(new Scheme("https", sf, Config.webfaunaWebservicePort));

                    ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpClient.getParams(), registry);
                    httpClient = new DefaultHttpClient(ccm, httpClient.getParams());
                    // End ------------------
                }

                /*set timeout*/
                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), Config.webfaunaWebserviceRequestTimeout);

                /*start request*/
                BasicHttpContext localContext = new BasicHttpContext();
                response = httpClient.execute(new HttpGet(url), localContext);

                /*check if request went well*/
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    /* get string response*/
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    String responseString = out.toString();

                    /*parse JSON*/
                    try{
                        JSONObject parsedJSON = new JSONObject(responseString);

                        /*navigate JSON to find Object*/
                        if(parsedJSON != null) {
                            JSONArray resource = parsedJSON.getJSONArray("resource");
                            if(resource != null && resource.length() > 0) {
                                returnRealmValues = new ArrayList<WebfaunaRealmValue>();
                                for(int i = 0; i < resource.length(); i++) {
                                    JSONObject resultJSON = resource.getJSONObject(i);
                                    if(resultJSON != null) {
                                        WebfaunaRealmValue tmpRealmValue = new WebfaunaRealmValue(resultJSON);
                                        if(tmpRealmValue != null) {
                                            returnRealmValues.add(tmpRealmValue);
                                        }
                                    }
                                }

                            }
                        }

                        if(returnRealmValues == null) {
                            throw new Exception("WebfaunaWebserviceThesaurus - getRealmValuesFromWebservice: Received malformed JSON from server");
                        }

                    } catch(JSONException jsonEx) {
                        Log.e("WebfaunaWebserviceThesaurus - getRealmValuesFromWebservice", "could not parse JSON: " + responseString);
                        throw jsonEx;
                    }


                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception ex) {
                Log.e("WebfaunaWebserviceThesaurus - getRealmValuesFromWebservice", "error", ex);
                outException.setValue(ex);
            }
        }
        return returnRealmValues;
    }
}
