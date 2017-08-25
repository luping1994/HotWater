package net.suntrans.hotwater.ui.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.suntrans.hotwater.R;
import net.suntrans.hotwater.databinding.FragmentTimesettingBinding;
import net.suntrans.looney.widgets.LoadingDialog;

import static android.R.attr.data;
import static android.R.attr.fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimesettingFragment extends Fragment implements TimePickerDialogFragment.onTimeSelectedListener, View.OnClickListener {

    private LoadingDialog dialog;
    private FragmentTimesettingBinding binding;

    public TimesettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timesetting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.taiyangnengStart.setOnClickListener(this);
        binding.taiyangnengStop.setOnClickListener(this);

        binding.xiyugaofengStart.setOnClickListener(this);
        binding.xiyugaofengStop.setOnClickListener(this);

        binding.jizuStart1.setOnClickListener(this);
        binding.jizuStart2.setOnClickListener(this);
        binding.jizuStart3.setOnClickListener(this);
        binding.jizuStop1.setOnClickListener(this);
        binding.jizuStop2.setOnClickListener(this);
        binding.jizuStop3.setOnClickListener(this);
        binding.shitangStart1.setOnClickListener(this);
        binding.shitangStart2.setOnClickListener(this);
        binding.shitangStart3.setOnClickListener(this);
        binding.shitangStop1.setOnClickListener(this);
        binding.shitangStop2.setOnClickListener(this);
        binding.shitangStop3.setOnClickListener(this);

    }

    int currentId;

    @Override
    public void onClick(View v) {
        currentId = v.getId();
        System.out.println("我被点击了");
        TimePickerDialogFragment fragment = (TimePickerDialogFragment) getChildFragmentManager().findFragmentByTag("timeDialog");
        if (fragment == null) {
            fragment = new TimePickerDialogFragment();
            fragment.setOnTimeSelectedListener(this);
        }
        fragment.show(getChildFragmentManager(), "timeDialog");
    }

    @Override
    public void onTimeSelected(int hourOfDay, int minute) {
        if (dialog == null) {
            dialog = new LoadingDialog(getContext());
            dialog.setCancelable(false);
        }
        System.out.println(hourOfDay + ":" + minute);
        String time = hourOfDay + ":" + minute;
        switch (currentId) {
            case R.id.taiyangnengStart:
                dialog.setWaitText("正在设置太阳能起始时间...");
                binding.taiyangnengStart.setText(time);
                break;
            case R.id.taiyangnengStop:
                dialog.setWaitText("正在设置太阳能结束时间...");
                binding.taiyangnengStop.setText(time);
                break;
            case R.id.xiyugaofengStart:
                dialog.setWaitText("正在设置洗浴高峰起始时间...");

                binding.xiyugaofengStart.setText(time);

                break;
            case R.id.xiyugaofengStop:
                dialog.setWaitText("正在设置洗浴高峰结束时间...");
                binding.xiyugaofengStop.setText(time);
                break;
            case R.id.jizuStart1:
                dialog.setWaitText("正在设置机组1工作开始时间...");
                binding.jizuStart1.setText(time);
                break;
            case R.id.jizuStop1:
                dialog.setWaitText("正在设置机组1工作结束时间...");
                binding.jizuStart1.setText(time);
                break;
            case R.id.jizuStart2:
                dialog.setWaitText("正在设置机组2工作开始时间...");
                binding.jizuStart1.setText(time);
                break;
            case R.id.jizuStop2:
                dialog.setWaitText("正在设置机组2工作结束时间...");
                binding.jizuStart1.setText(time);
                break;
            case R.id.jizuStart3:
                dialog.setWaitText("正在设置机组3工作开始时间...");
                binding.jizuStart1.setText(time);
                break;
            case R.id.jizuStop3:
                dialog.setWaitText("正在设置机组3工作结束时间...");
                binding.jizuStart1.setText(time);
                break;
            case R.id.shitangStart1:
                dialog.setWaitText("正在设置食堂1开始时间...");
                binding.shitangStart1.setText(time);
                break;
            case R.id.shitangStop1:
                dialog.setWaitText("正在设置食堂1结束时间...");
                binding.shitangStop1.setText(time);
                break;
            case R.id.shitangStart2:
                dialog.setWaitText("正在设置食堂2开始时间...");
                binding.shitangStart2.setText(time);
                break;
            case R.id.shitangStop2:
                dialog.setWaitText("正在设置食堂2结束时间...");
                binding.shitangStop2.setText(time);
                break;
            case R.id.shitangStart3:
                dialog.setWaitText("正在设置食堂3开始时间...");
                binding.shitangStart3.setText(time);
                break;
            case R.id.shitangStop3:
                dialog.setWaitText("正在设置食堂3结束时间...");
                binding.shitangStop3.setText(time);
                break;
        }
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000);

    }

    Handler handler = new Handler();

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}