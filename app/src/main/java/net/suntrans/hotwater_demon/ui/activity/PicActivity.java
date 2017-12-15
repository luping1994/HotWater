package net.suntrans.hotwater_demon.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioButton;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.FragmentEvent;

import net.suntrans.hotwater_demon.App;
import net.suntrans.hotwater_demon.MainActivity;
import net.suntrans.hotwater_demon.R;
import net.suntrans.hotwater_demon.api.RetrofitHelper;
import net.suntrans.hotwater_demon.bean.CmdMsg;
import net.suntrans.hotwater_demon.bean.Read2;
import net.suntrans.hotwater_demon.bean.Read2Entity;
import net.suntrans.hotwater_demon.ui.fragment.TimesettingDialogFragment;
import net.suntrans.hotwater_demon.ui.fragment.WendusettingDialogFragment;
import net.suntrans.hotwater_demon.utils.LogUtil;
import net.suntrans.hotwater_demon.utils.RxBus;
import net.suntrans.hotwater_demon.utils.UiUtils;
import net.suntrans.hotwater_demon.utils.Utils;
import net.suntrans.hotwater_demon.websocket.WebSocketService;
import net.suntrans.looney.widgets.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/9/11.
 */

public class PicActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webview;
    public WebSocketService.ibinder binder;
    public boolean allowControl;
    private AlertDialog dialog;
    private LoadingDialog dialog1;

    private RadioButton zidong;
    private RadioButton shoudong;

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


    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private Subscription subscribe;
    private Subscription subscribe1;
    private Read2 read2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pic);

        Intent intent = new Intent();
        intent.setClass(this, WebSocketService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        allowControl = App.getSharedPreferences().getBoolean("allowControl", false);

        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);

        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        zidong = (RadioButton) findViewById(R.id.zidong);
        shoudong = (RadioButton) findViewById(R.id.shoudong);

        setRxBus();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        webview = (WebView) findViewById(R.id.webview);
        setUpWebview(webview);

        init();
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });


//        webview.loadUrl("file:///android_asset/hotsystem/hotwater.html");
        webview.loadUrl("http://ht.suntrans-cloud.com/hotwater.html");
    }

    private void init() {
        zidong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "settings");

                    jsonObject.put("name", "Operation_mode_ID");
                    jsonObject.put("parameter", "1");

                    if (binder != null)
                        binder.sendOrder(jsonObject.toString());
                    if (dialog1 == null) {
                        dialog1 = new LoadingDialog(PicActivity.this);
                        dialog1.setCancelable(false);
                        dialog1.setWaitText("请稍后...");
                    }
                    dialog1.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog1.dismiss();
                            UiUtils.showToast("设置超时");
                            initView(read2);
                        }
                    }, 2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        shoudong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "settings");

                    jsonObject.put("name", "Operation_mode_ID");
                    jsonObject.put("parameter", "0");

                    if (binder != null)
                        binder.sendOrder(jsonObject.toString());
                    if (dialog1 == null) {
                        dialog1 = new LoadingDialog(PicActivity.this);
                        dialog1.setCancelable(false);
                        dialog1.setWaitText("请稍后...");
                    }
                    dialog1.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dialog1.dismiss();
                            UiUtils.showToast("设置超时");
                            initView(read2);
                        }
                    }, 2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Handler handler = new Handler();

    private void setUpWebview(WebView webview) {
        WebSettings settings = webview.getSettings();

        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setGeolocationEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        webview.setInitialScale(0);
        webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webview.setWebViewClient(new WebViewClient());
        webview.setVerticalScrollBarEnabled(false);

        WebSettings localWebSettings = webview.getSettings();
        localWebSettings.setJavaScriptEnabled(true);
        localWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        localWebSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);

        webview.setHorizontalScrollBarEnabled(false);//水平不显示
        webview.addJavascriptInterface(new AndroidtoJs(), "control");


    }


    // 继承自Object类
    public class AndroidtoJs extends Object {

        // 定义JS需要调用的方法
        // 被JS调用的方法必须加入@JavascriptInterface注解
        @JavascriptInterface
        public void alert(String control) {
            new AlertDialog.Builder(PicActivity.this)
                    .setMessage(control)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create().show();
        }
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
        if (!subscribe1.isUnsubscribed()) {
            subscribe1.unsubscribe();
        }
        handler.removeCallbacksAndMessages(null);
        webview.destroy();
        unbindService(connection);
        super.onDestroy();
    }

    private void setRxBus() {
        subscribe = RxBus.getInstance().toObserverable(CmdMsg.class)
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
                                                dialog = new AlertDialog.Builder(PicActivity.this)
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
                                    } else if (jsonObject.getString("action").equals("feedback")) {
                                        if (jsonObject.getString("name").equals("Sys_rtc_time_ID")) {
                                            UiUtils.showToast("校时成功!" + jsonObject.getString("message"));
//                                            initView(read4);
                                        }
                                        if (jsonObject.getString("action").equals("feedback")) {
                                            LogUtil.i("workStateFragment:" + jsonObject.toString());
                                            Utils.setValue(read2, jsonObject.getString("name"), jsonObject.getInt("message"));
                                            initView(read2);
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab1:
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "set_rtu_time");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (binder != null)
                    binder.sendOrder(jsonObject.toString());
                break;
            case R.id.fab2:
                TimesettingDialogFragment fragment = new TimesettingDialogFragment();
                fragment.show(getSupportFragmentManager(), "timeSetting");

                break;
            case R.id.fab3:
                WendusettingDialogFragment wendusettingDialogFragment = new WendusettingDialogFragment();
                wendusettingDialogFragment.show(getSupportFragmentManager(), "wenduSetting");
                break;
        }
    }


    public void getData() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("action", "read2");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        if (activity.binder != null)
//            activity.binder.sendOrder(jsonObject.toString());

        subscribe1 = RetrofitHelper.getApi().getRead2()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Read2Entity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(Read2Entity read2Entity) {
                        read2 = read2Entity.info.lists;
                        initView(read2Entity.info.lists);

                    }
                });

    }

    private void initView(Read2 read2) {
        handler.removeCallbacksAndMessages(null);
        if (dialog1!=null)
            dialog1.dismiss();
        if (read2 == null)
            return;
        LogUtil.i("WorkStateFragment11111111" + read2.toString());
        if (read2.Operation_mode_ID == 1) {
            zidong.setChecked(true);
        } else {
            shoudong.setChecked(true);
        }
    }
}
