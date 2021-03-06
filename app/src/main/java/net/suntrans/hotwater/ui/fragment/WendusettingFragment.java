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
import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.R;
import net.suntrans.hotwater.api.RetrofitHelper;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Read1Entity;
import net.suntrans.hotwater.bean.Read3;
import net.suntrans.hotwater.bean.Read3Entity;
import net.suntrans.hotwater.bean.Read4Entity;
import net.suntrans.hotwater.databinding.FragmentWendusettingBinding;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.utils.Utils;
import net.suntrans.looney.widgets.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class WendusettingFragment extends LazyLoadFragment implements View.OnClickListener, ChangeNameDialogFragment.ChangeNameListener {


    private FragmentWendusettingBinding binding;


    private MainActivity activity;
    private Read3 read3;
    private SparseArray<String> datas;
    private LoadingDialog dialog;
    private Observable<Read3Entity> observable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
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
                });
    }
    DecimalFormat fnum  =   new  DecimalFormat("##0.00");
    private void initView(Read3 read3) {
        handler.removeCallbacksAndMessages(null);
        LogUtil.i("WendusettingFragment", read3.toString());

        binding.time.setText(read3.created_at);


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



//        binding.SupplyPressID.setText(read3.Supply_press_ID);
        binding.FeirePressID.setText(read3.Feire_press_ID);
        binding.SetJireTempSafeID.setText(read3.SetJire_temp_safe_ID);
        float shuiyasheding = Float.valueOf(read3.SetSupply_press_ID)/10;
        float SetFeirePressID = Float.valueOf(read3.SetFeire_press_ID)/10;
        float Set_period_ID = Float.valueOf(read3.Set_period_ID)/10;

        binding.shuiyasheding.setText(fnum.format(shuiyasheding));
        binding.SetFeirePressID.setText(fnum.format(SetFeirePressID));
        binding.tiaopinzhouqi.setText(fnum.format(Set_period_ID));



        handler.removeCallbacksAndMessages(null);
        if (dialog != null) {
            dialog.dismiss();
        }
        binding.refreshLayout.setRefreshing(false);

    }

    private void setListener() {
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


    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    @Override
    protected void onFragmentVisibleChange(boolean isVisible) {
        super.onFragmentVisibleChange(isVisible);
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

        if (observable == null) {
            observable = RetrofitHelper.getApi().getRead3()
                    .compose(this.<Read3Entity>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

//        binding.refreshLayout.setRefreshing(true);
        observable.subscribe(new Subscriber<Read3Entity>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                if (binding.refreshLayout!=null){
                    binding.refreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onNext(Read3Entity read3Entity) {
                read3 = read3Entity.info.lists;
                initView(read3Entity.info.lists);
                if (binding.refreshLayout!=null){
                    binding.refreshLayout.setRefreshing(false);
                }
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
    }




    ChangeNameDialogFragment fragment2;

    private void showChangedNameDialog(String name) {
        fragment2 = (ChangeNameDialogFragment) getChildFragmentManager().findFragmentByTag("ChangeNameDialogFragment");
        if (fragment2 == null) {
            fragment2 = ChangeNameDialogFragment.newInstance(name);
            fragment2.setCancelable(true);
            fragment2.setListener(this);
        }
        fragment2.show(getChildFragmentManager(), "ChangeNameDialogFragment");
    }

    @Override
    public void changeName(String value) {
        if (!activity.allowControl){
            UiUtils.showToast("您没有操作权限");
            initView(read3);
            return;
        }
        JSONObject jsonObject = new JSONObject();
        String name = datas.get(currentSelectedId);
        if (name.equals("SetSupply_press_ID")||name.equals("SetFeire_press_ID")||name.equals("SetWindow_press_ID")){
            value = Float.valueOf(value)*10 +"";
        }
        try {
            jsonObject.put("action", "settings");
//            jsonObject.put("user_id", user_id);

            jsonObject.put("name", name);
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
}
