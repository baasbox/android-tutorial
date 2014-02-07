package com.baasbox.android.pinbox.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.baasbox.android.*;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.pinbox.Contract;
import com.baasbox.android.pinbox.PinBox;
import com.baasbox.android.pinbox.utils.SyncTimeManager;
import com.baasbox.android.pinbox.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrea Tortorella on 05/02/14.
 */
public class RefreshService extends IntentService {
    private final static String TAG = "REFRESHING";

    private final static String AUTHOR_KEY = "AUTHOR_KEY";

    private SyncTimeManager mSyncTimer;

    public static void doRefresh(Context context) {
        context.startService(new Intent(context, RefreshService.class));
    }

    public static void refreshUser(Context context, String author) {
        context.startService(new Intent(context, RefreshService.class).putExtra(AUTHOR_KEY, author));
    }

    public RefreshService() {
        super("RefreshService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSyncTimer = PinBox.getSyncTimeManager();
    }


    private void insertLoading(String id, String title, String author) {
        ContentValues values = new ContentValues();
        values.put(Contract.Image._SERVER_ID, id);
        values.put(Contract.Image._TITLE, title);
        values.put(Contract.Image._AUTHOR, author);
        values.put(Contract.Image._STATUS, Contract.Image.STATE_LOADING);
        getContentResolver().insert(Contract.Image.CONTENT_URI, values);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "Start refresh");
        String authorFilter = intent.getStringExtra(AUTHOR_KEY);
        Filter f;
        if (authorFilter == null) {
            f = Filter.ANY;
        } else {
            f = Filter.where("_author = ?", authorFilter);
        }
        BaasResult<List<BaasFile>> res = BaasFile.fetchAllSync(f);

        try {
            List<BaasFile> files = res.get();
            Log.d(TAG, "Values obtained " + files.size());
            ArrayList<RequestToken> tokens = new ArrayList<>();
            for (BaasFile file : files) {
                String id = file.getId();
                if (checkIfImageIsStored(id)) continue;
                JsonObject attachedData = file.getAttachedData();
                String title = attachedData != null ? attachedData.getString("title") : "";
                String author = file.getAuthor();
                insertLoading(id, title, author);
                RequestToken token = file.stream(Priority.NORMAL, new SaveToFile(getContentResolver(), title), null);
                tokens.add(token);
            }
            for (RequestToken t : tokens) {
                BaasResult<Boolean> rs = t.await();
                Log.d(TAG, "Val is " + rs.value());
            }

        } catch (BaasException e) {
            Log.e(TAG, "There was an error during refresh: ", e);
        }
    }


    private boolean checkIfImageIsStored(String imageId) {
        Cursor q = getContentResolver().query(Contract.Image.imageByServerId(imageId), null, null, null, null);
        Log.d(TAG, "YOU HAVE THE IMAGE");
        return q.getCount() > 0;
    }

    public static void cleanUpUser(Context context, String name) {
        context.getContentResolver().delete(Contract.Image.CONTENT_URI, Contract.Image._AUTHOR + " = '" + name + "'", null);
    }

    private static class SaveToFile implements DataStreamHandler<Boolean> {
        private Uri fileName;
        private String title;
        private FileOutputStream fout;
        private final ContentResolver resolver;

        SaveToFile(ContentResolver resolver, String title) {
            this.title = title;
            this.resolver = resolver;
        }

        @Override
        public Boolean onData(byte[] bytes, int read, long length, String id, String contentType) throws Exception {
            if (fout == null) {
                Log.d(TAG, "First call to download");
                fileName = Utils.generateUniqueFileUri();
                File f = new File(fileName.getPath());
                fout = new FileOutputStream(f);
            }
            if (bytes != null) {
                fout.write(bytes, 0, read);
            } else {
                fout.close();
                ContentValues values = new ContentValues();
                values.put(Contract.Image._DATA, fileName.toString());
                values.put(Contract.Image._STATUS, Contract.Image.STATE_INSYNC);
                resolver.update(Contract.Image.imageByServerId(id), values, null, null);
            }
            return true;
        }
    }
}
