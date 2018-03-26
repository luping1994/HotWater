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
import android.support.v7.app.AppCompatDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.hotwater.R;
import net.suntrans.hotwater.api.Api;
import net.suntrans.hotwater.api.RetrofitHelper;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Read4;
import net.suntrans.hotwater.bean.Read4Entity;
import net.suntrans.hotwater.databinding.FragmentTimesettingBinding;
import net.suntrans.hotwater.ui.activity.PicActivity;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.utils.Utils;

import net.suntrans.hotwater.widgets.FullScreenDialog;
import net.suntrans.looney.widgets.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimesettingDialogFragment extends DialogFragment implements TimePickerDialogFragment.onTimeSelectedListener, View.OnClickListener {

    private LoadingDialog dialog;
    private FragmentTimesettingBinding binding;
    private PicActivity activity;
    private Read4 read4;
    private Observable<Read4Entity> observable;
    protected Api api = RetrofitHelper.getApi();
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();

    public TimesettingDialogFragment() {
        // Required empty public constructor
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_timesetting, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.jiaoshi.setOnClickListener(this);
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

        binding.fanhui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

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
        binding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));

        setRxBus();

        handler2.post(runnable);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (PicActivity) context;
    }

    int currentId;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.jiaoshi) {
            if (!activity.allowControl) {
                UiUtils.showToast("您没有操作权限");
//                initView(read4);
                return;
            }
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("action", "set_rtu_time");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (activity != null) {
                if (activity.binder != null)
                    activity.binder.sendOrder(jsonObject.toString());
            }

            return;
        }
        currentId = v.getId();
        TimePickerDialogFragment
                fragment = new TimePickerDialogFragment();
        fragment.setOnTimeSelectedListener(this);
        fragment.show(getChildFragmentManager(), "timeDialog");
    }

    boolean isSetting = false;

    @Override
    public void onTimeSelected(int hourOfDay, int minute) {
        if (!activity.allowControl) {
            UiUtils.showToast("您没有操作权限");
            initView(read4);
            return;
        }
        if (dialog == null) {
            dialog = new LoadingDialog(getContext());
            dialog.setCancelable(false);
        }
//        System.out.println(hourOfDay + ":" + minute);
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
                    dialog.setWaitText("正在设置洗浴供水开始时间#1...");
                    jsonObject.put("name1", "SetBath_hour_start1_ID");
                    jsonObject.put("name2", "SetBath_min_start1_ID");
                    break;
                case R.id.xiyugongshui1Stop:
                    dialog.setWaitText("正在设置洗浴供水结束时间#1...");
                    jsonObject.put("name1", "SetBath_hour_stop1_ID");
                    jsonObject.put("name2", "SetBath_min_stop1_ID");
                    break;
                case R.id.xiyugongshui2Start:
                    dialog.setWaitText("正在设置洗浴供水开始时间#2...");
                    jsonObject.put("name1", "SetBath_hour_start2_ID");
                    jsonObject.put("name2", "SetBath_min_start2_ID");
                    break;
                case R.id.xiyugongshui2Stop:
                    dialog.setWaitText("正在设置洗浴供水结束时间#2...");
                    jsonObject.put("name1", "SetBath_hour_stop2_ID");
                    jsonObject.put("name2", "SetBath_min_stop2_ID");
                    break;
                case R.id.xiyugongshui3Start:
                    dialog.setWaitText("正在设置洗浴供水开始时间#3...");
                    jsonObject.put("name1", "SetBath_hour_start3_ID");
                    jsonObject.put("name2", "SetBath_min_start3_ID");
                    break;
                case R.id.xiyugongshui3Stop:
                    dialog.setWaitText("正在设置洗浴供水结束时间#3...");
                    jsonObject.put("name1", "SetBath_hour_stop3_ID");
                    jsonObject.put("name2", "SetBath_min_stop3_ID");
                    break;

                case R.id.jizuStart1:
                    dialog.setWaitText("正在设置机组工作开始时间#1...");
                    jsonObject.put("name1", "SetJizu_hour_start1_ID");
                    jsonObject.put("name2", "SetJizu_min_start1_ID");
                    break;
                case R.id.jizuStop1:
                    dialog.setWaitText("正在设置机组工作结束时间#1...");
                    jsonObject.put("name1", "SetJizu_hour_stop1_ID");
                    jsonObject.put("name2", "SetJizu_min_stop1_ID");
                    break;
                case R.id.jizuStart2:
                    dialog.setWaitText("正在设置机组工作开始时间#2...");
                    jsonObject.put("name1", "SetJizu_hour_start2_ID");
                    jsonObject.put("name2", "SetJizu_min_start2_ID");
                    break;
                case R.id.jizuStop2:
                    dialog.setWaitText("正在设置机组工作结束时间#2...");
                    jsonObject.put("name1", "SetJizu_hour_stop2_ID");
                    jsonObject.put("name2", "SetJizu_min_stop2_ID");
                    break;
                case R.id.jizuStart3:
                    dialog.setWaitText("正在设置机组工作开始时间#3...");
                    jsonObject.put("name1", "SetJizu_hour_start3_ID");
                    jsonObject.put("name2", "SetJizu_min_start3_ID");
                    break;
                case R.id.jizuStop3:
                    dialog.setWaitText("正在设置机组工作结束时间#3...");
                    jsonObject.put("name1", "SetJizu_hour_stop3_ID");
                    jsonObject.put("name2", "SetJizu_min_stop3_ID");
                    break;
                case R.id.shitangStart1:
                    dialog.setWaitText("正在设置食堂开始时间#1...");
                    jsonObject.put("name1", "SetDining_hour_start1_ID");
                    jsonObject.put("name2", "SetDining_min_start1_ID");
                    break;
                case R.id.shitangStop1:
                    dialog.setWaitText("正在设置食堂结束时间#1...");
                    jsonObject.put("name1", "SetDining_hour_stop1_ID");
                    jsonObject.put("name2", "SetDining_min_stop1_ID");
                    break;
                case R.id.shitangStart2:
                    dialog.setWaitText("正在设置食堂开始时间#2...");
                    jsonObject.put("name1", "SetDining_hour_start2_ID");
                    jsonObject.put("name2", "SetDining_min_start2_ID");
                    break;
                case R.id.shitangStop2:
                    dialog.setWaitText("正在设置食堂结束时间#2...");
                    jsonObject.put("name1", "SetDining_hour_stop2_ID");
                    jsonObject.put("name2", "SetDining_min_stop2_ID");
                    break;
                case R.id.shitangStart3:
                    dialog.setWaitText("正在设置食堂开始时间#3...");
                    jsonObject.put("name1", "SetDining_hour_start3_ID");
                    jsonObject.put("name2", "SetDining_min_start3_ID");
                    break;
                case R.id.shitangStop3:
                    dialog.setWaitText("正在设置食堂结束时间#3...");
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
        isSetting = true;
        if (activity.binder != null)
            activity.binder.sendOrder(jsonObject.toString());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                isSetting = false;
                if (dialog != null)
                    dialog.dismiss();
            }
        }, 2000);

    }


    private void setRxBus() {
       mCompositeSubscription.add( RxBus.getInstance()
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
                                       if (jsonObject.getString("name").equals("Sys_rtc_time_ID")) {
                                           UiUtils.showToast("校时成功!" + jsonObject.getString("message"));
//                                            initView(read4);
                                       } else {
                                           Utils.setValue(read4, jsonObject.getString("name"), jsonObject.getString("message"));
                                           if (isSetting) {
                                               UiUtils.showToast("设置成功");
                                               isSetting = false;
                                           }
                                           initView(read4);
                                       }

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
               }));
    }

    private void initView(Read4 read4) {
        handler.removeCallbacksAndMessages(null);
        if (dialog != null) {
            dialog.dismiss();
        }
        if (binding.refreshLayout != null) {
            binding.refreshLayout.setRefreshing(false);
        }

        LogUtil.i("TimeSettingFragment", read4.toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String format = dateFormat.format(new Date());
        binding.time.setText(format);

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


    }


    public void getData() {
//
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("action", "read4");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (activity != null) {
//            if (activity.binder != null)
//                activity.binder.sendOrder(jsonObject.toString());
//        }


//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                binding.refreshLayout.setRefreshing(false);
//
//            }
//        },2000);

       mCompositeSubscription.add( RetrofitHelper.getApi().getRead4()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<Read4Entity>() {
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
                   public void onNext(Read4Entity read4Entity) {
                       read4 = read4Entity.info.lists;

                       initView(read4Entity.info.lists);
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
