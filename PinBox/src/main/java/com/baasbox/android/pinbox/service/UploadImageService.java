package com.baasbox.android.pinbox.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.baasbox.android.*;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.pinbox.Contract;
import com.baasbox.android.pinbox.utils.Utils;

import java.io.File;

/**
 * Created by Andrea Tortorella on 05/02/14.
 */
public class UploadImageService extends IntentService {
    private final static String TITLE_EXTRA = "title_extra_key";
    private final static String TAG = "UploadImageService";

    private ContentResolver resolver;

    public UploadImageService() {
        super(TAG);
    }

    public static void saveAnduploadImage(Context context, Uri imageUri, String title) {
        Intent intent = new Intent(context, UploadImageService.class);
        intent.setData(imageUri);
        intent.putExtra(TITLE_EXTRA, title);
        context.startService(intent);
    }

    private Uri insertIntoProvider(String title, Uri contentUri) {
        ContentValues values = new ContentValues();
        values.put(Contract.Image._DATA, contentUri.toString());
        values.put(Contract.Image._TITLE, title);
        return resolver.insert(Contract.Image.CONTENT_URI, values);
    }

    private void updateImageState(Uri target, String serverId) {
        ContentValues values = new ContentValues();
        values.put(Contract.Image._SERVER_ID, serverId);
        resolver.update(target, values, null, null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.resolver = getContentResolver();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String title = intent.getStringExtra(TITLE_EXTRA);
        Uri uri = intent.getData();

        Utils.logStep("INSERTING " + uri);
        Uri providerUri = insertIntoProvider(title, uri);
        Utils.logStep("INSERTED " + providerUri);

        File media = Utils.getMediaFile(this, uri);

        BaasACL acl = new BaasACL();
        acl.grantRoles(Grant.READ, Role.friendsOf(BaasUser.current().getName()));
        BaasFile file;
        if (title != null) {
            file = new BaasFile(new JsonObject().putString("title", title));
        } else {
            file = new BaasFile();
        }
        Utils.logStep("UPLOADING");
        BaasResult<BaasFile> res = file.uploadSync(acl, media);
        if (res.isSuccess()) {
            BaasFile fileRes = res.value();
            String idOnTheServer = fileRes.getId();
            Utils.logStep("UPDATING ");
            updateImageState(providerUri, idOnTheServer);
            Utils.logStep("UPDATED");
        } else {
            Utils.logStep("Failure " + res.error().getMessage());
        }

    }
}
