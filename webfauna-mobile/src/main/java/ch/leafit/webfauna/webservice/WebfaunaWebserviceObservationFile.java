package ch.leafit.webfauna.webservice;

import android.graphics.Bitmap;
import android.util.Log;
import ch.leafit.webfauna.Utils.OutParam;
import ch.leafit.webfauna.config.Config;
import ch.leafit.webfauna.data.DataDispatcher;
import ch.leafit.webfauna.models.WebfaunaObservation;
import ch.leafit.webfauna.models.WebfaunaObservationFile;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.KeyStore;

/**
 * Created by marius on 21/07/14.
 */
public class WebfaunaWebserviceObservationFile {
    public static boolean postObservationFileToWebservice(ByteBuffer fileData, String observationRestID, OutParam<Exception> outEx, String username, String password) {
        boolean success = false;

        if (fileData != null && outEx != null && observationRestID != null) {
            String url = Config.webfaunaWebserviceBaseURL + "/observations/" + observationRestID + "/files";

            HttpResponse response;
            try {
                //set connectionParams
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, Config.webfaunaWebserviceRequestTimeout);
                HttpConnectionParams.setSoTimeout(httpParameters,Config.webfaunaWebserviceRequestTimeout);
                HttpProtocolParams.setUseExpectContinue(httpParameters, false);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                httpClient.getParams().setBooleanParameter("http.protocol.expect-continue",false);


                //accept all certificates if debug is enabled
                if (Config.useSelfSignedSSLCerts) {
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
                UsernamePasswordCredentials upc = new UsernamePasswordCredentials(username, password);
                ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(as, upc);
                BasicHttpContext localContext = new BasicHttpContext();
                BasicScheme basicAuth = new BasicScheme();
                localContext.setAttribute("preemptive-auth", basicAuth);
                // End ----------------


                /*create post and put data*/
                HttpPost httpPost = new HttpPost(url);


                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.STRICT);

                /*create tmpFile (otherwise it won't work with the current version of the http-components api) & add it to entity*/
                File tmpFile = File.createTempFile("image","");
                tmpFile.deleteOnExit();
                FileOutputStream fileOutputStream = new FileOutputStream(tmpFile);
                fileOutputStream.write(fileData.array(),0,fileData.array().length);
                if(!tmpFile.canRead()) {
                    throw new RuntimeException("WebfaunaWebserviceObservationFile: cannot read tmpFile: " + tmpFile.getAbsolutePath());
                }
                entity.addPart("file", new FileBody(tmpFile,"image/jpeg"));


                httpPost.setEntity(entity);

                response = httpClient.execute(httpPost, localContext);


                 /* get string response*/
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                String responseString = out.toString();
                Log.i("Webservice","ObservationFile-Response:" + responseString);

                /*check if request went well*/
                StatusLine statusLine = response.getStatusLine();
                /*observation added properly*/
                if (statusLine.getStatusCode() == 200) {
                    success = true;
                } else {
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }

            } catch (Exception ex) {
                Log.e("WebfaunaWebserviceObservationFile - postObservation", "error", ex);
                outEx.setValue(ex);
                success = false;
            }

        }
        return success;
    }
}
