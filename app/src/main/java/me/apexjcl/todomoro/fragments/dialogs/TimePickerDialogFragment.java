package me.apexjcl.todomoro.fragments.dialogs;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

/**
 * Shows a time picker dialog fragment
 * <p>
 * Created by apex on 20/04/17.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private int hour;
    private int minutes;
    private Calendar c;
    private TimePickerDialog.OnTimeSetListener listener;

    public TimePickerDialogFragment() {
        super();
        c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minutes = c.get(Calendar.MINUTE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), listener, hour, minutes, false);
    }


    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    public int getHour() {
        return hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setTime(int hour, int minutes) {
        this.hour = hour;
        this.minutes = minutes;
    }

    public Date getTime() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    public void setTime(Date due) {
        Calendar c = Calendar.getInstance();
        c.setTime(due);
        this.hour = c.get(Calendar.HOUR_OF_DAY);
        this.minutes = c.get(Calendar.MINUTE);
    }
}
