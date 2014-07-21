package ch.leafit.webfauna.webservice;

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
                //String url = Config.webfaunaWebserviceBaseURL + "/observations/" + observationRestID + "/files";
                String url = "http://posttestserver.com/post.php?dump&html&dir=maurius";


                HttpResponse response;
                try {
                    //set connectionParams
                    HttpParams httpParameters = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParameters, Config.webfaunaWebserviceRequestTimeout);
                    HttpConnectionParams.setSoTimeout(httpParameters,Config.webfaunaWebserviceRequestTimeout);
                    HttpProtocolParams.setUseExpectContinue(httpParameters, true);


                    DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);

//                    //accept all certificates if debug is enabled
//                    if (Config.debug) {
//                        // Accept all certificate
//                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//                        trustStore.load(null, null);
//
//                        SSLSocketFactory sf = new TrustAllSSLSocketFactory(trustStore);
//                        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
//
//                        SchemeRegistry registry = new SchemeRegistry();
//                        registry.register(new Scheme("https", sf, Config.webfaunaWebservicePort));
//                        registry.register(new Scheme("http", sf, 80));
//
//                        ClientConnectionManager ccm = new ThreadSafeClientConnManager(httpClient.getParams(), registry);
//                        httpClient = new DefaultHttpClient(ccm, httpClient.getParams());
//                        // End ------------------
//                    }
//                    // Basic authentication
//                    AuthScope as = AuthScope.ANY;
//                    UsernamePasswordCredentials upc = new UsernamePasswordCredentials(username, password);
//                    ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(as, upc);
//                    BasicHttpContext localContext = new BasicHttpContext();
//                    BasicScheme basicAuth = new BasicScheme();
//                    localContext.setAttribute("preemptive-auth", basicAuth);
//                    // End ----------------

                /*set timeout*/

                /*start request*/


                /*create post and put data*/


                    HttpPost httpPost = new HttpPost(url);


                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    //InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(fileData.array()),"image/jpeg","file");

                    //InputStream in = new ByteArrayInputStream(fileData.array());
                    //ContentBody mimePart = new InputStreamBody(in, "image/jpeg","file");

                   //entity.addPart("file",mimePart);



                    entity.addPart("test", new StringBody("asdfsfa"));

                    httpPost.setEntity(entity);



                    //Log.i("content-type", httpPost.getFirstHeader("Content-Type").getValue());

                    response = httpClient.execute(httpPost/*, localContext*/);

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

                } catch (Exception ex) {
                    Log.e("WebfaunaWebserviceObservation - postObservation", "error", ex);
                    outEx.setValue(ex);
                    success = false;
                }

        }
        return success;
    }


    private static class ByteBufferBackedInputStream extends InputStream{

        ByteBuffer buf;
        ByteBufferBackedInputStream( ByteBuffer buf){
            this.buf = buf;
        }
        public synchronized int read() throws IOException {
            if (!buf.hasRemaining()) {
                return -1;
            }
            return buf.get();
        }
        public synchronized int read(byte[] bytes, int off, int len) throws IOException {
            len = Math.min(len, buf.remaining());
            buf.get(bytes, off, len);
            return len;
        }
    }
}
