package me.apexjcl.todomoro.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import io.realm.SyncUser;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.fragments.LoginFragment;
import me.apexjcl.todomoro.fragments.SignupFragment;
import me.apexjcl.todomoro.realm.UserManager;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_IGNORE_USER = "action.ignoreCurrentUser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check if a session already exists
        if (savedInstanceState == null){
            if (!ACTION_IGNORE_USER.equals(getIntent().getAction()))
                if (UserManager.isSessionAvailable()) {
                    UserManager.setActiveUser(SyncUser.currentUser());
                    launchApp();
                    return;
                }
        }
        // Otherwise, continue
        ButterKnife.bind(this);
        loadLoginFragment();
    }

    public void launchApp() {
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }


    public void loadLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentHolder, new LoginFragment());
        ft.commit();
    }

    public void loadSigupFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentHolder, new SignupFragment());
        ft.commit();
    }
}
