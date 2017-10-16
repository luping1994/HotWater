package net.suntrans.looney.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by Looney on 2017/4/19.
 */

public class LoginModel extends BaseObservable {
    private String username;
    private String password;

    public LoginModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    private boolean isSavePass;

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isSavePass() {
        return isSavePass;
    }

    public void setSavePass(boolean savePass) {
        isSavePass = savePass;
    }

}
