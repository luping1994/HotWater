package net.suntrans.hotwater_demon.bean;

import java.util.List;

/**
 * Created by Looney on 2017/10/17.
 * Des:
 */

public class WarningEntity {


    /**
     * code : 1
     * message : 查询成功
     * info : [{"name":"食堂末端回水阀异常报警","device":"diningback_fa_warning_ID","created_at":"2017-10-17 10:40:01"}]
     */

    public int code;
    public String message;
    public List<InfoBean> info;

    public static class InfoBean {
        /**
         * name : 食堂末端回水阀异常报警
         * device : diningback_fa_warning_ID
         * created_at : 2017-10-17 10:40:01
         */

        public String name;
        public String device;
        public String created_at;
    }
}
