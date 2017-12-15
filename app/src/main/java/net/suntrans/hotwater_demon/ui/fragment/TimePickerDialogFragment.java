package net.suntrans.hotwater_demon.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.TimePicker;

/**
 * Created by Lp on 2017/8/23.
 */

public class TimePickerDialogFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getContext(), this, 12, 45, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (onTimeSelectedListener!=null)
            onTimeSelectedListener.onTimeSelected(hourOfDay,minute);
    }

    private onTimeSelectedListener onTimeSelectedListener;

    public TimePickerDialogFragment.onTimeSelectedListener getOnTimeSelectedListener() {
        return onTimeSelectedListener;
    }

    public void setOnTimeSelectedListener(TimePickerDialogFragment.onTimeSelectedListener onTimeSelectedListener) {
        this.onTimeSelectedListener = onTimeSelectedListener;
    }

    public interface onTimeSelectedListener{
        void onTimeSelected(int hourOfDay,int minute);
    }
}
