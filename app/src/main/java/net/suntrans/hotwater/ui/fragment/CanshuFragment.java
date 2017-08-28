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

import com.alibaba.fastjson.JSON;
import com.trello.rxlifecycle.components.support.RxFragment;

import net.suntrans.hotwater.App;
import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.R;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Read1;
import net.suntrans.hotwater.databinding.FragmentCanshuBinding;
import net.suntrans.hotwater.databinding.FragmentStatusBinding;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CanshuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CanshuFragment extends RxFragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private FragmentCanshuBinding binding;

    private MainActivity activity;

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
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CanshuFragment.
     */
    public static CanshuFragment newInstance(String param1, String param2) {
        CanshuFragment fragment = new CanshuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
                        if (cmdMsg.status == 1) {
                            try {
                                JSONObject jsonObject = new JSONObject(cmdMsg.msg);
                                if (jsonObject.has("action")) {
                                    if (jsonObject.getString("action").equals("read1")) {
                                        Read1 read1 = JSON.parseObject(cmdMsg.msg, Read1.class);
                                        initView(read1);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    private void initView(Read1 read1) {
        handler.removeCallbacksAndMessages(null);
        binding.refreshLayout.setRefreshing(false);

        LogUtil.i(read1.toString());
        binding.T1.setText(read1.Jire_temp_down_ID + "℃");
        binding.T2.setText(read1.Jire_temp_up_ID + "℃");
        binding.T3.setText(read1.Jire_temp_tank_ID + "℃");
        binding.T4.setText(read1.Hengwen_temp_tank_ID + "℃");
        binding.T5.setText(read1.Bath_temp_ID + "℃");
        binding.T6.setText(read1.Dining_temp_ID + "℃");
        binding.T7.setText(read1.Huanjing_temp_ID + "℃");
        binding.H1.setText(read1.Jire_level_ID + "%");
        binding.H2.setText(read1.Hengwen_level_ID + "%");
    }

    public void getData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "read1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (activity.binder != null)
            activity.binder.sendOrder(jsonObject.toString());
        if (binding.refreshLayout != null) {
            binding.refreshLayout.setRefreshing(true);
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.refreshLayout.setRefreshing(false);
                UiUtils.showToast("从服务器获取数据超时,请稍后再试");
            }
        }, 2000);
    }

    Handler handler = new Handler();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
