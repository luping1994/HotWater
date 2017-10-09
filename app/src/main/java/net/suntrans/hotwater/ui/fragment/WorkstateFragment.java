package net.suntrans.hotwater.ui.fragment;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.ExploreByTouchHelper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.R;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Read1;
import net.suntrans.hotwater.bean.Read2;
import net.suntrans.hotwater.databinding.FragmentWorkstateBinding;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.utils.Utils;
import net.suntrans.looney.widgets.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.widget.Switch;

import com.alibaba.fastjson.JSON;
import com.trello.rxlifecycle.android.FragmentEvent;

import java.util.Timer;
import java.util.TimerTask;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WorkstateFragment extends LazyLoadFragment implements CompoundButton.OnCheckedChangeListener {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private MainActivity activity;

    private FragmentWorkstateBinding binding;
    private Read2 read2;
    private LoadingDialog dialog;


    public WorkstateFragment() {
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_workstate, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateState(false);
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "settings");


                    if (checkedId == R.id.zidong) {
                        jsonObject.put("name", "Operation_mode_ID");
                        jsonObject.put("parameter", "1");
                        updateState(false);
                    } else {
                        jsonObject.put("name", "Operation_mode_ID");
                        jsonObject.put("parameter", "0");
                        updateState(true);
                    }
                    if (activity.binder != null)
                        activity.binder.sendOrder(jsonObject.toString());
                    if (dialog == null) {
                        dialog = new LoadingDialog(getContext());
                        dialog.setCancelable(false);
                        dialog.setWaitText("请稍后...");
                    }
                    dialog.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            initView(read2);
                        }
                    }, 2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        binding.tynB1.setOnCheckedChangeListener(this);
        binding.tynB2.setOnCheckedChangeListener(this);
        binding.feirexunhuanB.setOnCheckedChangeListener(this);
        binding.feirexunhuanB2.setOnCheckedChangeListener(this);
        binding.reshuizhuanyiB1.setOnCheckedChangeListener(this);
        binding.reshuizhuanyiB2.setOnCheckedChangeListener(this);
        binding.ranqijizu.setOnCheckedChangeListener(this);
        binding.reshuigongyingB.setOnCheckedChangeListener(this);
        binding.reshuigongyingB2.setOnCheckedChangeListener(this);
        binding.xieshuifa.setOnCheckedChangeListener(this);
        binding.reshuizhuanyifa.setOnCheckedChangeListener(this);
        binding.jirebushuifa.setOnCheckedChangeListener(this);
        binding.hengwenbushuifa.setOnCheckedChangeListener(this);
        binding.xiyumoduanhuishuifa.setOnCheckedChangeListener(this);
        binding.shitangmoduanfa.setOnCheckedChangeListener(this);


        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.refreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        RxBus.getInstance()
                .toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
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
                        if (cmdMsg.status == 1) {
                            try {
                                JSONObject jsonObject = new JSONObject(cmdMsg.msg);
                                if (jsonObject.has("action")) {
                                    if (jsonObject.getString("action").equals("read2")) {
                                        read2 = JSON.parseObject(cmdMsg.msg, Read2.class);
                                        LogUtil.i(read2.toString());
                                        initView(read2);
                                    }
                                    if (jsonObject.getString("action").equals("feedback")) {
                                        LogUtil.i("workStateFragment:" + jsonObject.toString());
                                        Utils.setValue(read2, jsonObject.getString("name"), jsonObject.getInt("message"));
                                        initView(read2);
                                    }
                                }
                                if (jsonObject.has("Error_code")) {
                                    UiUtils.showToast(jsonObject.getString("message"));
                                    LogUtil.i(jsonObject.toString());
                                    initView(read2);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void initView(Read2 read2) {
        if (read2 == null)
            return;
        LogUtil.i("WorkStateFragment" + read2.toString());
        if (read2.Operation_mode_ID == 1) {
            binding.zidong.setChecked(true);
        } else {
            binding.shoudong.setChecked(true);
        }
        binding.time.setText(read2.created_at);
        binding.tynB1.setChecked(read2.solar_pump1_flag_ID == 1 ? true : false);
        binding.tynB2.setChecked(read2.solar_pump2_flag_ID == 1 ? true : false);
        binding.feirexunhuanB.setChecked(read2.feire_pump1_ID == 1 ? true : false);
        binding.feirexunhuanB2.setChecked(read2.feire_pump2_ID == 1 ? true : false);
        binding.reshuizhuanyiB1.setChecked(read2.hottrans_pump1_ID == 1 ? true : false);
        binding.reshuizhuanyiB2.setChecked(read2.hottrans_pump2_ID == 1 ? true : false);
        binding.ranqijizu.setChecked(read2.ranqi_jizu_ID == 1 ? true : false);
        binding.reshuigongyingB.setChecked(read2.hotsupply_pump1_ID == 1 ? true : false);
        binding.reshuigongyingB2.setChecked(read2.hotsupply_pump2_ID == 1 ? true : false);


        binding.xieshuifa.setChecked(read2.xieshui_fa_ID == 1 ? true : false);
        binding.reshuizhuanyifa.setChecked(read2.hottrans_fa_ID == 1 ? true : false);
        binding.jirebushuifa.setChecked(read2.jiresupply_fa_ID == 1 ? true : false);
        binding.hengwenbushuifa.setChecked(read2.hengwensupply_fa_ID == 1 ? true : false);
        binding.xiyumoduanhuishuifa.setChecked(read2.bathback_fa_ID == 1 ? true : false);
        binding.shitangmoduanfa.setChecked(read2.diningback_fa_ID == 1 ? true : false);

        binding.xieshuifaTrue.setText(read2.Xieshuifa_true_ID == 1 ? "关闭"
                : read2.Xieshuifa_true_ID == 2 ? "关闭中" : read2.Xieshuifa_true_ID == 3 ? "开启中" : "开启");
        binding.reshuizhuanyifaTrue.setText(read2.Hottransfa_true_ID == 1 ? "关闭"
                : read2.Hottransfa_true_ID == 2 ? "关闭中" : read2.Hottransfa_true_ID == 3 ? "开启中" : "开启");
        binding.jirebushuifaTrue.setText(read2.Jirefa_ID == 1 ? "关闭"
                : read2.Jirefa_ID == 2 ? "关闭中" : read2.Jirefa_ID == 3 ? "开启中" : "开启");
        binding.guowenbushuifaTrue.setText(read2.Hengwenfa_ID == 1 ? "关闭"
                : read2.Hengwenfa_ID == 2 ? "关闭中" : read2.Hengwenfa_ID == 3 ? "开启中" : "开启");
        binding.xiyuhuishuifaTrue.setText(read2.Bathfa_ID == 1 ? "关闭"
                : read2.Bathfa_ID == 2 ? "关闭中" : read2.Bathfa_ID == 3 ? "开启中" : "开启");
        binding.shitanghuishuifaTrue.setText(read2.Diningfa_ID == 1 ? "关闭"
                : read2.Diningfa_ID == 2 ? "关闭中" : read2.Diningfa_ID == 3 ? "开启中" : "开启");


        updateState(binding.zidong.isChecked() ? false : true);
        handler.removeCallbacksAndMessages(null);
        if (dialog != null)
            dialog.dismiss();
        binding.refreshLayout.setRefreshing(false);

    }

    private void updateState(boolean canSwitch) {
        if (canSwitch) {
            binding.tynB1.setEnabled(true);
            binding.tynB2.setEnabled(true);
            binding.xieshuifa.setEnabled(true);
            binding.feirexunhuanB.setEnabled(true);
            binding.feirexunhuanB2.setEnabled(true);
            binding.reshuizhuanyiB1.setEnabled(true);
            binding.reshuizhuanyiB2.setEnabled(true);
            binding.reshuizhuanyifa.setEnabled(true);
            binding.ranqijizu.setEnabled(true);
            binding.reshuigongyingB.setEnabled(true);
            binding.reshuigongyingB2.setEnabled(true);
            binding.jirebushuifa.setEnabled(true);
            binding.hengwenbushuifa.setEnabled(true);
            binding.xiyumoduanhuishuifa.setEnabled(true);
            binding.shitangmoduanfa.setEnabled(true);
        } else {
            binding.tynB1.setEnabled(false);
            binding.tynB2.setEnabled(false);
            binding.xieshuifa.setEnabled(false);
            binding.feirexunhuanB.setEnabled(false);
            binding.feirexunhuanB2.setEnabled(false);
            binding.reshuizhuanyiB1.setEnabled(false);
            binding.reshuizhuanyiB2.setEnabled(false);
            binding.reshuizhuanyifa.setEnabled(false);
            binding.ranqijizu.setEnabled(false);
            binding.reshuigongyingB.setEnabled(false);
            binding.reshuigongyingB2.setEnabled(false);
            binding.jirebushuifa.setEnabled(false);
            binding.hengwenbushuifa.setEnabled(false);
            binding.xiyumoduanhuishuifa.setEnabled(false);
            binding.shitangmoduanfa.setEnabled(false);
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
        new RefreshThread().start();
    }


    public void getData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "read2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (activity.binder != null)
            activity.binder.sendOrder(jsonObject.toString());

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
//
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        LogUtil.i(isChecked + "");
        if (dialog == null) {
            dialog = new LoadingDialog(getContext());
            dialog.setCancelable(false);
            dialog.setWaitText("请稍后...");
        }
        dialog.show();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "settings");

            switch (buttonView.getId()) {
                case R.id.tynB1:
                    jsonObject.put("name", "solar_pump1_flag_ID");
                    break;
                case R.id.tynB2:
                    jsonObject.put("name", "solar_pump2_flag_ID");

                    break;
                case R.id.feirexunhuanB:
                    jsonObject.put("name", "feire_pump1_ID");

                    break;
                case R.id.feirexunhuanB2:
                    jsonObject.put("name", "feire_pump2_ID");

                    break;
                case R.id.reshuizhuanyiB1:
                    jsonObject.put("name", "hottrans_pump1_ID");

                    break;
                case R.id.reshuizhuanyiB2:
                    jsonObject.put("name", "hottrans_pump2_ID");

                    break;
                case R.id.ranqijizu:
                    jsonObject.put("name", "ranqi_jizu_ID");

                    break;
                case R.id.reshuigongyingB:
                    jsonObject.put("name", "hotsupply_pump1_ID");

                    break;
                case R.id.reshuigongyingB2:
                    jsonObject.put("name", "hotsupply_pump2_ID");

                    break;
                case R.id.xieshuifa:
                    jsonObject.put("name", "xieshui_fa_ID");
                    break;
                case R.id.reshuizhuanyifa:
                    jsonObject.put("name", "hottrans_fa_ID");
                    break;
                case R.id.jirebushuifa:
                    jsonObject.put("name", "jiresupply_fa_ID");
                    break;
                case R.id.hengwenbushuifa:
                    jsonObject.put("name", "hengwensupply_fa_ID");
                    break;
                case R.id.xiyumoduanhuishuifa:
                    jsonObject.put("name", "bathback_fa_ID");
                    break;
                case R.id.shitangmoduanfa:
                    jsonObject.put("name", "diningback_fa_ID");
                    break;
            }
            jsonObject.put("parameter", isChecked ? "1" : "0");
            if (activity.binder != null) {
                activity.binder.sendOrder(jsonObject.toString());
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    initView(read2);
                    dialog.dismiss();
                }
            }, 2000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getDataDelayed() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getData();
            }
        }, 2500);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        System.out.println("onPause");
//        timer.cancel();
        super.onPause();
    }

    private Timer timer = new Timer();

}
