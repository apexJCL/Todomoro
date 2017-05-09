package me.apexjcl.todomoro.fragments.stats;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;
import me.apexjcl.todomoro.realm.models.Task;

/**
 * Show time stats, like:
 * + Average time between a task is registered and due to
 * + Average time (gathered from pomodoro cycles) to complete a task
 * + Min time
 * + Max time
 * +
 * Created by apex on 06/05/2017.
 */

public class TimeFragment extends BaseFragment implements RealmChangeListener, IAxisValueFormatter,
        OnChartValueSelectedListener {

    private CombinedChart mCombinedChart;
    private RealmResults<Task> finishedTasks;

    private CombinedData mCombinedData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats_time, container, false);
        finishedTasks = TaskHandler.getFinishedTasks(realm);
        finishedTasks.addChangeListener(this);
        mCombinedChart = (CombinedChart) v.findViewById(R.id.combined_chart_average_times);
        mCombinedChart.setOnChartValueSelectedListener(this);
        mCombinedChart.getXAxis().setValueFormatter(this);
        mCombinedData = new CombinedData();
        return v;
    }

    @Override
    public void onDestroyView() {
        realm.removeAllChangeListeners();
        Log.d("Time", "Realm listener removed");
        super.onDestroyView();
    }

    /**
     * Updates average time datasets
     * This dataset will calculate for each task
     * + Time since the task was created until it was marked as dueTo
     */
    private void updateAvgTimeData() {
        if (finishedTasks.isEmpty())
            return;
        List<BarEntry> daySpanEntries = new ArrayList<>(finishedTasks.size());
        List<Entry> finishedDayEntries = new ArrayList<>(finishedTasks.size());
        for (int i = 0; i < finishedTasks.size(); i++) {
            Task t = finishedTasks.get(i);
            Calendar createdAt = Calendar.getInstance();
            createdAt.setTime(t.getCreatedAt());
            Calendar finishedAt = Calendar.getInstance();
            finishedAt.setTime(t.getFinishedAt());
            Calendar dueAt = Calendar.getInstance();
            dueAt.setTime(t.getDue());

            int daySpan = finishedAt.get(Calendar.DAY_OF_YEAR) - createdAt.get(Calendar.DAY_OF_YEAR);
            int finishedHeight = dueAt.get(Calendar.DAY_OF_YEAR) - createdAt.get(Calendar.DAY_OF_YEAR);
            daySpanEntries.add(new BarEntry(i + 1, daySpan, t.getTitle()));
            finishedDayEntries.add(new Entry(i + 1, finishedHeight, t.getTitle()));
        }
        BarDataSet mDaySpanSet = new BarDataSet(daySpanEntries, getString(R.string.label_day_span));
        mDaySpanSet.setColor(getResources().getColor(R.color.light_blue));
        LineDataSet mFinishedSet = new LineDataSet(finishedDayEntries, getString(R.string.label_day_finished));
        mFinishedSet.setColor(getResources().getColor(R.color.yellow));
        mCombinedData.setData(new BarData(mDaySpanSet));
        mCombinedData.setData(new LineData(mFinishedSet));
    }

    @Override
    public void onChange(Object element) {
        updateAvgTimeData();
        mCombinedChart.setData(mCombinedData);
        mCombinedData.notifyDataChanged();
        mCombinedChart.invalidate();
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return String.valueOf((int) value);
    }

    protected RectF mOnValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Toast.makeText(getContext(), e.getData().toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected() {

    }
}
