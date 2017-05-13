package me.apexjcl.todomoro.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import io.realm.Realm;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.adapters.TasksDoneAdapter;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;

/**
 * Created by apex on 02/05/2017.
 */

public class TasksDoneActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView mRecyclerView;
    private TasksDoneAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_done);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        realm = Realm.getDefaultInstance();
        mAdapter = new TasksDoneAdapter(TaskHandler.getFinishedTasks(realm));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }
}
