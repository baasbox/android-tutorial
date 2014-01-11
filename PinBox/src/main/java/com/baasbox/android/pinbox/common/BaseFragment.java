package com.baasbox.android.pinbox.common;


import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Andrea Tortorella on 26/12/13.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
}
