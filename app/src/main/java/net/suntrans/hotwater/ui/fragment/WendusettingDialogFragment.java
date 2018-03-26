package net.suntrans.hotwater.ui.fragment;


import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;

import net.suntrans.hotwater.R;
import net.suntrans.hotwater.api.Api;
import net.suntrans.hotwater.api.RetrofitHelper;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Read3;
import net.suntrans.hotwater.bean.Read3Entity;
import net.suntrans.hotwater.databinding.FragmentWendusettingBinding;
import net.suntrans.hotwater.ui.activity.PicActivity;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.utils.Utils;
import net.suntrans.hotwater.widgets.FullScreenDialog;
import net.suntrans.looney.widgets.LoadingDialog;

import org.json.JSONObject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class WendusettingDialogFragment extends DialogFragment implements View.OnClickListener, ChangeNameDialogFragment_land.ChangeNameListener {


    private FragmentWendusettingBinding binding;

    protected Api api = RetrofitHelper.getApi();
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    private PicActivity activity;
    private Read3 read3;
    private SparseArray<String> datas;
    private LoadingDialog dialog;
    private Observable<Read3Entity> observable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (PicActivity) context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FullScreenDialog dialog = new FullScreenDialog(getContext());
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wendusetting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        setListener();
        setRxBus();
        handler2.post(runnable);
    }


    private void initData() {
        datas = new SparseArray<>();
        binding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

//        datas.put(R.id.fangbaoWendu,"防爆温度");
//        datas.put(R.id.yunxuwendu,"允许温度");
//        datas.put(R.id.fandongwendu,"防冻温度");
//        datas.put(R.id.yuzhi_top,"温差阈值-上限");
//        datas.put(R.id.yuzhi_low,"温差阈值-下限");
//        datas.put(R.id.zhuanshuiwendu,"转水温度");
//        datas.put(R.id.henbushuiwendu,"恒补水温度");
//        datas.put(R.id.xuyuhuiwendu,"洗浴回温度");
//        datas.put(R.id.shitanghuiwendu,"食堂回温度");
//
//
//        datas.put(R.id.jiregaoyewei,"集热高液位");
//        datas.put(R.id.jireyewei,"集热液位");
//        datas.put(R.id.bushuigaoyewei,"补水高液位");
//        datas.put(R.id.bushuidiyewei,"补水低液位");
//        datas.put(R.id.henwenyewei,"恒温液位");
//        datas.put(R.id.gaofengyewei,"高峰液位");
//        datas.put(R.id.daigongyewei,"待供液位");
        datas.put(R.id.fangbaoWendu, "SetJire_temp_H_ID");
        datas.put(R.id.yunxuwendu, "SetJire_temp_L_ID");
        datas.put(R.id.fandongwendu, "SetIce_temp_ID");
        datas.put(R.id.yuzhi_top, "SetCha_temp_H_ID");
        datas.put(R.id.yuzhi_low, "SetCha_temp_L_ID");
        datas.put(R.id.zhuanshuiwendu, "SetJire_temp_trans_ID");
        datas.put(R.id.henbushuiwendu, "SetHengwen_temp_trans_ID");
        datas.put(R.id.xuyuhuiwendu, "SetBath_temp_back_ID");
        datas.put(R.id.shitanghuiwendu, "SetDining_temp_back_ID");


        datas.put(R.id.jiregaoyewei, "SetJire_level_max_ID");
        datas.put(R.id.jireyewei, "SetJire_level_ID");
        datas.put(R.id.bushuigaoyewei, "SetJire_supply_H_ID");
        datas.put(R.id.bushuidiyewei, "SetJire_supply_L_ID");
        datas.put(R.id.henwenyewei, "SetHengwen_level_ID");
        datas.put(R.id.gaofengyewei, "SetHengwen_level_peak_ID");
        datas.put(R.id.daigongyewei, "SetHengwen_level_bath_ID");

        datas.put(R.id.jireman, "Jire_level_full_ID");
        datas.put(R.id.henwenman, "Hengwen_level_full_ID");
        datas.put(R.id.SetHengwen_level_max_ID, "SetHengwen_level_max_ID");
        datas.put(R.id.shuiyasheding, "SetSupply_press_ID");
        datas.put(R.id.tiaopinzhouqi, "SetWindow_press_ID");
        datas.put(R.id.SetFeire_press_ID, "SetFeire_press_ID");
        datas.put(R.id.Supply_press_ID, "Supply_press_ID");
        datas.put(R.id.Feire_press_ID, "Feire_press_ID");
        datas.put(R.id.SetJire_temp_safe_ID, "SetJire_temp_safe_ID");
    }

    boolean isSetting = false;

    private void setRxBus() {
        mCompositeSubscription.add(RxBus.getInstance()
                .toObserverable(CmdMsg.class)
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
                                    if (jsonObject.getString("action").equals("read3")) {
//                                        System.out.println("我被执行了");
                                        read3 = null;
                                        read3 = JSON.parseObject(cmdMsg.msg, Read3.class);
                                        initView(read3);
                                    } else if (jsonObject.getString("action").equals("feedback")) {

                                        LogUtil.e(cmdMsg.msg);
                                        Utils.setValue(read3, jsonObject.getString("name"), jsonObject.getString("message"));
                                        initView(read3);
                                        if (isSetting) {
                                            UiUtils.showToast("设置成功");
                                            isSetting = false;
                                        }
                                    }
                                    if (jsonObject.has("Error_code")) {
                                        UiUtils.showToast(jsonObject.getString("message"));
                                        LogUtil.i(jsonObject.toString());
                                        initView(read3);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }));
    }

    private void initView(Read3 read3) {
        handler.removeCallbacksAndMessages(null);
        LogUtil.i("WendusettingFragment", read3.toString());

//        binding.time.setText(read3.created_at);


        binding.fangbaoWendu.setText(read3.SetJire_temp_H_ID);
        binding.yunxuwendu.setText(read3.SetJire_temp_L_ID);
        binding.fandongwendu.setText(read3.SetIce_temp_ID);
        binding.yuzhiTop.setText(read3.SetCha_temp_H_ID);
        binding.yuzhiLow.setText(read3.SetCha_temp_L_ID);
        binding.zhuanshuiwendu.setText(read3.SetJire_temp_trans_ID);
        binding.henbushuiwendu.setText(read3.SetHengwen_temp_trans_ID);
        binding.xuyuhuiwendu.setText(read3.SetBath_temp_back_ID);
        binding.shitanghuiwendu.setText(read3.SetDining_temp_back_ID);

        binding.jireyewei.setText(read3.SetJire_level_ID);
        binding.jiregaoyewei.setText(read3.SetJire_level_max_ID);
        binding.bushuigaoyewei.setText(read3.SetJire_supply_H_ID);
        binding.bushuidiyewei.setText(read3.SetJire_supply_L_ID);
        binding.henwenyewei.setText(read3.SetHengwen_level_ID);
        binding.gaofengyewei.setText(read3.SetHengwen_level_peak_ID);
        binding.daigongyewei.setText(read3.SetHengwen_level_bath_ID);
        binding.SetHengwenLevelMaxID.setText(read3.SetHengwen_level_max_ID);

        binding.jireman.setText(read3.Jire_level_full_ID);
        binding.henwenman.setText(read3.Hengwen_level_full_ID);
        binding.tiaopinzhouqi.setText(read3.Set_period_ID);
        binding.shuiyasheding.setText(read3.SetSupply_press_ID);
        binding.SetFeirePressID.setText(read3.SetFeire_press_ID);
        binding.SupplyPressID.setText(read3.Supply_press_ID);
        binding.FeirePressID.setText(read3.Feire_press_ID);
        binding.SetJireTempSafeID.setText(read3.SetJire_temp_safe_ID);

        handler.removeCallbacksAndMessages(null);
        if (dialog != null) {
            dialog.dismiss();
        }
        binding.refreshLayout.setRefreshing(false);

    }

    private void setListener() {
        binding.fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        binding.fangbaoWendu.setOnClickListener(this);
        binding.yunxuwendu.setOnClickListener(this);
        binding.fandongwendu.setOnClickListener(this);
        binding.yuzhiTop.setOnClickListener(this);
        binding.yuzhiLow.setOnClickListener(this);
        binding.zhuanshuiwendu.setOnClickListener(this);
        binding.henbushuiwendu.setOnClickListener(this);
        binding.xuyuhuiwendu.setOnClickListener(this);
        binding.shitanghuiwendu.setOnClickListener(this);


        binding.jireyewei.setOnClickListener(this);
        binding.jiregaoyewei.setOnClickListener(this);
        binding.bushuigaoyewei.setOnClickListener(this);
        binding.bushuidiyewei.setOnClickListener(this);
        binding.henwenyewei.setOnClickListener(this);
        binding.daigongyewei.setOnClickListener(this);
        binding.gaofengyewei.setOnClickListener(this);


        binding.jireman.setOnClickListener(this);
        binding.henwenman.setOnClickListener(this);
        binding.tiaopinzhouqi.setOnClickListener(this);
        binding.shuiyasheding.setOnClickListener(this);
        binding.SetFeirePressID.setOnClickListener(this);
        binding.SupplyPressID.setOnClickListener(this);
        binding.FeirePressID.setOnClickListener(this);
        binding.SetJireTempSafeID.setOnClickListener(this);
        binding.SetHengwenLevelMaxID.setOnClickListener(this);

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
    }

    private int currentSelectedId = -1;

    @Override
    public void onClick(View v) {
        currentSelectedId = v.getId();
        showChangedNameDialog(datas.get(currentSelectedId));
    }


    public void getData() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("action", "read3");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (activity.binder != null)
//            activity.binder.sendOrder(jsonObject.toString());
        mCompositeSubscription.add(
                RetrofitHelper.getApi().getRead3()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())

                        .subscribe(new Subscriber<Read3Entity>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                if (binding.refreshLayout != null) {
                                    binding.refreshLayout.setRefreshing(false);
                                }
                            }

                            @Override
                            public void onNext(Read3Entity read3Entity) {
                                read3 = read3Entity.info.lists;
                                initView(read3Entity.info.lists);
                                if (binding.refreshLayout != null) {
                                    binding.refreshLayout.setRefreshing(false);
                                }
                            }
                        }));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onUnsubscribe();
        handler.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
    }


    ChangeNameDialogFragment_land fragment2;

    private void showChangedNameDialog(String name) {
        fragment2 = (ChangeNameDialogFragment_land) getChildFragmentManager().findFragmentByTag("ChangeNameDialogFragment");
        if (fragment2 == null) {
            fragment2 = ChangeNameDialogFragment_land.newInstance(name);
            fragment2.setCancelable(true);
            fragment2.setListener(this);
        }
        fragment2.show(getChildFragmentManager(), "ChangeNameDialogFragment");
    }

    @Override
    public void changeName(String value) {
        if (!activity.allowControl) {
            UiUtils.showToast("您没有操作权限");
            initView(read3);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "settings");
            jsonObject.put("name", datas.get(currentSelectedId));
            jsonObject.put("parameter", value);
            isSetting = true;
            if (activity.binder != null) {
                activity.binder.sendOrder(jsonObject.toString());
            }
            if (dialog == null) {
                dialog = new LoadingDialog(getContext());
                dialog.setCancelable(false);
                dialog.setWaitText("请稍后");
            }
            dialog.show();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    isSetting = false;
                    UiUtils.showToast("设置超时,请稍后再试");
                }
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    Handler handler = new Handler();
    Handler handler2 = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getData();
//            handler2.postDelayed(this, 2000);
        }
    };

    public void addSubscription(Observable observable, Subscriber subscriber) {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }

        mCompositeSubscription.add(observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber));
    }

    public void onUnsubscribe() {
        if (mCompositeSubscription != null && mCompositeSubscription.hasSubscriptions()) {
            mCompositeSubscription.unsubscribe();
        }
    }

}
