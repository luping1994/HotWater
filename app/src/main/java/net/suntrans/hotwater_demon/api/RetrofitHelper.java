package net.suntrans.hotwater_demon.api;

import net.suntrans.hotwater_demon.App;
import net.suntrans.looney.utils.LogUtil;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Looney on 2016/12/15.
 */

public class RetrofitHelper {

//    public static final String BASE_URL = "http://g.suntrans.net:8088/SuntransTest-Peace/";
//    public static final String BASE_URL = "http://gszy.suntrans-cloud.com:8080/gszyapp/";

    public static final String BASE_URL = "http://183.236.25.192:24206/";

    private static OkHttpClient mOkHttpClient;

    static {
        initOkHttpClient();
    }

    public static Api getApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
//                    .addConverterFactory(MyGsonConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(Api.class);
    }

    public static Api getLoginApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(mOkHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        return retrofit.create(Api.class);
    }


    private static void initOkHttpClient() {
//        System.out.println("wobeizhixingle=======================");
        Interceptor netInterceptor =
                new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {

                        String header = App.getSharedPreferences().getString("token", "-1");
//                        System.out.println(header);
//                        System.out.println(header.length());
                        Request original = chain.request();

                        RequestBody newBody = original.body();

//                        if (original.body() instanceof FormBody) {
//                            newBody = addParamsToFormBody((FormBody) original.body());
//                        } else {
//                            newBody =    addParamsToFormBody();
//                        }
                        Request newRequest = original.newBuilder()
                                .header("Authorization","Bearer "+ header)
                                .method(original.method(), newBody)
                                .build();

                        Response response = chain.proceed(newRequest);
                        return response;
                    }
                };


        if (mOkHttpClient == null) {
            synchronized (RetrofitHelper.class) {
                if (mOkHttpClient == null) {
                    mOkHttpClient = new OkHttpClient.Builder()
                            .addInterceptor(netInterceptor)
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
    }

    /**
     * 为FormBody类型请求体添加参数
     *
     * @param body
     * @return
     */
    private static FormBody addParamsToFormBody(FormBody body) {
        FormBody.Builder builder = new FormBody.Builder();
        String header = App.getSharedPreferences().getString("token", "raVnKIh8Rv");
//        String group = App.getSharedPreferences().getString("group", "raVnKIh8Rv");
//        String id = App.getSharedPreferences().getString("id", "-1");
        LogUtil.i("token", header);
        builder.add("token", header);
//        builder.add("group", group);
//        builder.add("id", id);
        //添加原请求体
        for (int i = 0; i < body.size(); i++) {
            builder.addEncoded(body.encodedName(i), body.encodedValue(i));
        }
        return builder.build();
    }

    /**
     * 为FormBody类型请求体添加参数
     *
     * @return
     */
    private static FormBody addParamsToFormBody() {
        FormBody.Builder builder = new FormBody.Builder();
        String header = App.getSharedPreferences().getString("token", "raVnKIh8Rv");
//        String group = App.getSharedPreferences().getString("group", "raVnKIh8Rv");
//        String id = App.getSharedPreferences().getString("id", "-1");
        LogUtil.i("token", header);
        builder.add("token", header);
//        builder.add("group", group);
//        builder.add("id", id);

        return builder.build();
    }


}
