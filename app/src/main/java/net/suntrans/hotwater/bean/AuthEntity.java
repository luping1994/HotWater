package net.suntrans.hotwater.bean;

/**
 * Created by Looney on 2017/10/24.
 * Des:
 */

public class AuthEntity {


    /**
     * code : 1
     * message : 认证失败
     * info : {"token":1}
     */

    public int code;
    public String message;
    public InfoBean info;

    public static class InfoBean {
        /**
         * token : 1
         */

        public String token;
    }
}
