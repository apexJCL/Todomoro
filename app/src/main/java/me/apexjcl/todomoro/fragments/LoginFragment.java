package me.apexjcl.todomoro.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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
 * Login fragment class duhh
 * <p>
 * Created by apex on 22/04/17.
 */
public class LoginFragment extends Fragment implements SyncUser.Callback {

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.layout_form)
    LinearLayout mFormLayout;
    @BindView(R.id.layout_password)
    TextInputLayout mPasswordLayout;
    @BindView(R.id.layout_username)
    TextInputLayout mUsernameLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @OnClick(R.id.button_login)
    void login() {
        if (!checkFields())
            return;
        toggleProgressBar();
        SyncUser.loginAsync(
                SyncCredentials.usernamePassword(
                        getUsername(),
                        getPassword(),
                        false
                ),
                TodomoroApplication.ROS_AUTH_URL,
                this
        );
    }

    private String getUsername() {
        String u = mUsernameLayout.getEditText().getText().toString();
        return u == null ? "" : u;
    }

    private String getPassword() {
        String p = mPasswordLayout.getEditText().getText().toString();
        return p == null ? "" : p;
    }

    @OnClick(R.id.button_signup)
    void showSignupFragment() {
        ((MainActivity) getActivity()).loadSigupFragment();
    }

    private boolean checkFields() {
        return validateUsername() && validatePassword();
    }

    private boolean validateUsername() {
        String username = getUsername();
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
        String password = getPassword();
        if (password.length() == 0) {
            mPasswordLayout.setError(getString(R.string.error_empty_password));
            return false;
        }
        if (password.matches("\\s+")) {
            mPasswordLayout.setError(getString(R.string.error_blank_password));
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
        // Save username
        SharedPreferences prefs = getContext().getSharedPreferences(TodomoroApplication.SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(TodomoroApplication.PREFS_USERNAME, getUsername());
        edit.apply();
        ((MainActivity) getActivity()).launchApp();
    }

    @Override
    public void onError(ObjectServerError error) {
        if (BuildConfig.debug)
            error.printStackTrace();
        toggleProgressBar();
        Toast.makeText(getContext(), R.string.error_login, Toast.LENGTH_LONG).show();
    }
}
