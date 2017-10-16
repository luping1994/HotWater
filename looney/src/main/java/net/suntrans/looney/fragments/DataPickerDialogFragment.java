package net.suntrans.looney.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import net.suntrans.looney.widgets.CompatDatePickerDialog;

import java.util.Calendar;

/**
 * Created by Looney on 2017/9/5.
 */

public class DataPickerDialogFragment extends DialogFragment {
    private int mYear;
    private int mMonth;
    private int mDay;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CompatDatePickerDialog datePickerDialog = new CompatDatePickerDialog(getContext(),mDateSetListener,mYear, mMonth-1, mDay);
        datePickerDialog.setTitle("选择时间");
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePicker.setMaxDate(System.currentTimeMillis());
        return datePickerDialog;
    }

    private CompatDatePickerDialog.OnDateSetListener mDateSetListener =
            new CompatDatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    mYear = year;
                    mMonth = monthOfYear + 1;
                    mDay = dayOfMonth;

                    if (listener != null) {
                        listener.onDateSet(mYear, mMonth, mDay, new StringBuilder()
                                .append(mYear).append("-")
                                .append(pad(mMonth)).append("-")
                                .append(pad(mDay)).toString());
                    }


                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }


    private OnDateSetChangerListener listener;

    public OnDateSetChangerListener getListener() {
        return listener;
    }

    public void setListener(OnDateSetChangerListener listener) {
        this.listener = listener;
    }

    public interface OnDateSetChangerListener {
        void onDateSet(int year, int month, int day, String format);
    }

}
