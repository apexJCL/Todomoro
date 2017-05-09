package me.apexjcl.todomoro.fragments.stats;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.Arrays;

import io.realm.RealmChangeListener;
import me.apexjcl.todomoro.R;
import me.apexjcl.todomoro.realm.handlers.TaskHandler;

/**
 * Created by apex on 06/05/2017.
 */

public class GeneralFragment extends BaseFragment implements RealmChangeListener, IValueFormatter {

    private PieData mData;
    private PieChart mPie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_stats_general, container, false);
        mPie = (PieChart) v.findViewById(R.id.pie_chart);
        mPie.setData(mData);
        mPie.setNoDataText(getString(R.string.no_data_text));
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm.addChangeListener(this);
        createPieData();
    }

    @Override
    public void onDestroy() {
        realm.removeChangeListener(this);
        super.onDestroy();
    }

    private void createPieData() {
        long unfinished = TaskHandler.countUnfinished(realm);
        long finished = TaskHandler.countFinished(realm);
        long inProgress = TaskHandler.countInProgress(realm);

        PieEntry[] entries = new PieEntry[3];
        entries[0] = new PieEntry(unfinished, getString(R.string.label_unfinished));
        entries[1] = new PieEntry(finished, getString(R.string.label_finished));
        entries[2] = new PieEntry(inProgress, getString(R.string.label_in_progress));
        PieDataSet set = new PieDataSet(Arrays.asList(entries), getString(R.string.chart_general));
        set.setColors(new int[]{
                R.color.red,
                R.color.light_blue,
                R.color.orange
        }, getContext());
        mData = new PieData(set);
        mData.setValueFormatter(this);
    }

    @Override
    public void onChange(Object element) {
        createPieData();
        mPie.setData(mData);
        mPie.notifyDataSetChanged();
        mPie.invalidate();
    }

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return String.valueOf((int) value);
    }
}
