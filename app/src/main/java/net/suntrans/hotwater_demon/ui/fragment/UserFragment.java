package net.suntrans.hotwater_demon.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.suntrans.hotwater_demon.BuildConfig;
import net.suntrans.hotwater_demon.R;
import net.suntrans.hotwater_demon.databinding.FragmentUserBinding;
import net.suntrans.hotwater_demon.ui.activity.AboutActivity;
import net.suntrans.hotwater_demon.ui.activity.LoginActivity;
import net.suntrans.hotwater_demon.ui.activity.WarningActivity;
import net.suntrans.hotwater_demon.utils.UiUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment implements View.OnClickListener {


    private FragmentUserBinding binding;

    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        binding.currentVersion.setText("当前版本" + BuildConfig.VERSION_NAME);
        binding.exit.setOnClickListener(this);
        binding.signOut.setOnClickListener(this);
        binding.about.setOnClickListener(this);
        binding.checkVersion.setOnClickListener(this);
        binding.message.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.exit:
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case R.id.checkVersion:
                UiUtils.showToast("当前已是最新版本!");
                break;
            case R.id.about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.signOut:
                new AlertDialog.Builder(getContext())
                        .setMessage("注销登录?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getContext(), LoginActivity.class));
                                getActivity().finish();
                            }
                        }).setNegativeButton("取消",null).create().show();
                break;
            case R.id.message:
                startActivity(new Intent(getActivity(), WarningActivity.class));
                break;
        }
    }
}
