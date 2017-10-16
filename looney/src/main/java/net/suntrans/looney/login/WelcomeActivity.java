package net.suntrans.looney.login;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import net.suntrans.looney.AppBase;
import net.suntrans.looney.utils.LogUtil;

import java.util.List;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissonItem;

/**
 * Created by Looney on 2017/5/8.
 */

public abstract class WelcomeActivity extends AppCompatActivity {
    private static final String TAG = "WelcomeActivity";
    private boolean isfrist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        isfrist = AppBase.getSharedPreferences().getBoolean("isfrist", true);

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
            String access_token = AppBase.getSharedPreferences().getString("access_token", "-1");
            String expires_in = AppBase.getSharedPreferences().getString("expires_in", "-1");
            String user_id = AppBase.getSharedPreferences().getString("user_id", "-1");

            long firsttime = AppBase.getSharedPreferences().getLong("firsttime", -1l);
            long currenttime = System.currentTimeMillis();
            long d = (currenttime - firsttime) / 1000;

            if (access_token.equals("-1") || expires_in.equals("-1") || firsttime == -1l || user_id.equals("-1")) {
                handler.sendEmptyMessageDelayed(START_LOGIN, 1000);
            } else {
                if (d > (Long.valueOf(expires_in) - 1 * 24 * 3600)) {
                    handler.sendEmptyMessageDelayed(START_LOGIN, 1000);
                } else {
                    handler.sendEmptyMessageDelayed(START_MAIN, 1000);
                }
            }
        } catch (Exception e) {
            handler.sendEmptyMessageDelayed(START_LOGIN, 1000);
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
                startLogin();
                finish();
            }
            if (msg.what == START_MAIN) {
                startMain();
                finish();
            }
        }
    };

    public void checkPermission() {
        final List<PermissonItem> permissionItems = getPermissonItem();
        HiPermission.create(WelcomeActivity.this)
                .permissions(permissionItems)
                .checkMutiPermission(new PermissionCallback() {
                    @Override
                    public void onClose() {
                        LogUtil.i("关闭");
                    }

                    @Override
                    public void onFinish() {
                        LogUtil.i("结束了我的儿子");
                        check();
                    }

                    @Override
                    public void onDeny(String permisson, int position) {
                        LogUtil.i("拒绝了权限" + permisson);
                    }

                    @Override
                    public void onGuarantee(String permisson, int position) {
                        LogUtil.i("允许：" + permisson);
                    }
                });
    }

    protected abstract List<PermissonItem> getPermissonItem();
    protected abstract int getLayoutRes();
    protected abstract void startMain();
    protected abstract void startLogin();
}
