package com.baasbox.android.pinbox.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.baasbox.android.*;
import com.baasbox.android.pinbox.MainActivity;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseActivity;

/**
 * Created by Andrea Tortorella on 26/12/13.
 */
public class LoginActivity extends BaseActivity {
    private final String SUSPENDED_LOGIN = "suspended_login";
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


    private RequestToken loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            loginRequest = savedInstanceState.getParcelable(SUSPENDED_LOGIN);
        }
        setContentView(R.layout.activity_login);
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
    protected void onResume() {
        super.onResume();
        boolean show = false;
        if (loginRequest != null) {
            loginRequest.resume(handler);
            show = true;
        }

        if (show) {
            mLoginFragment.showLoginProgress(true, "Signing in...");
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (loginRequest != null) {
            loginRequest.suspend();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SUSPENDED_LOGIN, loginRequest);
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

    protected void completeLogin() {
        mLoginFragment.showLoginProgress(false, null);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    protected void failLogin(BaasException reason) throws RuntimeException {
        Toast.makeText(this, "Failed " + reason.getMessage(), Toast.LENGTH_LONG).show();
        mLoginFragment.showLoginProgress(false, null);
    }


    private final BaasHandler<BaasUser> handler = new BaasHandler<BaasUser>() {
        @Override
        public void handle(BaasResult<BaasUser> result) {
            if (result.isSuccess()) {
                completeLogin();
            } else {
                failLogin(result.error());
            }
            loginRequest = null;

        }
    };


    private class ActionCallbacks implements LoginFragment.OnAttemptLoginListener {
        @Override
        public void onAttemptLogin(boolean newUser, String username, String password, String email) {
            mLoginFragment.showLoginProgress(true, "Signin...");

            BaasUser user = BaasUser.withUserName(username);
            user.setPassword(password);
            if (newUser) {
                if (email != null) {
                    user.getScope(BaasUser.Scope.PRIVATE).putString("email", email);
                }

                loginRequest = user.signup(handler);
            } else {
                loginRequest = user.login(handler);
            }
        }
    }
}
