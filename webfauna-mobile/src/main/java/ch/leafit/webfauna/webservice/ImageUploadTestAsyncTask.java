package ch.leafit.webfauna.webservice;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import ch.leafit.webfauna.Utils.FileCompressor;
import ch.leafit.webfauna.config.Config;
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

import java.io.*;
import java.security.KeyStore;

/**
 * Created by marius on 21/07/14.
 */
public class ImageUploadTestAsyncTask extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... params) {

        String url = "http://posttestserver.com/post.php?dump&html&dir=maurius";
        //String url = "https://webfauna-api-test.cscf.ch/api/v1/observations/123388/files";


        HttpResponse response;
        try {
            //set connectionParams
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, Config.webfaunaWebserviceRequestTimeout);
            HttpConnectionParams.setSoTimeout(httpParameters,Config.webfaunaWebserviceRequestTimeout);
            HttpProtocolParams.setUseExpectContinue(httpParameters, true);


            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
                //accept all certificates if debug is enabled
                    if (Config.debug) {
                        // Accept all certificate
                        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                        trustStore.load(null, null);

                        SSLSocketFactory sf = new TrustAllSSLSocketFactory(trustStore);
                        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                        SchemeRegistry registry = new SchemeRegistry();
                        registry.register(new Scheme("https", sf, Config.webfaunaWebservicePort));
                        registry.register(new Scheme("http", sf, 80));

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


            HttpPost httpPost = new HttpPost(url);


            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            //InputStreamBody inputStreamBody = new InputStreamBody(new ByteArrayInputStream(fileData.array()),"image/jpeg","file");

            //InputStream in = new ByteArrayInputStream(fileData.array());
            //ContentBody mimePart = new InputStreamBody(in, "image/jpeg","file");

            //entity.addPart("file",mimePart);

            //File image = new File("/storage/sdcard0/QrDroid/QR_Droid.png");

            Bitmap compressedBitmap = FileCompressor.compressImageToFitSize("/storage/sdcard0/QrDroid/QR_Droid.png", Config.MAX_OBSERVATION_IMAGE_SIZE_BYTES, Config.OBSERVATION_IMAGE_HEIGHT, Config.OBSERVATION_IMAGE_WIDTH);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            File tmpFile = File.createTempFile("image","jpeg");
            tmpFile.deleteOnExit();

            stream.writeTo(new FileOutputStream(tmpFile));

            entity.addPart("file", new FileBody(tmpFile,"image/jpeg"));
            //entity.addPart("test", new StringBody("asdfsfa"));


            //entity.addPart("abc", new InputStreamBody(new ByteArrayInputStream()));

            httpPost.setEntity(entity);



            //Log.i("content-type", httpPost.getFirstHeader("Content-Type").getValue());

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

            } else {
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (Exception ex) {
            Log.e("WebfaunaWebserviceObservation - postObservation", "error", ex);
        }




        return null;
    }
}
