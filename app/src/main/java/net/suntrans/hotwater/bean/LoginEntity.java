package net.suntrans.hotwater.bean;

/**
 * Created by Looney on 2017/10/24.
 * Des:
 */

public class LoginEntity {

    /**
     * code : 1
     * message : 登录成功
     */

    public int code;
    public String message;
    public Info info;

    public static class Info{
       public String id;
    }
}
