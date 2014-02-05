package com.baasbox.android.pinbox.gallery;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.baasbox.android.pinbox.Contract;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.utils.Utils;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

/**
 * Created by eto on 12/01/14.
 */
class GalleryAdapter extends CursorAdapter {

    private final static float[] COLOR_MATRIX = {
            0.33f, 0.33f, 0.33f, 0, 1.0f,
            0.33f, 0.33f, 0.33f, 0, 1.0f,
            0.33f, 0.33f, 0.33f, 0, 1.0f,
            0, 0, 0, 1, 0
    };

    private final LayoutInflater fInflater;

    public GalleryAdapter(Context context) {
        super(context, null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        fInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View v = fInflater.inflate(R.layout.image_item, viewGroup, false);
        ViewHolder h = new ViewHolder();
        h.thumb = (ImageView) v.findViewById(R.id.item_thumb);
        h.text = (TextView) v.findViewById(R.id.item_title);
        v.setTag(h);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder h = (ViewHolder) view.getTag();
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(Contract.Image._ID));
        Utils.logStep("SHOWING IMAGE?");
        UrlImageViewHelper.setUrlDrawable(h.thumb, ContentUris.withAppendedId(Contract.Image.CONTENT_URI, id).toString());
        String serverId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Image._SERVER_ID));
        if (serverId == null) {
            ColorFilter cf = new ColorMatrixColorFilter(COLOR_MATRIX);
            h.thumb.setColorFilter(cf);
        } else {
            h.thumb.clearColorFilter();
        }
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Image._TITLE));
        h.text.setText(title);
    }

    private static class ViewHolder {
        private ImageView thumb;
        private TextView text;
    }
}
