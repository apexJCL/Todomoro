package me.apexjcl.todomoro.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.realm.models.PomodoroStatus;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * This activity will display a series of specific info about a single task
 */
public class ReportActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView mRecyclerView;
    private ReportAdapter mAdapter;
    private String mTaskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        String task_id = getIntent().getExtras().getString("task_id", null);
        if (task_id == null) {
            finish();
            return;
        }
        mTaskId = task_id;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        mAdapter = new ReportAdapter(realm.where(Task.class).equalTo(Task.PK, mTaskId).findFirst().getPomodoroStatusList(), true);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onPause() {
        realm.close();
        realm = null;
        super.onPause();
    }

    private class ReportAdapter extends RealmRecyclerViewAdapter<PomodoroStatus, ReportAdapter.ViewHolder> {

        public ReportAdapter(@Nullable OrderedRealmCollection<PomodoroStatus> data, boolean autoUpdate) {
            super(data, autoUpdate);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pomodoro_status, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.update(position);
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView mPomodoroId;
            private TextView mPomodoroStatus;
            private TextView mPomodoroCycle;
            private TextView mPomodoroRemaining;
            private TextView mPomodoroTime;

            public ViewHolder(View itemView) {
                super(itemView);
                mPomodoroId = (TextView) itemView.findViewById(R.id.pomodoro_id);
                mPomodoroStatus = (TextView) itemView.findViewById(R.id.pomodoro_status);
                mPomodoroCycle = (TextView) itemView.findViewById(R.id.pomodoro_cycle);
                mPomodoroRemaining = (TextView) itemView.findViewById(R.id.pomodoro_remaining);
                mPomodoroTime = (TextView) itemView.findViewById(R.id.pomodoro_time);
            }

            public void update(int position) {
                PomodoroStatus status = getItem(position);
                mPomodoroId.setText(String.valueOf(position));
                try {
                    mPomodoroStatus.setText(status.getStatus());
                } catch (Exception e) {
                    mPomodoroStatus.setText(getString(R.string.no_data));
                }
                mPomodoroCycle.setText(String.valueOf(status.getCycle()));
                mPomodoroRemaining.setText(String.valueOf(status.getRemaining()));
                mPomodoroTime.setText(String.valueOf(status.getTime().getTime()));
            }
        }

    }
}
