package com.baasbox.android.pinbox.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.baasbox.android.*;
import com.baasbox.android.json.JsonObject;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseFragment;
import com.baasbox.android.pinbox.utils.Intents;
import com.baasbox.android.pinbox.utils.Utils;

import java.io.ByteArrayOutputStream;

/**
 * Created by eto on 26/12/13.
 */
public class ProfileFragment extends BaseFragment {
    public final static int CHOOSE_PROFILE_IMAGE = 4;
    private final static String SAVE_PROFILE_PICTURE = "SAVE_PROFILE_PICTURE";


    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    private TextView mEmail;
    private TextView mUsername;
    private ImageView mProfileImage;
    private Uri mSavePictureUri;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        mEmail = (TextView) v.findViewById(R.id.tv_email_set);
        mUsername = (TextView) v.findViewById(R.id.username_view);
        mProfileImage = (ImageView) v.findViewById(R.id.iv_profile_image);
        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSavePictureUri = Utils.generateUniqueFileUri();
                Intents.importPicture(ProfileFragment.this, CHOOSE_PROFILE_IMAGE, getString(R.string.import_picture), mSavePictureUri);
            }
        });
        BaasUser user = BaasUser.current();
        mEmail.setText(user.getScope(BaasUser.Scope.PRIVATE).getString("email"));
        mUsername.setText(user.getName());

        String profile = user.getScope(BaasUser.Scope.REGISTERED).getString("profile_image");
        if (profile != null) {
        } else {
            mProfileImage.setImageResource(R.drawable.ic_launcher);
        }
        return v;
    }

    private void setProfileImage(Uri profile) {
        Bitmap bmp = Utils.getBitmapThumbnail(profile);
        mProfileImage.setImageBitmap(bmp);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
            byte[] imgJpg = out.toByteArray();
            JsonObject data = new JsonObject()
                    .putBoolean("profile", true);
            BaasFile file = new BaasFile(data);
            BaasACL acl = new BaasACL();
            acl.grantRoles(Grant.READ, Role.friendsOf(BaasUser.current().getName()));
            RequestToken upload = file.upload(acl, imgJpg, uploadHandler);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private BaasHandler<BaasFile> uploadHandler = new BaasHandler<BaasFile>() {
        @Override
        public void handle(BaasResult<BaasFile> res) {

        }
    };

    private BaasHandler<BaasUser> updateUser = new BaasHandler<BaasUser>() {
        @Override
        public void handle(BaasResult<BaasUser> res) {
            Log.d("LOG", "SUCCESS " + res.isSuccess());
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSE_PROFILE_IMAGE) {
            Log.d("LOG", "HANDLING");
            Uri res = Intents.processImageResult(resultCode, data, mSavePictureUri);
            if (res != null) {
                setProfileImage(res);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
