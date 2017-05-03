package me.apexjcl.todomoro.fragments.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.apexjcl.todomoro.R;

/**
 * Created by apex on 02/05/2017.
 */

public class TaskMoreDetailsFragment extends DialogFragment {

    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";

    @BindView(R.id.created_at)
    TextView mCreatedAt;
    @BindView(R.id.updated_at)
    TextView mUpdatedAt;

    public static TaskMoreDetailsFragment newInstance(String createdAt, String updatedAt) {
        TaskMoreDetailsFragment fragment = new TaskMoreDetailsFragment();
        Bundle args = new Bundle();
        args.putString(CREATED_AT, createdAt);
        args.putString(UPDATED_AT, updatedAt);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_task_more_details, container, false);
        ButterKnife.bind(this, v);
        mCreatedAt.setText(getArguments().getString(CREATED_AT, getString(R.string.no_data)));
        mUpdatedAt.setText(getArguments().getString(UPDATED_AT, getString(R.string.no_data)));
        return v;
    }

}
