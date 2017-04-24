package me.apexjcl.todomoro.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import me.apexjcl.todomoro.fragments.tabs.OverviewFragment;
import me.apexjcl.todomoro.fragments.tabs.TasksDoneFragment;
import me.apexjcl.todomoro.fragments.tabs.TasksFragment;

/**
 * Aadpter for home tabs
 * <p>
 * Created by apex on 22/04/17.
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private static int ITEMS = 3;

    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new OverviewFragment();
            case 1:
                return new TasksFragment();
            case 2:
                return new TasksDoneFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return ITEMS;
    }
}
