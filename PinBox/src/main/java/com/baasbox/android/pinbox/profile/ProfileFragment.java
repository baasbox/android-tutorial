package com.baasbox.android.pinbox.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseFragment;

/**
 * Created by eto on 26/12/13.
 */
public class ProfileFragment extends BaseFragment {

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment, container, false);
        return v;
    }
}
