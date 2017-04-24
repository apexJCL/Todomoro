package me.apexjcl.todomoro.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.rubensousa.floatingtoolbar.FloatingToolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.adapters.TasksRecyclerAdapter;
import me.apexjcl.todomoro.fragments.dialogs.CreateTaskDialogFragment;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Handles showing all (not finished) tasks
 * Created by apex on 22/04/17.
 */
public class TasksFragment extends Fragment implements FloatingToolbar.ItemClickListener {

    @BindView(R.id.tasksRecyclerView)
    RecyclerView mTaskRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.floatingToolbar)
    FloatingToolbar mToolbar;

    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.bind(this, v);
        onCreateViewInit();
        return v;
    }

    private void onCreateViewInit() {
        realm = Realm.getDefaultInstance();
        mToolbar.attachFab(mFab);
        mToolbar.setClickListener(this);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OrderedRealmCollection<Task> tasks = TaskHandler.getUnfinishedTasks(realm);
        TasksRecyclerAdapter adapter = new TasksRecyclerAdapter(getContext(), tasks, true, getFragmentManager(), false);
        mTaskRecyclerView.setAdapter(adapter);
        mTaskRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0)
                    mFab.hide();
                else mFab.show();
            }
        });
    }


    @Override
    public void onDestroy() {
        realm.close();
        realm = null;
        super.onDestroy();
    }

    @Override
    public void onItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                CreateTaskDialogFragment dialogFragment = new CreateTaskDialogFragment();
                dialogFragment.show(getFragmentManager(), "create_task");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemLongClick(MenuItem item) {

    }
}
