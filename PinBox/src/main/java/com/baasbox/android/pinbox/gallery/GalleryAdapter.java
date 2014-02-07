package com.baasbox.android.pinbox.gallery;

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
import com.baasbox.android.BaasUser;
import com.baasbox.android.pinbox.Contract;
import com.baasbox.android.pinbox.R;
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
        h.author = (TextView) v.findViewById(R.id.item_author);
        v.setTag(h);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder h = (ViewHolder) view.getTag();
        long id = cursor.getLong(cursor.getColumnIndexOrThrow(Contract.Image._ID));
        String url = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Image._DATA));
        int state = cursor.getInt(cursor.getColumnIndexOrThrow(Contract.Image._STATUS));
        String auhtor = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Image._AUTHOR));
        if (url != null) {
            UrlImageViewHelper.setUrlDrawable(h.thumb, url);
        } else if (state == Contract.Image.STATE_LOADING) {
            h.thumb.setImageResource(R.drawable.ic_refresh);
        }
        String serverId = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Image._SERVER_ID));

        if (serverId == null) {
            ColorFilter cf = new ColorMatrixColorFilter(COLOR_MATRIX);
            h.thumb.setColorFilter(cf);
        } else {
            h.thumb.clearColorFilter();
        }
        String title = cursor.getString(cursor.getColumnIndexOrThrow(Contract.Image._TITLE));
        h.text.setText(title);
        h.author.setText(BaasUser.current().getName().equals(auhtor) ? "ME" : auhtor);
    }

    private static class ViewHolder {
        ImageView thumb;
        TextView text;
        TextView author;
    }
}
