package net.suntrans.hotwater.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.pgyersdk.update.PgyUpdateManager;

import net.suntrans.hotwater.App;
import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.api.RetrofitHelper;
import net.suntrans.hotwater.bean.AuthEntity;
import net.suntrans.hotwater.bean.LoginEntity;
import net.suntrans.looney.utils.UiUtils;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Looney on 2017/8/28.
 */

public class LoginActivity extends net.suntrans.looney.login.LoginActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PgyUpdateManager.register(this, "net.suntrans.hotwater.fileProvider");
    }

    private Subscription subscribe;
    private Subscription subscribe1;

    @Override
    protected void loginFromServer(final String username, final String password) {
        subscribe = RetrofitHelper.getApi().Login(username, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<LoginEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        UiUtils.showToast("连接服务器失败");
                        dialog.dismiss();

                    }

                    @Override
                    public void onNext(LoginEntity loginEntity) {
                        if (loginEntity.code == 1) {
                            App.user_id = loginEntity.info.id;
                            App.getSharedPreferences().edit().putString("username", username)
                                    .putString("password", password)
                                    .putString("user_id",loginEntity.info.id)
                                    .apply();
                            checkAuth(username);
                        } else {
                            dialog.dismiss();
                            UiUtils.showToast("账号或密码错误");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (!subscribe.isUnsubscribed()) {
            subscribe.unsubscribe();
        }
        if (!subscribe1.isUnsubscribed()) {
            subscribe1.unsubscribe();
        }
        super.onDestroy();
    }

    private void checkAuth(String username) {
        subscribe1 = RetrofitHelper.getApi().auth(username)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<AuthEntity>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        UiUtils.showToast("账号或密码错误");
                        dialog.dismiss();

                    }

                    @Override
                    public void onNext(AuthEntity loginEntity) {
                        dialog.dismiss();
                        if ("1".equals(loginEntity.info.token)) {
                            App.getSharedPreferences().edit().putBoolean("allowControl", false).apply();
                        } else {
                            App.getSharedPreferences().edit().putBoolean("allowControl", true).apply();
                        }
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }
}
