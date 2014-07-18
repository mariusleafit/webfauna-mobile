package ch.leafit.webfauna.webservice;

import android.util.Log;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaGroup;
import ch.leafit.webfauna.models.WebfaunaObservation;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;

/**
 * Created by marius on 14/07/14.
 */
public class WebfaunaWebserviceObservation {
    public static boolean postObservationToWebservice(String observationGUID, OutParam<Exception> outEx) {
        boolean success = false;

        if (observationGUID != null && outEx != null) {

            /*get Observation from DataDispatcher*/
            WebfaunaObservation observation = DataDispatcher.getInstantce().getObservation(observationGUID);

            /*temporarily change coordinates*/
            if(observation.getWebfaunaLocation() != null) {
                observation.getWebfaunaLocation().setSwissCoordinatesX(600000);
                observation.getWebfaunaLocation().setSwissCoordinatesY(200000);
            }

            if(observation != null) {
                String url = Config.webfaunaWebserviceBaseURL + "/observations/";

                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpResponse response;
                try {
                    //accept all certificates if debug is enabled
                    if (Config.debug) {
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
                    UsernamePasswordCredentials upc = new UsernamePasswordCredentials("app.cscf@unine.ch", "WebFauna2014");
                    ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(as, upc);
                    BasicHttpContext localContext = new BasicHttpContext();
                    BasicScheme basicAuth = new BasicScheme();
                    localContext.setAttribute("preemptive-auth", basicAuth);
                    // End ----------------

                /*set timeout*/
                    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), Config.webfaunaWebserviceRequestTimeout);

                /*start request*/


                /*create post and put data*/
                    JSONObject jsonObservation = observation.toJSON();
                    if(jsonObservation != null) {

                        /*modify json*/
                        //remove guid, since its only used locally
                        jsonObservation.remove("guid");

                        //remove wgs-coordinates, since its only used locally
                        if(jsonObservation.has("location")) {
                            JSONObject jsonLocation = jsonObservation.getJSONObject("location");
                            jsonLocation.remove("wgsCoordinatesLat");
                            jsonLocation.remove("wgsCoordinatesLng");
                        }

                        HttpPost httpPost = new HttpPost(url);
                        Log.i("JSON", jsonObservation.toString());
                        StringEntity entity = new StringEntity(jsonObservation.toString(), HTTP.UTF_8);
                        entity.setContentType("application/json");
                        httpPost.setEntity(entity);

                        response = httpClient.execute(httpPost, localContext);

                        /*check if request went well*/
                        StatusLine statusLine = response.getStatusLine();
                        /*observation added properly*/
                        if (statusLine.getStatusCode() == 201) {
                        /* get string response*/
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            response.getEntity().writeTo(out);
                            out.close();
                            String responseString = out.toString();

                            Log.i("JSONsuccss:", responseString);

                            success = true;


                        } else {
                            response.getEntity().getContent().close();
                            throw new IOException(statusLine.getReasonPhrase());
                        }
                    }
                } catch (Exception ex) {
                    Log.e("WebfaunaWebserviceObservation - postObservation", "error", ex);
                    outEx.setValue(ex);
                    success = false;
                }
            }
        }
        return success;
    }
}