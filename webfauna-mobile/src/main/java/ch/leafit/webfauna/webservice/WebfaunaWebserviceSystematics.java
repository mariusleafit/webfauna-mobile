package ch.leafit.webfauna.webservice;

import android.util.Log;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaSpecies;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.conn.ssl.SSLSocketFactory;
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
public final class WebfaunaWebserviceSystematics {

    public static WebfaunaGroup getGroupFromWebservice(String groupRestID, OutParam<Exception> outException) {
        WebfaunaGroup returnGroup = null;

        if (groupRestID != null && groupRestID != "") {
            String url = Config.webfaunaWebserviceBaseURL + "/systematics/groups/" + groupRestID;

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
                                    returnGroup = new WebfaunaGroup(resultJSON);
                                }
                            }
                        }

                        if(returnGroup == null) {
                            throw new Exception("WebfaunaWebserviceSystematics - getGroupFromWebservice: Received malformed JSON from server");
                        }

                    } catch(JSONException jsonEx) {
                        Log.e("WebfaunaWebserviceSystematics - getGroupFromWebservice", "could not parse JSON: " + responseString);
                        throw jsonEx;
                    }


                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception ex) {
                Log.e("WebfaunaWebserviceSystematics - getGroupFromWebservice", "error", ex);
                outException.setValue(ex);
            }
        }
        return returnGroup;
    }

    public static ArrayList<WebfaunaSpecies> getSpeciesOfGroupFromWebservice(String groupRestID, OutParam<Exception>  outException) {
        ArrayList<WebfaunaSpecies> returnSpeciesArray = null;

        if (groupRestID != null && groupRestID != "") {
            String url = Config.webfaunaWebserviceBaseURL + "/systematics/groups/" + groupRestID + "/species";

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
                                returnSpeciesArray = new ArrayList<WebfaunaSpecies>();
                                for(int i = 0; i < resource.length(); i++) {
                                    JSONObject resultJSON = resource.getJSONObject(i);
                                    if(resultJSON != null) {
                                        WebfaunaSpecies tmpWebfaunaSpecies = new WebfaunaSpecies(resultJSON,groupRestID);

                                        if(tmpWebfaunaSpecies != null) {
                                            returnSpeciesArray.add(tmpWebfaunaSpecies);
                                        }
                                    }
                                }


                            }
                        }

                        if(returnSpeciesArray == null) {
                            throw new Exception("WebfaunaWebserviceSystematics - getSpeciesOfGroupFromWebservice: Received malformed JSON from server");
                        }

                    } catch(JSONException jsonEx) {
                        Log.e("WebfaunaWebserviceSystematics - getSpeciesOfGroupFromWebservice", "could not parse JSON: " + responseString);
                        throw jsonEx;
                    }


                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception ex) {
                Log.e("WebfaunaWebserviceSystematics - getSpeciesOfGroupFromWebservice", "error", ex);
                outException.setValue(ex);
            }
        }
        return returnSpeciesArray;
    }
}
