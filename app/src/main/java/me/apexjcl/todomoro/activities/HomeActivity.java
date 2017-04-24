package me.apexjcl.todomoro.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.adapters.HomePagerAdapter;
import me.apexjcl.todomoro.realm.UserManager;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;

/**
 * Principal application activity
 * Created by apex on 22/04/17.
 */
public class HomeActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener {

    private boolean logoutAfterClose = false;

    @BindView(R.id.pager)
    ViewPager mPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mPager.setAdapter(new HomePagerAdapter(getSupportFragmentManager()));
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(this);
        TabLayout.Tab overviewTab = mTabLayout.newTab();
        overviewTab.setIcon(R.drawable.tab_overview);
        TabLayout.Tab tasksTab = mTabLayout.newTab();
        tasksTab.setIcon(R.drawable.ic_dashboard_black_24dp);
        TabLayout.Tab doneTasksTab = mTabLayout.newTab();
        doneTasksTab.setIcon(R.drawable.ic_done_all_black_24dp);
        mTabLayout.addTab(overviewTab);
        mTabLayout.addTab(tasksTab);
        mTabLayout.addTab(doneTasksTab);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_logout:
                logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        mPager.setCurrentItem(tab.getPosition(), true);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
