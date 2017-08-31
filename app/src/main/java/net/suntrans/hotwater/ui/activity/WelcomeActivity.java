package net.suntrans.hotwater.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import net.suntrans.hotwater.App;
import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.R;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

/**
 * Created by Looney on 2017/7/21.
 */

public class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        init();
    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        } else {
            check();
        }
    }

    private boolean ischeck = false;

    private void check() {
        if (ischeck) {
            return;
        }
        ischeck = true;
        try {
            String access_token = App.getSharedPreferences().getString("access_token", "-1");
            String expires_in = App.getSharedPreferences().getString("expires_in", "-1");
            String user_id = App.getSharedPreferences().getString("user_id", "-1");

            long firsttime = App.getSharedPreferences().getLong("firsttime", -1l);
            long currenttime = System.currentTimeMillis();
            long d = (currenttime - firsttime) / 1000;

            if (access_token.equals("-1") || expires_in.equals("-1") || firsttime == -1l ) {
                handler.sendEmptyMessageDelayed(START_LOGIN, 400);
            } else {
                if (d > (Long.valueOf(expires_in) - 1 * 24 * 3600)) {
                    handler.sendEmptyMessageDelayed(START_LOGIN, 400);
                } else {
                    handler.sendEmptyMessageDelayed(START_MAIN, 400);
                }
            }
        } catch (Exception e) {
            handler.sendEmptyMessageDelayed(START_LOGIN, 400);
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    final int START_LOGIN = 0;
    final int START_MAIN = 1;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == START_LOGIN) {
                startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                finish();
            }
            if (msg.what == START_MAIN) {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                finish();
            }
        }
    };


    public void checkPermission() {
        HiPermission.create(this)
                .checkSinglePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                    @Override
                    public void onClose() {

                    }

                    @Override
                    public void onFinish() {
                        LogUtil.i("权限授予完成");
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        UiUtils.showToast("您已经拒绝授予存储权限,应用无法运行");
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                        LogUtil.i("权限通过");
                        check();
                    }
                });
    }
}
