package me.apexjcl.todomoro.fragments.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Shows a date picker dialog
 * Created by apex on 20/04/17.
 */
public class DatePickerDialogFragment extends DialogFragment {

    private Calendar c;
    private int year;
    private int month;
    private int day;
    private DatePickerDialog.OnDateSetListener listener;

    public DatePickerDialogFragment() {
        super();
        c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), listener, year, month, day);
    }

    public Date getDate() {
        c.set(year, month, day);
        Date date = new Date();
        date.setTime(c.getTimeInMillis());
        return date;
    }

    public void setDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public void setDate(Date due) {
        Calendar c = Calendar.getInstance();
        c.setTime(due);
        this.year = c.get(Calendar.YEAR);
        this.month = c.get(Calendar.MONTH);
        this.day = c.get(Calendar.DAY_OF_MONTH);
    }
}
