package net.suntrans.looney.login;


import static net.suntrans.looney.utils.UiUtils.checkNotNull;

/**
 * Created by Looney on 2017/4/19.
 */

public class LoginPresenter implements Login.Presenter {

    public LoginPresenter(Login.View view) {
        mView = checkNotNull(view);
    }

    private Login.View mView;

    @Override
    public void login(String username, String password) {
        if (username == null || username.equals("")) {
            mView.fieldEmpty("账号为不能为空");
            return;
        }

        if (password == null || password.equals("")) {
            mView.fieldEmpty("密码为不能为空");
            return;
        }
        mView.startLogin(username,password);

    }

    @Override
    public void signUp() {

    }

    @Override
    public void forgetPassword() {

    }


}
