package me.apexjcl.todomoro.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.ObjectChangeSet;
import io.realm.Realm;
import io.realm.RealmObjectChangeListener;
import me.apexjcl.todomoro.BuildConfig;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.fragments.dialogs.TaskMoreDetailsFragment;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Created by apex on 30/04/2017.
 */

public class TaskDetailActivity extends AppCompatActivity implements FloatingActionButton.OnClickListener,
        Realm.Transaction.OnSuccess, Realm.Transaction.OnError {

    public static final String TASK_ID_EXTRA = "task_id";

    private ACTION mAction = ACTION.NONE;

    private TaskMoreDetailsFragment mMoreDialog;
    private SimpleDateFormat mFormatter;
    private Realm realm;
    private Task mTask;
    private Menu mMenu;

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
                if (!task.isLoaded())
                    return;
                if (!task.isValid()) {
                    finish();
                    return;
                }
                if (mTask == null)
                    return;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition();
                }
                init();
            }
        });
        ButterKnife.bind(this);
        mFormatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        mFab.setOnClickListener(this);
        setSupportActionBar(mToolbar);
        mMarkdownView.addStyleSheet(new Github());
    }

    private void init() {
        mTaskTitle.setText(mTask.getTitle());
        mToolbar.setBackgroundColor(mTask.getColor());
        mMarkdownView.loadMarkdown(mTask.getDescription());
        mMoreDialog = TaskMoreDetailsFragment.newInstance(
                mFormatter.format(mTask.getCreatedAt()),
                mFormatter.format(mTask.getUpdatedAt())
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mTask.getColor());
            getWindow().setNavigationBarColor(mTask.getColor());
        }
        if (mTask.isFinished()) {
            mMenu.getItem(0).setVisible(false);
            mFab.hide();
        } else
            mFab.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_task_detail, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                markAsDone();
                break;
            case R.id.action_delete:
                deleteTask();
                break;
            case R.id.action_more:
                showMoreDialog();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
        mTask = null;
    }

    @Override
    public void onClick(View v) {
        Intent i = new Intent(getApplicationContext(), PomodoroActivity.class);
        i.putExtra(PomodoroActivity.TASK_ID, mTask.getId());
        startActivity(i);
    }

    private void markAsDone() {
        mAction = ACTION.UPDATE;
        final String task_id = mTask.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                Task t = bgRealm.where(Task.class).equalTo(Task.PK, task_id).findFirst();
                t.setFinished(true);
            }
        }, this, this);
    }

    private void showMoreDialog() {
        mMoreDialog.show(getFragmentManager(), null);
    }

    private void deleteTask() {
        mAction = ACTION.DELETE;
        final String task_id = mTask.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm bgRealm) {
                bgRealm.where(Task.class).equalTo(Task.PK, task_id).findFirst().deleteFromRealm();
            }
        }, this, this);
    }

    @Override
    public void onError(Throwable error) {
        if (BuildConfig.debug)
            error.printStackTrace();
        Toast.makeText(getApplicationContext(), R.string.transaction_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        switch (mAction) {
            case UPDATE:
                Toast.makeText(getApplicationContext(), R.string.updated_correctly, Toast.LENGTH_SHORT).show();
                break;
            case DELETE:
                Toast.makeText(getApplicationContext(), R.string.deleted_successfully, Toast.LENGTH_LONG).show();
                finish();
                break;
            default:
            case NONE:
                break;
        }
    }

    private enum ACTION {
        UPDATE, DELETE, NONE
    }
}
