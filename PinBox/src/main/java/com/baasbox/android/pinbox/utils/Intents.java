package com.baasbox.android.pinbox.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by eto on 10/01/14.
 */
public class Intents {
    private Intents() {
    }

    public static void importPicture(Fragment fragment, int code, String message, Uri captureFileUri) {
        final Context context = fragment.getActivity();
        Intent intent = createIntent(context, message, captureFileUri);
        fragment.startActivityForResult(intent, code);
    }

    public static void importPicture(Activity activity, int code, String message, Uri captureFileUri) {
        Intent intent = createIntent(activity, message, captureFileUri);
        activity.startActivityForResult(intent, code);
    }


    public static Uri processImageResult(int resultCode, Intent data, Uri outputUri) {
        if (resultCode == Activity.RESULT_OK) {
            final boolean isCameraResult = data == null || MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
            Uri selectedImageUri;
            if (isCameraResult) {
                selectedImageUri = outputUri;
            } else {
                selectedImageUri = data == null ? null : data.getData();
            }
            return selectedImageUri;
        }
        return null;
    }

    private static Intent createIntent(Context context, String message, Uri captureFileUri) {
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager pm = context.getPackageManager();

        final List<ResolveInfo> cameraApps = pm != null ?
                pm.queryIntentActivities(capture, 0) :
                Collections.<ResolveInfo>emptyList();

        for (ResolveInfo cameraApp : cameraApps) {
            if (cameraApp.activityInfo != null) {
                final String packageName = cameraApp.activityInfo.packageName;
                final String activityName = cameraApp.activityInfo.name;
                final Intent intent = new Intent(capture);
                intent.setComponent(new ComponentName(packageName, activityName));
                intent.setPackage(packageName);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, captureFileUri);
                cameraIntents.add(intent);
            }
        }

        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        final Intent chooser = Intent.createChooser(galleryIntent, message);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraApps.toArray(new Parcelable[cameraApps.size()]));
        return chooser;
    }

}
