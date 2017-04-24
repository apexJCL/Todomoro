package me.apexjcl.todomoro.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.joaquimley.faboptions.FabOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.apexjcl.todomoro.R;

/**
 * Created by apex on 22/04/17.
 */
public class OverviewFragment extends Fragment {

    @BindView(R.id.fab_options)
    FabOptions mFab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, v);
        mFab.setButtonsMenu(R.menu.fab_overview );
        return v;
    }
}
