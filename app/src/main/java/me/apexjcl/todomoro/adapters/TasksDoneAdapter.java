package me.apexjcl.todomoro.adapters;

import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Created by apex on 02/05/2017.
 */

public class TasksDoneAdapter extends RealmRecyclerViewAdapter<Task, TasksDoneAdapter.ViewHolder> {

    public TasksDoneAdapter(@Nullable OrderedRealmCollection<Task> data) {
        super(data, true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.update(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView mMenu;
        private TextView mTitle;
        private ImageView mColorDot;

        public ViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mColorDot = (ImageView) itemView.findViewById(R.id.colorDot);
            mMenu = (ImageView) itemView.findViewById(R.id.menuButton);
            mMenu.setVisibility(View.GONE);
        }

        public void update(Task task) {
            mTitle.setText(task.getTitle());
            mColorDot.getDrawable().mutate().setColorFilter(task.getColor(), PorterDuff.Mode.SRC_IN);
        }
    }

}
