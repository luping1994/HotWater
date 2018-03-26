package net.suntrans.hotwater.ui.fragment;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.R;
import net.suntrans.hotwater.api.RetrofitHelper;
import net.suntrans.hotwater.bean.Read1;
import net.suntrans.hotwater.bean.Read1Entity;
import net.suntrans.hotwater.databinding.FragmentCanshuBinding;
import net.suntrans.hotwater.utils.LogUtil;

import java.text.DecimalFormat;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CanshuFragment extends LazyLoadFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FragmentCanshuBinding binding;

    private MainActivity activity;
    private Observable<Read1Entity> observable;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public CanshuFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        handler2.post(runnable);

    }

    @Override
    public void onPause() {
        super.onPause();
        handler2.removeCallbacksAndMessages(null);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected void onFragmentFirstVisible() {
        super.onFragmentFirstVisible();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_canshu, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        binding.refreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
//        RxBus.getInstance()
//                .toObserverable(CmdMsg.class)
//                .compose(this.<CmdMsg>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<CmdMsg>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(CmdMsg cmdMsg) {
//                        try {
//                            if (cmdMsg.status == 1) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject(cmdMsg.msg);
//                                    if (jsonObject.has("action")) {
//                                        if (jsonObject.getString("action").equals("read1")) {
//                                            Read1 read1 = JSON.parseObject(cmdMsg.msg, Read1.class);
//                                            initView(read1);
//                                        }
//                                    }
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
    }



    private void initView(Read1 read1) {
        handler.removeCallbacksAndMessages(null);
        binding.refreshLayout.setRefreshing(false);

        LogUtil.i("CanshuFragment", read1.toString());
        binding.T1.setText(read1.Jire_temp_down_ID + "℃");
        binding.T2.setText(read1.Jire_temp_up_ID + "℃");
        binding.T3.setText(read1.Jire_temp_tank_ID + "℃");
        binding.T4.setText(read1.Hengwen_temp_tank_ID + "℃");
        binding.T5.setText(read1.Bath_temp_ID + "℃");
        binding.T6.setText(read1.Dining_temp_ID + "℃");
        binding.T7.setText(read1.Huanjing_temp_ID + "℃");
        binding.H1.setText(read1.Jire_level_ID + "%");
        binding.H2.setText(read1.Hengwen_level_ID + "%");
       float a = Float.valueOf(read1.Supply_press_ID)/10;
       float b = Float.valueOf(read1.Feire_press_ID)/10;
        DecimalFormat fnum  =   new  DecimalFormat("##0.00");
        binding.SupplyPressID.setText(read1.Supply_press_ID == null ? "--" : fnum.format(a) + "MPa");
        binding.FeirePressID.setText(read1.Feire_press_ID == null ? "--" : fnum.format(b) + "MPa");
        binding.time.setText(read1.created_at);
    }


    public void getData() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("action", "read1");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (activity.binder != null)
//            activity.binder.sendOrder(jsonObject.toString());
////        if (binding.refreshLayout != null) {
////            binding.refreshLayout.setRefreshing(true);
////        }
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                binding.refreshLayout.setRefreshing(false);
//            }
//        }, 2000);

        if (observable == null) {
            observable = RetrofitHelper.getApi().getRead1()
                    .compose(this.<Read1Entity>bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }

//        binding.refreshLayout.setRefreshing(true);
        observable.subscribe(new Subscriber<Read1Entity>() {
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
            public void onNext(Read1Entity read1Entity) {
                initView(read1Entity.info.lists);
                if (binding.refreshLayout!=null){
                    binding.refreshLayout.setRefreshing(false);
                }
            }
        });
    }

    Handler handler = new Handler();
    Handler handler2 = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getData();
            handler2.postDelayed(this, 2000);
        }
    };


}
