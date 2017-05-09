package me.apexjcl.todomoro.fragments.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import io.realm.Realm;
import me.apexjcl.todomoro.BuildConfig;

/**
 * Created by apex on 06/05/2017.
 */

public class BaseFragment extends Fragment {

    protected Realm realm;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        if (BuildConfig.DEBUG)
            Log.d("Todomoro", "Fragment onCreate called");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Todomoro", "Fragment onDestroyView called");
    }

    @Override
    public void onDestroy() {
        realm.close();
        realm = null;
        if (BuildConfig.DEBUG)
            Log.d("Todomoro", "Fragment onDestroy called");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Todomoro", "Fragment onDetach called");
    }
}
