package me.apexjcl.todomoro.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmObjectChangeListener;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Created by apex on 30/04/2017.
 */

public class TaskDetailActivity extends AppCompatActivity implements FloatingActionButton.OnClickListener {

    public static final String TASK_ID_EXTRA = "task_id";

    private Realm realm;
    private Task mTask;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.task_title)
    TextView mTaskTitle;
    @BindView(R.id.markdown_view)
    MarkdownView mMarkdownView;
    @BindView(R.id.fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) postponeEnterTransition();
        String taskID = getIntent().getStringExtra(TASK_ID_EXTRA);
        if (taskID == null) {
            finish();
            return;
        }
        realm = Realm.getDefaultInstance();
        mTask = TaskHandler.getTask(taskID, realm);
        mTask.addChangeListener(new RealmObjectChangeListener<Task>() {
            @Override
            public void onChange(Task task, ObjectChangeSet changeSet) {
                if (!task.isLoaded() || !task.isValid() || mTask == null)
                    return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition();
                }
                init();
            }
        });
        ButterKnife.bind(this);
        mFab.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        mMarkdownView.addStyleSheet(new Github());
    }

    private void init() {
        mTaskTitle.setText(mTask.getTitle());
        mToolbar.setBackgroundColor(mTask.getColor());
        mMarkdownView.loadMarkdown(mTask.getDescription());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mTask.getColor());
            getWindow().setNavigationBarColor(mTask.getColor());
        }
        mFab.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        mTask = null;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), PomodoroActivity.class);
        i.putExtra(PomodoroActivity.TASK_ID, mTask.getId());
        startActivity(i);
    }
}
