package com.baasbox.android.pinbox.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.baasbox.android.BaasUser;
import com.baasbox.android.RequestToken;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseFragment;
import com.baasbox.android.pinbox.gallery.UploadFragment;
import com.baasbox.android.pinbox.utils.Intents;
import com.baasbox.android.pinbox.utils.Utils;

/**
 * Created by eto on 26/12/13.
 */
public class ProfileFragment extends BaseFragment {
    private final static int CHOOSE_PROFILE_IMAGE = 2;
    private final static String SAVE_PROFILE_PICTURE = "SAVE_PROFILE_PICTURE";

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    private TextView mEmail;
    private TextView mUsername;
    private ImageView mProfileImage;
    private Uri mSavePictureUri;
    private RequestToken t;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSavePictureUri = savedInstanceState.getParcelable(SAVE_PROFILE_PICTURE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mSavePictureUri != null) {
            outState.putParcelable(SAVE_PROFILE_PICTURE, mSavePictureUri);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_PROFILE_IMAGE) {
            Uri imageUri = Intents.processImageResult(resultCode, data, mSavePictureUri);
            if (imageUri != null) {
                UploadFragment.show(getFragmentManager(), imageUri);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        mEmail = (TextView) v.findViewById(R.id.tv_email_set);
        mUsername = (TextView) v.findViewById(R.id.tv_username);
        mProfileImage = (ImageView) v.findViewById(R.id.iv_profile_image);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSavePictureUri = Utils.generateUniqueFileUri();
                Intents.importPicture(getActivity(), CHOOSE_PROFILE_IMAGE, getString(R.string.import_picture), mSavePictureUri);
            }
        });
        BaasUser user = BaasUser.current();
//        if (user!=null){
//            mEmail.setText(user.getScope(BaasUser.Scope.PRIVATE).getString("email","NOMAIL"));
//            mUsername.setText("Welcome "+user.getName());
//        }
        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}
