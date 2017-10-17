package net.suntrans.hotwater.api;

import net.suntrans.hotwater.bean.Read1Entity;
import net.suntrans.hotwater.bean.Read2Entity;
import net.suntrans.hotwater.bean.Read3Entity;
import net.suntrans.hotwater.bean.Read4Entity;
import net.suntrans.hotwater.bean.WarningEntity;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by Looney on 2017/1/4.
 */

public interface Api {


    /**
     * 登录
     *
     * @param
     * @return
     */
    @POST("hotwater/index.php/home/index/alarm_mobile")
    Observable<WarningEntity> getWarningList();

    /**
     * 登录
     *
     * @param
     * @return
     */
    @FormUrlEncoded
    @POST("hotwater/index.php/home/index/alarm_mobile_fk")
    Observable<WarningEntity> handlerError(@Field("data_alarm") String data_alarm);

    /**
     * read1
     *
     * @param
     * @return
     */
    @POST("hotwater/index.php/home/index/read1")
    Observable<Read1Entity> getRead1();

    /**
     * read2
     *
     * @param
     * @return
     */
    @POST("hotwater/index.php/home/index/read2")
    Observable<Read2Entity> getRead2();

    /**
     * read3
     *
     * @param
     * @return
     */
    @POST("hotwater/index.php/home/index/read3")
    Observable<Read3Entity> getRead3();

    /**
     * read4
     *
     * @param
     * @return
     */
    @POST("hotwater/index.php/home/index/read4")
    Observable<Read4Entity> getRead4();

}
