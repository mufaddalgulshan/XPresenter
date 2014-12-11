package com.saiflimited.xpresenter.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Utils {

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    public static boolean isConnected(Context context) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (activeNetworkInfo != null) && (activeNetworkInfo.isConnected());
    }

    public static Bitmap getBitmapFromBase64(String base64Image) {
        byte[] bytes = Base64.decode(base64Image, 0);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static Uri getUri(String base64Image, String filename) {

        String newEncodedIcon = base64Image.substring(base64Image.indexOf(",") + 1);

        byte[] decodedString = Base64.decode(newEncodedIcon, Base64.DEFAULT);
        Bitmap icon = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        File url = writeImage(icon, filename);
        Uri uri = Uri.fromFile(url);
        return uri;
    }

    private static File writeImage(Bitmap icon, String filename) {

        File directory = Environment.getExternalStorageDirectory();
        if (!directory.exists()) {
            directory.mkdirs();
        }

        OutputStream outputStream = null;
        File iconFile = new File(directory, filename + ".png");
        try {
            outputStream = new FileOutputStream(iconFile);
            Bitmap bitmap = icon;
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return iconFile;
    }
}
