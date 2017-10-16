package net.suntrans.looney.login;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.suntrans.looney.AppBase;
import net.suntrans.looney.R;
import net.suntrans.looney.databinding.ActivityLoginBinding;
import net.suntrans.looney.utils.UiUtils;
import net.suntrans.looney.widgets.LoadingDialog;

public abstract class LoginActivity extends AppCompatActivity implements Login.View {
    protected LoginPresenter presenter;
    protected LoginModel info;
    protected LoadingDialog dialog;
    protected ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((!isTaskRoot()) && (getIntent().hasCategory("android.intent.category.LAUNCHER")) && (getIntent().getAction() != null) && (getIntent().getAction().equals("android.intent.action.MAIN"))) {
            finish();
            return;
        }
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        String username = AppBase.getSharedPreferences().getString("username", "");
        String password = AppBase.getSharedPreferences().getString("password", "");
        info = new LoginModel(username, password);
        presenter = new LoginPresenter(this);
        binding.setUser(info);
        binding.setActionHandler(presenter);

    }

    @Override
    public void loginSuccess() {
        getInfo();
    }

    protected void getInfo() {

    }

    @Override
    public void loginFiled() {
        dialog.dismiss();
    }

    @Override
    public void startLogin(String username, String password) {
        if (dialog == null)
            dialog = new LoadingDialog(this, R.style.loading_dialog);
        dialog.setWaitText("正在登录...");
        dialog.show();
        loginFromServer(username,password);
    }


    @Override
    public void fieldEmpty(String msg) {
        UiUtils.showToast(msg);
    }

    protected abstract void loginFromServer(String username, String password);


}
