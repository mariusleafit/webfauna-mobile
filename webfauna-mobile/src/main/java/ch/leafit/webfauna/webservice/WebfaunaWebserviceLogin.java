package ch.leafit.webfauna.webservice;

import android.util.Log;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaUser;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
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

/**
 * Created by marius on 18/07/14.
 */
public class WebfaunaWebserviceLogin {
    public static WebfaunaUser checkLogin(String email, String password, OutParam<Exception> outEx) {
        WebfaunaUser returnUser = null;

        if (email != null && password != null) {
            String url = Config.webfaunaWebserviceBaseURL + "/";

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
                // Basic authentication
                AuthScope as = AuthScope.ANY;
                UsernamePasswordCredentials upc = new UsernamePasswordCredentials(email, password);
                ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(as, upc);
                BasicHttpContext localContext = new BasicHttpContext();
                BasicScheme basicAuth = new BasicScheme();
                localContext.setAttribute("preemptive-auth", basicAuth);
                // End ----------------

                /*set timeout*/
                HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), Config.webfaunaWebserviceRequestTimeout);

                /*start request*/
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
                                if(resultJSON != null && resultJSON.has("user")) {
                                    returnUser = new WebfaunaUser(resultJSON.getJSONObject("user"), password);
                                }
                            }
                        }

                        if(returnUser == null) {
                            throw new Exception("WebfaunaWebserviceLogin: received malformed JSON from server");
                        }

                    } catch(JSONException jsonEx) {
                        Log.e("WebfaunaWebserviceLogin", "could not parse JSON: " + responseString);
                        throw jsonEx;
                    }
                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (Exception ex) {
                Log.e("WebfaunaWebserviceLogin", "error", ex);
                outEx.setValue(ex);
                returnUser = null;
            }
        }

        return returnUser;
    }
}
