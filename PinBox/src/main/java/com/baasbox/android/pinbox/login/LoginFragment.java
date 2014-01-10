package com.baasbox.android.pinbox.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.baasbox.android.pinbox.R;
import com.baasbox.android.pinbox.common.BaseFragment;

/**
 * Created by Andrea Tortorella on 26/12/13.
 */
public class LoginFragment extends BaseFragment {

    public static final String ARG_EMAIL = LoginActivity.EXTRA_EMAIL;
    public static final String ARG_USERNAME = LoginActivity.EXTRA_USERNAME;
    public static final String KEY_PASSWORD = "savekey.PASSWORD";

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(new Bundle());
        return loginFragment;
    }

    public static interface OnAttemptLoginListener {
        public void onAttemptLogin(String username, String password, String email);
    }

    private String mEmail;
    private String mPassword;
    private String mUsername;

    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessaegView;

    private OnAttemptLoginListener mAttemptLoginListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle data = savedInstanceState == null
                ? getArguments()
                : savedInstanceState;
        if (data != null) {
            mUsername = data.getString(ARG_USERNAME);
            mEmail = data.getString(ARG_EMAIL);
            mPassword = data.getString(KEY_PASSWORD);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_login, container, true);

        mLoginFormView = v.findViewById(R.id.login_form);
        mLoginStatusView = v.findViewById(R.id.login_status);

        mUsernameView = (EditText) v.findViewById(R.id.username);
        mEmailView = (EditText) v.findViewById(R.id.email);
        mPasswordView = (EditText) v.findViewById(R.id.password);

        mLoginStatusMessaegView = (TextView) v.findViewById(R.id.login_status_message);

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        v.findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(mUsername, ARG_USERNAME);
        outState.putString(mEmail, ARG_EMAIL);
        outState.putString(mPassword, KEY_PASSWORD);
    }

    void attemptLogin() {
        //reset errors
        mEmailView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        mEmail = mEmailView.getText().toString();
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(mEmail)) {
            if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
                mEmailView.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(mPassword)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (mPassword.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUsername)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        } else if (mUsername.length() < 4) {
            mUsernameView.setError(getString(R.string.error_invalid_username));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't login and focus the error
            focusView.requestFocus();
        } else if (mAttemptLoginListener != null) {
            mAttemptLoginListener.onAttemptLogin(mUsername, mPassword, mEmail);
        }
    }

    public void setAttemptLoginListener(OnAttemptLoginListener listener) {
        mAttemptLoginListener = listener;
    }


    public void showLoginProgress(final boolean show, String message) {
        if (show) {
            mLoginStatusMessaegView.setText(message);
        }
        showProgress(show);
    }

    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            showProgressModern(show);
        } else {
            showProgressCompat(show);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgressModern(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mLoginStatusView.setVisibility(View.VISIBLE);
        mLoginStatusView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 1 : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });

        mLoginStatusView.setVisibility(View.VISIBLE);
        mLoginFormView.animate()
                .setDuration(shortAnimTime)
                .alpha(show ? 0 : 1)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });
    }

    private void showProgressCompat(boolean show) {
        mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
