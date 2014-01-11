package com.baasbox.android.pinbox.utils;

import android.net.Uri;
import android.os.Environment;

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
}
