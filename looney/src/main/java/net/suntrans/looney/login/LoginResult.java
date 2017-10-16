package net.suntrans.looney.login;

/**
 * Created by Looney on 2017/4/19.
 */

public class LoginResult {
    public String access_token;
    public String expires_in;
    public String token_type;
    public String scope;
    public String refresh_token;
    public String error_description;
    public String error;

    @Override
    public String toString() {
        return "LoginResult{" +
                "access_token='" + access_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", token_type='" + token_type + '\'' +
                ", scope='" + scope + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                '}';
    }
}
