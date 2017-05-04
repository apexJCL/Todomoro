package me.apexjcl.todomoro.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.SyncUser;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.TodomoroApplication;
import me.apexjcl.todomoro.fragments.CreateTaskFragment;
import me.apexjcl.todomoro.fragments.EditTaskFragment;
import me.apexjcl.todomoro.fragments.tabs.TasksFragment;
import me.apexjcl.todomoro.realm.UserManager;
import me.apexjcl.todomoro.services.PomodoroService;

/**
 * Principal application activity
 * Created by apex on 22/04/17.
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ServiceConnection {

    private boolean logoutAfterClose = false;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawer;
    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    TextView mUsername;

    private String username;
    private Intent mServiceIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkForRunningPomodoro();
        // Check if a session already exists
        if (savedInstanceState == null) {
            if (UserManager.isSessionAvailable()) {
                UserManager.setActiveUser(SyncUser.currentUser());
            } else {
                logout();
            }
        }
        ButterKnife.bind(this);
        init();
    }

    private void checkForRunningPomodoro() {
        mServiceIntent = new Intent(this, PomodoroService.class);
        bindService(mServiceIntent, this, Context.BIND_AUTO_CREATE);
    }

    private void init() {
        mUsername = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.drawerUsername);
        username = getApplicationContext().getSharedPreferences(TodomoroApplication.SHARED_PREFS, MODE_PRIVATE).getString(TodomoroApplication.PREFS_USERNAME, "");
        mUsername.setText(username);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        mNavigationView.setNavigationItemSelectedListener(this);

        mDrawer.setDrawerListener(toggle);
        toggle.syncState();
        loadFragment();
    }

    private void loadFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentHolder, new TasksFragment());
        ft.commit();
    }


    private void logout() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        i.setAction(MainActivity.ACTION_IGNORE_USER);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        logoutAfterClose = true;
    }

    @Override
    protected void onStop() {
        if (logoutAfterClose) {
            UserManager.logoutCurrentUser();
            logoutAfterClose = false;
        }
        super.onStop();
    }


    public void showCreateTaskFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentHolder, new CreateTaskFragment());
        ft.addToBackStack(CreateTaskFragment.TAG);
        ft.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_done:
                launchDoneTask();
                break;
            case R.id.action_logout:
                logout();
                break;
            default:
                break;
        }
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void launchDoneTask() {
        Intent i = new Intent(getApplicationContext(), TasksDoneActivity.class);
        startActivity(i);
    }

    public void showTaskDetail(String id) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new EditTaskFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EditTaskFragment.TASK_ID, id);
        fragment.setArguments(bundle);
        ft.replace(R.id.fragmentHolder, fragment);
        ft.addToBackStack(EditTaskFragment.TAG);
        ft.commit();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        PomodoroService mService = ((PomodoroService.PomodoroBinder) service).getService();
        if (mService.isRunning()) {
            Intent i = new Intent(this, PomodoroActivity.class);
            i.putExtra(PomodoroActivity.TASK_EXTRA, mService.getTaskId());
            startActivity(i);
        } else unbindService(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
}
