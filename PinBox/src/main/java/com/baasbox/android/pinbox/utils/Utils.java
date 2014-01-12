package com.baasbox.android.pinbox.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by eto on 10/01/14.
 */
public class Utils {

    public static Uri generateUniqueFileUri() {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return Uri.fromFile(new File(dir, String.format(Locale.US, "capture-%s-.jpg", UUID.randomUUID())));
    }

    private final static String[] PROJ ={MediaStore.Images.Media.DATA};

    public static File getMediaFile(Context context,Uri uri){
        if(uri==null)return null;
        String scheme = uri.getScheme();
        if(ContentResolver.SCHEME_CONTENT.equals(scheme)){
            return getMediaStoreFile(context,uri);
        } else if(ContentResolver.SCHEME_FILE.equals(scheme)){
            return new File(uri.getPath());
        } else {
            return null;
        }

    }
    public static File getMediaStoreFile(Context context,Uri uri){
        Cursor c = null;
        try {
            Log.d("PINBOX",uri.toString());
            c = context.getContentResolver().query(uri,PROJ,null,null,null);
            if (c==null)return null;
            int cidx = c.getColumnIndexOrThrow(PROJ[0]);
            c.moveToFirst();
            return new File(c.getString(cidx));
        }finally {
            if(c!=null){
                c.close();
            }
        }
    }
}
