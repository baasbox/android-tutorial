package com.baasbox.android.pinbox.login;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseActivity;

/**
 * Created by Andrea Tortorella on 26/12/13.
 */
public class LoginActivity extends BaseActivity {

    private final static String LOGIN_FRAGMENT_TAG = "LOGIN_TAG";

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_USERNAME = "com.baasbox.android.pinbox.auth.extra.USERNAME";

    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.baasbox.android.pinbox.auth.extra.EMAIL";

    private final ActionCallbacks mActionCallbacks = new ActionCallbacks();

    private LoginFragment mLoginFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(LoginFragment.newInstance(), LOGIN_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment instanceof LoginFragment) {
            mLoginFragment = (LoginFragment) fragment;
            mLoginFragment.setAttemptLoginListener(mActionCallbacks);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = true;
        switch (item.getItemId()) {
            case R.id.action_forgot_password:
                break;
            default:
                handled = false;
        }
        return handled || super.onOptionsItemSelected(item);
    }

    private class ActionCallbacks implements LoginFragment.OnAttemptLoginListener {
        @Override
        public void onAttemptLogin(String username, String password, String email) {

        }
    }
}
