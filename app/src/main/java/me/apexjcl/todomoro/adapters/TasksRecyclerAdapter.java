package me.apexjcl.todomoro.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmRecyclerViewAdapter;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.fragments.dialogs.TaskDetailDialogFragment;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * RecyclerAdapter for tasks
 * Created by apex on 20/04/17.
 */
public class TasksRecyclerAdapter extends RealmRecyclerViewAdapter<Task, TasksRecyclerAdapter.ViewHolder>
        implements RealmChangeListener<Realm> {

    private FragmentManager fragmentManager;
    private boolean done = false;

    /**
     * Create s a new TaskRecyclerAdapter
     *
     * @param context
     * @param data
     * @param autoUpdate
     * @param fragmentManager
     * @param done            If enabled, it makes the recycler behaves on done tasks
     */
    public TasksRecyclerAdapter(@NonNull Context context,
                                @Nullable OrderedRealmCollection<Task> data,
                                boolean autoUpdate, FragmentManager fragmentManager, boolean done) {
        super(context, data, autoUpdate);
        this.fragmentManager = fragmentManager;
        this.done = done;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Task task = getItem(position);
        if (task == null || !task.isValid())
            return;
        holder.updateView(task);
    }

    @Override
    public void onChange(Realm element) {
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            PopupMenu.OnMenuItemClickListener, View.OnLongClickListener {

        @BindView(R.id.title)
        TextView title;
        @BindView(R.id.menuButton)
        ImageView mMenu;
        @BindView(R.id.colorDot)
        ImageView mColorDot;

        private String mTaskId;
        private PopupMenu mPopup;

        private final int DELETE_ITEM = 4;
        private final int MARK_DONE_ITEM = 2;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new ViewClickListener());
            itemView.setOnLongClickListener(this);
            mPopup = new PopupMenu(context, mMenu);
            mPopup.getMenuInflater().inflate(R.menu.menu_task_item, mPopup.getMenu());
            mMenu.setOnClickListener(this);
            mPopup.setOnMenuItemClickListener(this);
            //
            if (done)
                mPopup.getMenu().getItem(MARK_DONE_ITEM).setVisible(false);
        }

        private void updateView(Task task) {
            this.mTaskId = task.getId();
            this.title.setText(task.getTitle());
            mColorDot.getDrawable().setColorFilter(task.getColor(), PorterDuff.Mode.SRC_ATOP);
        }

        @Override
        public void onClick(View v) {
            mPopup.getMenu().getItem(DELETE_ITEM).setVisible(false);
            mPopup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_details:
                    showDetails();
                    return true;
                case R.id.action_done:
                    TaskHandler.markFinished(mTaskId);
                    return true;
                case R.id.action_pomodoro:
                    return true;
                case R.id.action_delete:
                    TaskHandler.delete(mTaskId);
                    return true;
                default:
                    return true;
            }
        }

        void showDetails() {
            TaskDetailDialogFragment dialogFragment = new TaskDetailDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(TaskDetailDialogFragment.BUNDLE_TASK_ID, mTaskId);
            dialogFragment.setArguments(bundle);
            dialogFragment.show(fragmentManager, "task");
        }

        @Override
        public boolean onLongClick(View v) {
            mPopup.getMenu().getItem(DELETE_ITEM).setVisible(true);
            mPopup.show();
            return true;
        }

        private class ViewClickListener implements View.OnClickListener {

            @Override
            public void onClick(View v) {
                showDetails();
            }
        }
    }
}
