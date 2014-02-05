package com.baasbox.android.pinbox;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by eto on 13/01/14.
 */
public final class Contract {

    private Contract() {
    }

    public final static String AUTHORITY = "com.baasbox.android.pinbox";

    public final static Uri CONTENT_URI = Uri.parse(ContentResolver.SCHEME_CONTENT + "://" + AUTHORITY);

    public final static class Image {
        public static final String DIR_CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.pinbox,image";
        public static final String ITEM_CONTENT_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.pinbox.image";


        public static final Uri imageByServerId(String serverId) {
            return Image.CONTENT_URI.buildUpon().appendPath("serverid").appendPath(serverId).build();
        }

        private Image() {
        }

        public final static String PATH = "image";
        public final static Uri CONTENT_URI = Contract.CONTENT_URI.buildUpon().appendPath(PATH).build();

        public final static String _ID = BaseColumns._ID;
        public final static String _SERVER_ID = "_server_id";
        public final static String _DATA = "_data";
        public final static String _STATUS = "_status";
        public static final String _TITLE = "title";
    }
}
