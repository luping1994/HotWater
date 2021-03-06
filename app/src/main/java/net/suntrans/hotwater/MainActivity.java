package net.suntrans.hotwater;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.pgyersdk.update.PgyUpdateManager;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.databinding.ActivityMainBinding;
import net.suntrans.hotwater.ui.activity.PicActivity;
import net.suntrans.hotwater.ui.fragment.SettingFragment;
import net.suntrans.hotwater.ui.fragment.StatusFragment;
import net.suntrans.hotwater.ui.fragment.UserFragment;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.websocket.WebSocketService;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends RxAppCompatActivity implements StatusFragment.OnFragmentInteractionListener {
    private ActivityMainBinding binding;
    private Fragment[] fragments;
    public WebSocketService.ibinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (WebSocketService.ibinder) service;
//            System.out.println("绑定成功");
//            handler.postDelayed(runnable, 500);
//            handler2.postDelayed(runnable2, 1500);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private StatusFragment fragment;
    private AlertDialog.Builder builder;
    private AlertDialog dialog;
    public boolean allowControl;

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

//        allowControl = App.getSharedPreferences().getBoolean("allowControl", false);
        allowControl = true;

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        setSupportActionBar(binding.toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

//        binding.logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("action", s[i]);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                binder.sendOrder(jsonObject.toString());
//                i++;
//                if (i > 3)
//                    i = 0;
//            }
//        });
        RxBus.getInstance().toObserverable(CmdMsg.class)
                .compose(this.<CmdMsg>bindUntilEvent(ActivityEvent.DESTROY))
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

                        } else if (cmdMsg.status == 1) {
                            try {
                                JSONObject jsonObject = new JSONObject(cmdMsg.msg);
                                if (jsonObject.has("action")) {
                                    if (jsonObject.getString("action").equals("Warning")) {
                                        String name = jsonObject.getString("name");
                                        String message = jsonObject.getString("message");
                                        String s1 = WebSocketService.warningDictionaries.get(name);
                                        if (message.equals("1")) {
                                            if (dialog == null) {
                                                dialog = new AlertDialog.Builder(MainActivity.this)
                                                        .setNegativeButton("确定", null)
                                                        .setTitle("警告").create();
                                            }
                                            if (s1 != null) {
                                                dialog.setMessage(s1);
                                                if (!dialog.isShowing()) {
                                                    dialog.show();
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        PgyUpdateManager.register(this, "net.suntrans.hotwater.fileProvider");
    }


    private boolean isShowDialog = false;
    @Override
    protected void onPause() {
        super.onPause();
        isShowDialog = false;
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
        try {
            PgyUpdateManager.unregister();
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.removeCallbacksAndMessages(null);
        handler2.removeCallbacksAndMessages(null);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.pic) {
//            startActivity(new Intent(this, PicActivity.class));
//        }
//        return super.onOptionsItemSelected(item);
//    }


    public void getData1() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("action", "read1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (binder != null)
            binder.sendOrder(jsonObject.toString());

//        try {
//            Thread.sleep(400);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


    }

    private void getData2() {
        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put("action", "read2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (binder != null)
            binder.sendOrder(jsonObject2.toString());
    }

    private long[] mHits = new long[2];

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 2000)) {
                finish();
//                android.os.Process.killProcess(android.os.Process.myPid());
            } else {
                UiUtils.showToast("再按一次退出");
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private Handler handler = new Handler();
    private Handler handler2 = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getData1();
            handler.postDelayed(this, 4000);
        }
    };
    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            getData2();
            handler.postDelayed(this, 4000);
        }
    };
}
