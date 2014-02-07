package com.baasbox.android.pinbox.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Locale;
import java.util.UUID;

/**
 * Created by eto on 10/01/14.
 */
public class Utils {
    private final static String LOG_TAG = "LOGTAG";

    public static void logStep(String text) {
        Log.d(LOG_TAG, text);
    }

    public static Uri generateUniqueFileUri() {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "images");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return Uri.fromFile(new File(dir, String.format(Locale.US, "capture-%s-.jpg", UUID.randomUUID())));
    }

    private final static String[] PROJ = {MediaStore.Images.Media.DATA};

    public static File getMediaFile(Context context, Uri uri) {
        if (uri == null) return null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            return getMediaStoreFile(context, uri);
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return new File(uri.getPath());
        } else {
            return null;
        }

    }

    public static byte[] saveSmallThumbnail(Uri uri) {
        Bitmap bmp = getBitmapThumbnail(uri);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
        return out.toByteArray();

    }

    public static Bitmap getBitmapThumbnail(Uri uri) {
        File image = new File(uri.getPath());
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if (bounds.outWidth == -1 || bounds.outHeight == -1) {
            return null;
        }
        int original = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight :
                bounds.outWidth;
        BitmapFactory.Options resize = new BitmapFactory.Options();
        resize.inSampleSize = original / 200;
        return BitmapFactory.decodeFile(image.getPath(), resize);

    }

    public static File getMediaStoreFile(Context context, Uri uri) {
        Cursor c = null;
        try {
            Log.d("PINBOX", uri.toString());
            c = context.getContentResolver().query(uri, PROJ, null, null, null);
            if (c == null) return null;
            int cidx = c.getColumnIndexOrThrow(PROJ[0]);
            c.moveToFirst();
            return new File(c.getString(cidx));
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }
}
