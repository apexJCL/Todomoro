package me.apexjcl.todomoro.fragments.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.tiagohm.markdownview.MarkdownView;
import br.tiagohm.markdownview.css.styles.Github;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmObject;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Shows a task detail
 * <p>
 * Created by apex on 20/04/17.
 */
public class TaskDetailDialogFragment extends BottomSheetDialogFragment implements FloatingActionButton.OnClickListener,
        RealmChangeListener<Realm> {

    public static final String BUNDLE_TASK_ID = "bundle.task.id";
    private RealmObject mTask;

    @BindView(R.id.main_collapsing)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.taskDescription)
    MarkdownView mTaskDescription;
    @BindView(R.id.editFab)
    FloatingActionButton mfab;
    @BindView(R.id.main_appbar)
    AppBarLayout mAppBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.Theme_Design_Light_BottomSheetDialog_Todomoro);
        String taskId = getArguments().getString(BUNDLE_TASK_ID, "");
        mTask = TaskHandler.find(taskId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_task, container, false);
        ButterKnife.bind(this, v);
        mfab.setOnClickListener(this);
        updateView();
        Realm.getDefaultInstance().addChangeListener(this);
        return v;
    }

    private void updateView() {
        if (mTask == null || !mTask.isValid())
            return;
        Task task = (Task) mTask;
        mCollapsingToolbar.setTitle(task.title);
        mCollapsingToolbar.setBackgroundColor(task.getColor());
        // Markdown :3
        mTaskDescription.addStyleSheet(new Github());
        mTaskDescription.loadMarkdown(task.description);
    }

    @Override
    public void onClick(View view) {
//        Intent i = new Intent(getContext(), TaskActivity.class);
//        i.putExtra(TaskActivity.TASK_EXTRA, task.id); // changing later for numbers and a PK
//        startActivity(i);
    }

    @Override
    public void onChange(Realm element) {
        updateView();
    }
}

