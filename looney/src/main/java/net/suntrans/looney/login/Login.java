package net.suntrans.looney.login;

/**
 * Created by Looney on 2017/4/19.
 */

public interface Login {
    interface View  {

        void loginSuccess();

        void loginFiled();

        void startLogin(String username, String password);

        void fieldEmpty(String msg);
    }

    interface Presenter  {
        void login(String username, String password);
        void signUp();
        void forgetPassword();
    }
}
