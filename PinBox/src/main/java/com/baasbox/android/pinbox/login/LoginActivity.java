package com.baasbox.android.pinbox.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baasbox.android.BAASBox;
import com.baasbox.android.BaasAccount;
import com.baasbox.android.BaasPerson;
import com.baasbox.android.BaasResult;
import com.baasbox.android.RequestToken;
import com.baasbox.android.pinbox.MainActivity;
import com.baasbox.android.pinbox.PinBox;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseActivity;

/**
 * Created by Andrea Tortorella on 26/12/13.
 */
public class LoginActivity extends BaseActivity {
    private final String SUSPENDED_SIGNUP = "suspended_signup";
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

    private BAASBox box;

    private RequestToken signupRequest;
    private RequestToken loginRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        box = PinBox.getBaasBox();
//
//        if (savedInstanceState!=null){
//            signupRequest = savedInstanceState.getParcelable(SUSPENDED_SIGNUP);
//            loginRequest = savedInstanceState.getParcelable(SUSPENDED_LOGIN);
//        }

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
        signupRequest = PinBox.getBaasBox().resume(SUSPENDED_SIGNUP, this, signupHandler);
        loginRequest = PinBox.getBaasBox().resume(SUSPENDED_LOGIN, this, signupHandler);
        if (signupRequest != null || loginRequest != null) {
            mLoginFragment.showLoginProgress(true, "Signing in...");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (signupRequest != null) {
            PinBox.getBaasBox().suspend(SUSPENDED_SIGNUP, signupRequest);
        }
        if (loginRequest != null) {
            PinBox.getBaasBox().suspend(SUSPENDED_LOGIN, loginRequest);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SUSPENDED_SIGNUP, signupRequest);
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

    protected void failLogin(String reason) {
        Toast.makeText(this, "Failed " + reason, Toast.LENGTH_LONG).show();
    }

    private static BAASBox.BAASHandler<Void, LoginActivity> signupHandler = new BAASBox.BAASHandler<Void, LoginActivity>() {
        @Override
        public void handle(BaasResult<Void> result, LoginActivity loginActivity) {
            if (result.isSuccess()) {
                loginActivity.completeLogin();
            } else {
                loginActivity.failLogin(result.error().getMessage());
            }
            if (loginActivity.signupRequest != null) {
                loginActivity.signupRequest = null;
            }
            if (loginActivity.loginRequest != null) {
                loginActivity.loginRequest = null;
            }
        }
    };


    private class ActionCallbacks implements LoginFragment.OnAttemptLoginListener {
        @Override
        public void onAttemptLogin(boolean newUser, String username, String password, String email) {
            mLoginFragment.showLoginProgress(true, "Signin...");
            BaasAccount account = new BaasAccount(username, password);
            if (newUser) {
                if (email != null) {
                    account.getScope(BaasPerson.Scope.PRIVATE).putString("email", email);
                }
                signupRequest = account.signup(box, LoginActivity.this, signupHandler);
            } else {
                loginRequest = account.login(box, LoginActivity.this, signupHandler);
            }
        }
    }
}
