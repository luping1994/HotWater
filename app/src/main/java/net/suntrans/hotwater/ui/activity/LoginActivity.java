package net.suntrans.hotwater.ui.activity;

import android.content.Intent;

import net.suntrans.hotwater.App;
import net.suntrans.hotwater.MainActivity;
import net.suntrans.looney.utils.UiUtils;

/**
 * Created by Looney on 2017/8/28.
 */

public class LoginActivity extends net.suntrans.looney.login.LoginActivity {


    @Override
    protected void loginFromServer(String username, String password) {
        if (username.equals("admin")&&password.equals("111111")){
          dialog.dismiss();
            App.getSharedPreferences().edit().putString("username",username)
                    .putString("password",password)
                    .commit();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            dialog.dismiss();
            UiUtils.showToast("账号或密码错误");
        }
    }
}
