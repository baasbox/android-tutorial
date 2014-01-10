package com.baasbox.android.pinbox.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.baasbox.android.BAASBox;
import com.baasbox.android.BaasAccount;
import com.baasbox.android.BaasDisposer;
import com.baasbox.android.BaasPerson;
import com.baasbox.android.BaasResult;
import com.baasbox.android.pinbox.MainActivity;
import com.baasbox.android.pinbox.PinBox;
import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseActivity;

/**
 * Created by Andrea Tortorella on 26/12/13.
 */
public class LoginActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        box = PinBox.getBaasBox();
//        if (savedInstanceState == null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(LoginFragment.newInstance(), LOGIN_FRAGMENT_TAG)
//                    .commit();
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

    protected void failLogin() {
        Toast.makeText(this, "Failed", Toast.LENGTH_LONG).show();
    }

    private static BAASBox.BAASHandler<Void, LoginActivity> signupHandler = new BAASBox.BAASHandler<Void, LoginActivity>() {
        @Override
        public void handle(BaasResult<Void> result, LoginActivity loginActivity) {
            if (result.isSuccess()) {
                loginActivity.completeLogin();
            } else {
                loginActivity.failLogin();
            }
        }
    };

    private class ActionCallbacks implements LoginFragment.OnAttemptLoginListener {
        @Override
        public void onAttemptLogin(String username, String password, String email) {
            BaasAccount account = new BaasAccount(username, password);
            if (email != null) {
                account.getScope(BaasPerson.Scope.PRIVATE).putString("email", email);
            }
            BaasDisposer disposer = account.signup(box, LoginActivity.this, signupHandler);
        }
    }
}
