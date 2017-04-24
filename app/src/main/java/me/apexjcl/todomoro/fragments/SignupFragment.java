package me.apexjcl.todomoro.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.ObjectServerError;
import io.realm.SyncCredentials;
import io.realm.SyncUser;
import me.apexjcl.todomoro.BuildConfig;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.TodomoroApplication;
import me.apexjcl.todomoro.activities.MainActivity;
import me.apexjcl.todomoro.realm.UserManager;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

/**
 * Handles signup stuff
 * Created by apex on 22/04/17.
 */
public class SignupFragment extends Fragment implements SyncUser.Callback {

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.layout_username)
    TextInputLayout mUsernameLayout;
    @BindView(R.id.layout_password)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.layout_password_verification)
    TextInputLayout mPasswordVerificationLayout;
    @BindView(R.id.layout_form)
    LinearLayout mFormLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signup, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.button_signup)
    void signup() {
        if (!checkFields())
            return;
        toggleProgressBar();
        SyncUser.loginAsync(
                SyncCredentials.usernamePassword(
                        getUsername(),
                        getPassword(),
                        true
                ),
                TodomoroApplication.ROS_AUTH_URL,
                this
        );
    }

    private String getUsername() {
        return mUsernameLayout.getEditText().getText().toString();
    }

    private String getPassword() {
        return mPasswordLayout.getEditText().getText().toString();
    }

    @OnClick(R.id.button_login)
    void login() {
        ((MainActivity) getActivity()).loadLoginFragment();
    }

    private boolean checkFields() {
        return validateUsername() && validatePassword();
    }

    private boolean validateUsername() {
        String username = mUsernameLayout.getEditText().getText().toString();
        if (username.length() == 0) {
            mUsernameLayout.setError(getString(R.string.error_empty_username));
            return false;
        }
        if (username.matches("\\s+")) {
            mUsernameLayout.setError(getString(R.string.error_blank_username));
            return false;
        }
        return true;
    }

    private boolean validatePassword() {
        String password = mPasswordLayout.getEditText().getText().toString();
        String passwordVer = mPasswordVerificationLayout.getEditText().getText().toString();
        if (password.length() == 0) {
            mPasswordLayout.setError(getString(R.string.error_empty_password));
            return false;
        }
        if (password.matches("\\s+")) {
            mPasswordLayout.setError(getString(R.string.error_blank_password));
            return false;
        }
        if (!password.matches(passwordVer)) {
            mPasswordVerificationLayout.setError(getString(R.string.error_passwords_mismatch));
            return false;
        }
        return true;
    }

    private void toggleProgressBar() {
        mProgressBar.setVisibility(
                mProgressBar.getVisibility() == GONE ? VISIBLE : GONE
        );
        mFormLayout.setVisibility(
                mFormLayout.getVisibility() == INVISIBLE ? VISIBLE : INVISIBLE
        );
    }

    @Override
    public void onSuccess(SyncUser user) {
        UserManager.setActiveUser(user);
        ((MainActivity) getActivity()).launchApp();
    }

    @Override
    public void onError(ObjectServerError error) {
        if (BuildConfig.DEBUG)
            error.printStackTrace();
        toggleProgressBar();
        Toast.makeText(getContext(), R.string.error_signup, Toast.LENGTH_SHORT).show();
    }
}
