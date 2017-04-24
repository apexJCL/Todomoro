package me.apexjcl.todomoro.fragments.tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.adapters.TasksRecyclerAdapter;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Fragment that shows tasks that are marked as done
 *
 * Created by apex on 22/04/17.
 */
public class TasksDoneFragment extends Fragment {

    @BindView(R.id.tasksRecyclerView)
    RecyclerView mTaskRecyclerView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

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
        OrderedRealmCollection<Task> collection = TaskHandler.getFinishedTasks(realm);
        TasksRecyclerAdapter adapter = new TasksRecyclerAdapter(getContext(), collection, true, getFragmentManager(), true);
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
}
