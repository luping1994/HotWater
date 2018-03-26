package net.suntrans.hotwater.ui.activity;

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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RadioButton;
import android.widget.ToggleButton;


import net.suntrans.hotwater.App;
import net.suntrans.hotwater.R;
import net.suntrans.hotwater.api.RetrofitHelper;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Read2;
import net.suntrans.hotwater.bean.Read2Entity;
import net.suntrans.hotwater.ui.fragment.TimesettingDialogFragment;
import net.suntrans.hotwater.ui.fragment.WendusettingDialogFragment;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;
import net.suntrans.hotwater.utils.Utils;
import net.suntrans.hotwater.websocket.WebSocketService;
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


    private FloatingActionButton fab0;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;
    private Subscription subscribe;
    private Subscription subscribe1;
    private Read2 read2;
    private ToggleButton button;

    private long lastTime =0;
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
        fab0 = (FloatingActionButton) findViewById(R.id.fab0);

        fab0.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);

        fab0.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        zidong = (RadioButton) findViewById(R.id.zidong);
        shoudong = (RadioButton) findViewById(R.id.shoudong);
        button = (ToggleButton) findViewById(R.id.toggleButton);

        View view = findViewById(R.id.toggle);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (System.currentTimeMillis()-lastTime<1000){

                    return;
                }
                lastTime = System.currentTimeMillis();
                boolean isChecked = button.isChecked();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("action", "settings");

                    jsonObject.put("name", "Operation_mode_ID");
                    jsonObject.put("parameter", isChecked?"0":"1");

                    if (binder != null)
                        binder.sendOrder(jsonObject.toString());
//                    if (dialog1 == null) {
//                        dialog1 = new LoadingDialog(PicActivity.this);
//                        dialog1.setCancelable(false);
//                        dialog1.setWaitText("请稍后...");
//                    }
//                    dialog1.show();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            dialog1.dismiss();
//                            UiUtils.showToast("设置超时");
//                            initView(read2);
//                        }
//                    }, 2000);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        setRxBus();

        setUpFullScreen();
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
        webview.loadUrl("http://ht.suntrans-cloud.com/lanzhou/hotwater.html");
    }

    private void setUpFullScreen() {
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(Color.TRANSPARENT);
        }
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
        getWindow().setAttributes(params);


//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
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
        webview.setVerticalScrollBarEnabled(false);
//        webview.setWebChromeClient(new WebChromeClient());

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
            case R.id.fab0:
                if (webview != null)
                    webview.reload();
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
        if (dialog1 != null)
            dialog1.dismiss();
        if (read2 == null)
            return;
        if (read2.Operation_mode_ID == 1) {
            zidong.setChecked(true);
            button.setChecked(true);
        } else {
            shoudong.setChecked(true);
            button.setChecked(false);
        }
    }
}
