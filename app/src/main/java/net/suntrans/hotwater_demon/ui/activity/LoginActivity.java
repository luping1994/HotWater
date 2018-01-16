package net.suntrans.hotwater_demon.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.pgyersdk.update.PgyUpdateManager;

import net.suntrans.hotwater_demon.App;
import net.suntrans.hotwater_demon.MainActivity;
import net.suntrans.hotwater_demon.api.RetrofitHelper;
import net.suntrans.hotwater_demon.bean.AuthEntity;
import net.suntrans.hotwater_demon.bean.LoginEntity;
import net.suntrans.looney.utils.UiUtils;

import retrofit2.Response;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static net.suntrans.hotwater_demon.BuildConfig.DEBUG;

/**
 * Created by Looney on 2017/8/28.
 */

public class LoginActivity extends net.suntrans.looney.login.LoginActivity {


    private Subscription subscribe;
    private Subscription subscribe1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!DEBUG)
            PgyUpdateManager.register(this, "net.suntrans.hotwater_demon.fileProvider");
    }

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
                        UiUtils.showToast("账号或密码错误");
                        dialog.dismiss();

                    }

                    @Override
                    public void onNext(LoginEntity loginEntity) {
                        dialog.dismiss();
                        if (loginEntity.code == 1) {
                            App.getSharedPreferences().edit().putString("username", username)
                                    .putString("password", password)
                                    .commit();
                            checkAuth(username);
                        } else {
                            UiUtils.showToast("账号或密码错误");
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (subscribe != null) {
            if (!subscribe.isUnsubscribed()) {
                subscribe.unsubscribe();
            }
        }
        if (subscribe1 != null) {
            if (!subscribe1.isUnsubscribed()) {
                subscribe1.unsubscribe();
            }
        }

        if (!DEBUG)
            PgyUpdateManager.unregister();

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
                            App.getSharedPreferences().edit().putBoolean("allowControl", false).commit();
                        } else {
                            App.getSharedPreferences().edit().putBoolean("allowControl", true).commit();
                        }
                        startActivity(new Intent(LoginActivity.this, PicActivity.class));
                        finish();
                    }
                });
    }
}
