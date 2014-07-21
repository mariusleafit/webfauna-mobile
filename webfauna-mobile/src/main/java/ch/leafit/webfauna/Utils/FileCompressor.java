package ch.leafit.webfauna.Utils;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by marius on 20/07/14.
 */
public final class FileCompressor {

    private static final String LOG ="FfileCompressor";

    public static Bitmap compressImageToFitSize(String imageUri, double numberOfBytes, int height, int width) throws Exception{
        try {
            //Decode image size
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(new FileInputStream(imageUri),null,options);

            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(options.outWidth/scale/2>=width && options.outHeight/scale/2>=height)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize=scale;


            //compress image if to big
            Bitmap scaledBitmap = BitmapFactory.decodeStream(new FileInputStream(imageUri), null, options2);

            double bitmapFileSize = scaledBitmap.getRowBytes() * scaledBitmap.getHeight();

            if(bitmapFileSize <= numberOfBytes) {
                return scaledBitmap;
            } else {
                //compress scaledBitmap;
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                int quality = 80;
                do {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
                    quality -= 20;
                //while image to big
                } while((scaledBitmap.getRowBytes() * scaledBitmap.getHeight())> numberOfBytes && quality > 0);

                byte[] byteArray = stream.toByteArray();
                stream.close();
                return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            }
        } catch (Exception ex) {
            Log.e("LOG","cannot compress image",ex);
            throw ex;
        }
    }

}
