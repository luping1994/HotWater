package net.suntrans.hotwater.ui.fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;

import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.R;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Read3;
import net.suntrans.hotwater.bean.Read4;
import net.suntrans.hotwater.databinding.FragmentTimesettingBinding;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.utils.Utils;
import net.suntrans.looney.widgets.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.R.attr.data;
import static android.R.attr.fragment;
import static android.R.attr.value;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimesettingFragment extends LazyLoadFragment implements TimePickerDialogFragment.onTimeSelectedListener, View.OnClickListener {

    private LoadingDialog dialog;
    private FragmentTimesettingBinding binding;
    private MainActivity activity;
    private Read4 read4;

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
        //太阳能时间设置
        binding.taiyangnengStart.setOnClickListener(this);
        binding.taiyangnengStop.setOnClickListener(this);

        //洗浴高峰
        binding.xiyugaofengStart.setOnClickListener(this);
        binding.xiyugaofengStop.setOnClickListener(this);

        //三段洗浴供水
        binding.xiyugongshui1Start.setOnClickListener(this);
        binding.xiyugongshui1Stop.setOnClickListener(this);
        binding.xiyugongshui2Start.setOnClickListener(this);
        binding.xiyugongshui2Stop.setOnClickListener(this);
        binding.xiyugongshui3Start.setOnClickListener(this);
        binding.xiyugongshui3Stop.setOnClickListener(this);


        //三组机组
        binding.jizuStart1.setOnClickListener(this);
        binding.jizuStart2.setOnClickListener(this);
        binding.jizuStart3.setOnClickListener(this);
        binding.jizuStop1.setOnClickListener(this);
        binding.jizuStop2.setOnClickListener(this);
        binding.jizuStop3.setOnClickListener(this);


        //三段食堂时间设置
        binding.shitangStart1.setOnClickListener(this);
        binding.shitangStart2.setOnClickListener(this);
        binding.shitangStart3.setOnClickListener(this);
        binding.shitangStop1.setOnClickListener(this);
        binding.shitangStop2.setOnClickListener(this);
        binding.shitangStop3.setOnClickListener(this);

        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refreshLayout.setRefreshing(false);
                        initView(read4);
                    }
                }, 2000);
            }
        });
        setRxBus();

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
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
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "set_time");

            switch (currentId) {
                case R.id.taiyangnengStart:
                    dialog.setWaitText("正在设置太阳能起始时间...");
                    jsonObject.put("name1", "SetSolar_hour_start_ID");
                    jsonObject.put("name2", "SetSolar_min_start_ID");

//                binding.taiyangnengStart.setText(time);
                    break;
                case R.id.taiyangnengStop:
                    dialog.setWaitText("正在设置太阳能结束时间...");
                    jsonObject.put("name1", "SetSolar_hour_stop_ID");
                    jsonObject.put("name2", "SetSolar_min_stop_ID");
                    break;
                case R.id.xiyugaofengStart:
                    dialog.setWaitText("正在设置洗浴高峰起始时间...");
                    jsonObject.put("name1", "SetPeak_hour_start_ID");
                    jsonObject.put("name2", "SetPeak_min_start_ID");
                    //      binding.xiyugaofengStart.setText(time);

                    break;
                case R.id.xiyugaofengStop:
                    dialog.setWaitText("正在设置洗浴高峰结束时间...");
                    jsonObject.put("name1", "SetPeak_hour_stop_ID");
                    jsonObject.put("name2", "SetPeak_min_stop_ID");
                    //    binding.xiyugaofengStop.setText(time);
                    break;

                case R.id.xiyugongshui1Start:
                    dialog.setWaitText("正在设置洗浴供水开始时间1...");
                    jsonObject.put("name1", "SetBath_hour_start1_ID");
                    jsonObject.put("name2", "SetBath_min_start1_ID");
                    break;
                case R.id.xiyugongshui1Stop:
                    dialog.setWaitText("正在设置洗浴供水结束时间1...");
                    jsonObject.put("name1", "SetBath_hour_stop1_ID");
                    jsonObject.put("name2", "SetBath_min_stop1_ID");
                    break;
                case R.id.xiyugongshui2Start:
                    dialog.setWaitText("正在设置洗浴供水开始时间2...");
                    jsonObject.put("name1", "SetBath_hour_start2_ID");
                    jsonObject.put("name2", "SetBath_min_start2_ID");
                    break;
                case R.id.xiyugongshui2Stop:
                    dialog.setWaitText("正在设置洗浴供水结束时间2...");
                    jsonObject.put("name1", "SetBath_hour_stop2_ID");
                    jsonObject.put("name2", "SetBath_min_stop2_ID");
                    break;
                case R.id.xiyugongshui3Start:
                    dialog.setWaitText("正在设置洗浴供水开始时间3...");
                    jsonObject.put("name1", "SetBath_hour_start3_ID");
                    jsonObject.put("name2", "SetBath_min_start3_ID");
                    break;
                case R.id.xiyugongshui3Stop:
                    dialog.setWaitText("正在设置洗浴供水结束时间3...");
                    jsonObject.put("name1", "SetBath_hour_stop3_ID");
                    jsonObject.put("name2", "SetBath_min_stop3_ID");
                    break;

                case R.id.jizuStart1:
                    dialog.setWaitText("正在设置机组1工作开始时间...");
                    jsonObject.put("name1", "SetJizu_hour_start1_ID");
                    jsonObject.put("name2", "SetJizu_min_start1_ID");
                    break;
                case R.id.jizuStop1:
                    dialog.setWaitText("正在设置机组1工作结束时间...");
                    jsonObject.put("name1", "SetJizu_hour_stop1_ID");
                    jsonObject.put("name2", "SetJizu_min_stop1_ID");
                    break;
                case R.id.jizuStart2:
                    dialog.setWaitText("正在设置机组2工作开始时间...");
                    jsonObject.put("name1", "SetJizu_hour_start2_ID");
                    jsonObject.put("name2", "SetJizu_min_start2_ID");
                    break;
                case R.id.jizuStop2:
                    dialog.setWaitText("正在设置机组2工作结束时间...");
                    jsonObject.put("name1", "SetJizu_hour_stop2_ID");
                    jsonObject.put("name2", "SetJizu_min_stop2_ID");
                    break;
                case R.id.jizuStart3:
                    dialog.setWaitText("正在设置机组3工作开始时间...");
                    jsonObject.put("name1", "SetJizu_hour_start3_ID");
                    jsonObject.put("name2", "SetJizu_min_start3_ID");
                    break;
                case R.id.jizuStop3:
                    dialog.setWaitText("正在设置机组3工作结束时间...");
                    jsonObject.put("name1", "SetJizu_hour_stop3_ID");
                    jsonObject.put("name2", "SetJizu_min_stop3_ID");
                    break;
                case R.id.shitangStart1:
                    dialog.setWaitText("正在设置食堂1开始时间...");
                    jsonObject.put("name1", "SetDining_hour_start1_ID");
                    jsonObject.put("name2", "SetDining_min_start1_ID");
                    break;
                case R.id.shitangStop1:
                    dialog.setWaitText("正在设置食堂1结束时间...");
                    jsonObject.put("name1", "SetDining_hour_stop1_ID");
                    jsonObject.put("name2", "SetDining_min_stop1_ID");
                    break;
                case R.id.shitangStart2:
                    dialog.setWaitText("正在设置食堂2开始时间...");
                    jsonObject.put("name1", "SetDining_hour_start2_ID");
                    jsonObject.put("name2", "SetDining_min_start2_ID");
                    break;
                case R.id.shitangStop2:
                    dialog.setWaitText("正在设置食堂2结束时间...");
                    jsonObject.put("name1", "SetDining_hour_stop2_ID");
                    jsonObject.put("name2", "SetDining_min_stop2_ID");
                    break;
                case R.id.shitangStart3:
                    dialog.setWaitText("正在设置食堂3开始时间...");
                    jsonObject.put("name1", "SetDining_hour_start3_ID");
                    jsonObject.put("name2", "SetDining_min_start3_ID");
                    break;
                case R.id.shitangStop3:
                    dialog.setWaitText("正在设置食堂3结束时间...");
                    jsonObject.put("name1", "SetDining_hour_stop3_ID");
                    jsonObject.put("name2", "SetDining_min_stop3_ID");
                    break;
            }
            jsonObject.put("parameter1", hourOfDay + "");
            jsonObject.put("parameter2", minute + "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dialog.show();
        activity.binder.sendOrder(jsonObject.toString());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        }, 2000);

    }


    private void setRxBus() {
        RxBus.getInstance()
                .toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<CmdMsg>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(CmdMsg cmdMsg) {
                        if (binding.refreshLayout != null)
                            binding.refreshLayout.setRefreshing(false);

                        if (cmdMsg.status == 1) {
                            try {
                                JSONObject jsonObject = new JSONObject(cmdMsg.msg);
                                if (jsonObject.has("action")) {
                                    if (jsonObject.getString("action").equals("read4")) {
                                        read4 = null;
                                        read4 = JSON.parseObject(cmdMsg.msg, Read4.class);
                                        initView(read4);
                                    } else if (jsonObject.getString("action").equals("feedback")) {
                                        Utils.setValue(read4, jsonObject.getString("name"), jsonObject.getString("message"));
                                        UiUtils.showToast("设置成功");
                                        initView(read4);
                                    }
                                }
                                if (jsonObject.has("Error_code")) {
                                    UiUtils.showToast(jsonObject.getString("message"));
                                    LogUtil.i(jsonObject.toString());
                                    initView(read4);
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void initView(Read4 read4) {
        handler.removeCallbacksAndMessages(null);
        LogUtil.i("TimeSettingFragment",read4.toString());
        binding.time.setText(read4.created_at);

        binding.taiyangnengStart.setText(read4.SetSolar_hour_start_ID + ":" + read4.SetSolar_min_start_ID);
        binding.taiyangnengStop.setText(read4.SetSolar_hour_stop_ID + ":" + read4.SetSolar_min_stop_ID);

        binding.xiyugaofengStart.setText(read4.SetPeak_hour_start_ID + ":" + read4.SetPeak_min_start_ID);
        binding.xiyugaofengStop.setText(read4.SetPeak_hour_stop_ID + ":" + read4.SetPeak_min_stop_ID);


        binding.xiyugongshui1Start.setText(read4.SetBath_hour_start1_ID + ":" + read4.SetBath_min_start1_ID);
        binding.xiyugongshui1Stop.setText(read4.SetBath_hour_stop1_ID + ":" + read4.SetBath_min_stop1_ID);
        binding.xiyugongshui2Start.setText(read4.SetBath_hour_start2_ID + ":" + read4.SetBath_min_start2_ID);
        binding.xiyugongshui2Stop.setText(read4.SetBath_hour_stop2_ID + ":" + read4.SetBath_min_stop2_ID);
        binding.xiyugongshui3Start.setText(read4.SetBath_hour_start3_ID + ":" + read4.SetBath_min_start3_ID);
        binding.xiyugongshui3Stop.setText(read4.SetBath_hour_stop3_ID + ":" + read4.SetBath_min_stop3_ID);


        binding.jizuStart1.setText(read4.SetJizu_hour_start1_ID + ":" + read4.SetJizu_min_start1_ID);
        binding.jizuStop1.setText(read4.SetJizu_hour_stop1_ID + ":" + read4.SetJizu_min_stop1_ID);
        binding.jizuStart2.setText(read4.SetJizu_hour_start2_ID + ":" + read4.SetJizu_min_start2_ID);
        binding.jizuStop2.setText(read4.SetJizu_hour_stop2_ID + ":" + read4.SetJizu_min_stop2_ID);
        binding.jizuStart3.setText(read4.SetJizu_hour_start3_ID + ":" + read4.SetJizu_min_start3_ID);
        binding.jizuStop3.setText(read4.SetJizu_hour_stop3_ID + ":" + read4.SetJizu_min_stop3_ID);


        binding.shitangStart1.setText(read4.SetDining_hour_start1_ID + ":" + read4.SetDining_min_start1_ID);
        binding.shitangStop1.setText(read4.SetDining_hour_stop1_ID + ":" + read4.SetDining_min_stop1_ID);
        binding.shitangStart2.setText(read4.SetDining_hour_start2_ID + ":" + read4.SetDining_min_start2_ID);
        binding.shitangStop2.setText(read4.SetDining_hour_stop2_ID + ":" + read4.SetDining_min_stop2_ID);
        binding.shitangStart3.setText(read4.SetDining_hour_start3_ID + ":" + read4.SetDining_min_start3_ID);
        binding.shitangStop3.setText(read4.SetDining_hour_stop3_ID + ":" + read4.SetDining_min_stop3_ID);


        handler.removeCallbacksAndMessages(null);
        if (dialog!=null){
            dialog.dismiss();
        }
        if (binding.refreshLayout!=null){
            binding.refreshLayout.setRefreshing(false);
        }
    }


    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        binding.refreshLayout.setRefreshing(true);
        new RefreshThread().start();
//        getData();
    }


    public void getData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "read4");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (activity.binder != null)
            activity.binder.sendOrder(jsonObject.toString());

//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                binding.refreshLayout.setRefreshing(false);
//
//            }
//        },2000);

    }

    Handler handler = new Handler();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isStart = false;
        handler.removeCallbacksAndMessages(null);
    }


    private boolean isStart = true;

    private class RefreshThread extends Thread {
        @Override
        public void run() {
            getData();
//            while (isStart) {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }


}
