package net.suntrans.hotwater;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.pgyersdk.update.PgyUpdateManager;

import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.databinding.ActivityMainBinding;
import net.suntrans.hotwater.ui.fragment.SettingFragment;
import net.suntrans.hotwater.ui.fragment.StatusFragment;
import net.suntrans.hotwater.ui.fragment.UserFragment;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.websocket.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements StatusFragment.OnFragmentInteractionListener {
    int i = 0;
    private ActivityMainBinding binding;
    private Fragment[] fragments;
    public WebSocketService.ibinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
            fragment.getData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };
    private StatusFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fragment = new StatusFragment();
        SettingFragment fragment1 = new SettingFragment();
        UserFragment fragment2 = new UserFragment();
        fragments = new Fragment[]{fragment, fragment1, fragment2};
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(adapter);
        binding.viewpager.setOffscreenPageLimit(3);
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.jiankong:
                        binding.viewpager.setCurrentItem(0, false);
                        break;
                    case R.id.setting:
                        binding.viewpager.setCurrentItem(1, false);

                        break;
                    case R.id.user:
                        binding.viewpager.setCurrentItem(2, false);

                        break;
                }
                return true;
            }
        });
        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        binding.bottomNav.setSelectedItemId(R.id.jiankong);
                        break;
                    case 1:
                        binding.bottomNav.setSelectedItemId(R.id.setting);
                        break;
                    case 2:
                        binding.bottomNav.setSelectedItemId(R.id.user);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        final String[] s = {"read1", "read2", "read3", "read4"};

        binding.logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", s[i]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binder.sendOrder(jsonObject.toString());
                i++;
                if (i > 3)
                    i = 0;
            }
        });
        RxBus.getInstance().toObserverable(CmdMsg.class)
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
                        if (cmdMsg.status == 0) {
                            UiUtils.showToast(cmdMsg.msg);
                        }

                    }
                });
        PgyUpdateManager.register(this, "net.suntrans.hotwater.fileProvider");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    class PagerAdapter extends FragmentPagerAdapter {


        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return 3;
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        PgyUpdateManager.unregister();

    }


}
