package com.baasbox.android.pinbox.gallery;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.GridView;
import com.baasbox.android.pinbox.Contract;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseFragment;
import com.baasbox.android.pinbox.utils.Intents;
import com.baasbox.android.pinbox.utils.Utils;

public class GalleryFragment extends BaseFragment {
    private final static String SAVE_PICTURE_URI = "save_picture";
    private final static int IMPORT_IMAGE_REQUEST = 2;

    public static interface OnImageChoosen{
        public void onImageChoosen(Uri imageUri);
    }

    public static GalleryFragment newInstance() {
        GalleryFragment fragment = new GalleryFragment();
        return fragment;
    }

    private Uri mSavePictureUri;
    private OnImageChoosen mImageChoiceListener;
    private GridView mGridView;
    private GalleryAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSavePictureUri = savedInstanceState.getParcelable(SAVE_PICTURE_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);
        mGridView = (GridView) v.findViewById(R.id.images_grid);
        mAdapter = new GalleryAdapter(getActivity());
        mGridView.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(R.id.GALLERY_LOADER, null, loaderCallbacks);
    }

    private final LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                    CursorLoader loader = new CursorLoader(getActivity(), Contract.Image.CONTENT_URI, null, null, null, null);
                    return loader;
                }

                @Override
                public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
                    mAdapter.swapCursor(cursor);
                }

                @Override
                public void onLoaderReset(Loader<Cursor> cursorLoader) {
                    mAdapter.swapCursor(null);
                }
            };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.gallery, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = true;
        switch (item.getItemId()) {
            case R.id.share:
                mSavePictureUri = Utils.generateUniqueFileUri();
                Intents.importPicture(this, IMPORT_IMAGE_REQUEST, getString(R.string.import_picture), mSavePictureUri);
                break;
            case R.id.refresh:
            default:
                handled = false;
        }
        return handled || super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSavePictureUri != null) {
            outState.putParcelable(SAVE_PICTURE_URI, mSavePictureUri);
        }
    }

    public void setOnImageChoosenListener(OnImageChoosen imageChoosen){
        mImageChoiceListener = imageChoosen;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_IMAGE_REQUEST) {
            Uri imageUri = Intents.processImageResult(resultCode, data, mSavePictureUri);
            if (imageUri!=null){
                if (mImageChoiceListener!=null){
                    mImageChoiceListener.onImageChoosen(imageUri);
                }
            }
        }
    }
}