package com.baasbox.android.pinbox.gallery;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import com.baasbox.android.pinbox.R;

import java.io.IOException;

/**
 * Created by eto on 11/01/14.
 */
public class UploadFragment extends DialogFragment {
    private final static String UPLOAD_FRAGMENT = "UPLOAD_FRAGMENT";
    private final static String IMAGE_URI = "IMAGE_URI";
    private final static String IS_PROFILE_IMAGE = "IS_PROFILE";

    private ImageView mImagePreview;
    private EditText mImageTitle;
    private Bitmap mBitmap;
    private Uri mImageUri;
    private OnUploadConfirmedListener mListener;

    public static interface OnUploadConfirmedListener {
        public void onUploadConfirmed(Uri imageUri, String title);
    }

    public void setOnUploadConfirmedListener(OnUploadConfirmedListener listener) {
        mListener = listener;
    }

    public static UploadFragment show(FragmentManager manager, Uri bitmapUri, boolean profile) {
        UploadFragment f = new UploadFragment();
        Bundle args = new Bundle();
        args.putParcelable(IMAGE_URI, bitmapUri);
        f.setArguments(args);
        f.show(manager, UPLOAD_FRAGMENT);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUri = getArguments().getParcelable(IMAGE_URI);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        final View v = inflater.inflate(R.layout.fragment_add_picture, null);
        mImagePreview = (ImageView) v.findViewById(R.id.image_preview);
        mImageTitle = (EditText) v.findViewById(R.id.input_title);
        Bitmap bmp = loadBitmap(getActivity().getContentResolver(), mImageUri);
        mImagePreview.setImageBitmap(bmp);
        builder.setView(v);
        builder.setPositiveButton("Upload", fDialogClick);
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }

    private final DialogInterface.OnClickListener fDialogClick = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    if (mListener != null) {
                        String title = mImageTitle.getText().toString();
                        mListener.onUploadConfirmed(mImageUri, title);
                    }
                    break;
            }
        }
    };

    private static Bitmap loadBitmap(ContentResolver resolver, Uri imageUri) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(resolver, imageUri);
            return bmp;
        } catch (IOException e) {
            Log.e("TESTING", e.getMessage(), e);
        }
        return null;
    }

}
