package net.suntrans.hotwater;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;


import net.suntrans.hotwater.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Looney on 2017/2/20.
 */

public class App extends Application {
    public static Application getApplication() {
        return application;
    }

    public static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = getApplication().getSharedPreferences("suntransconfig", Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    public static SharedPreferences sharedPreferences;
    private static Application application;

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

//        new Thread(){//copy assets目录下的开关信息数据库
//            @Override
//            public void run() {
//                try {
//                    CopySqliteFileFromRawToDatabases("IBMS");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//        if (!ENABLE_DEBUG)
//            PgyCrashManager.register(this);
//        Bugly.init(getApplicationContext(), "6ec9742d2e", false);
        registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.e("Application","onActivityCreated");
                Log.w("Application",activity.toString());
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }


    // 复制和加载区域数据库中的数据
    public String CopySqliteFileFromRawToDatabases(String SqliteFileName) throws IOException {

        // 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>

        File dir = new File("data/data/" + App.getApplication().getPackageName() + "/databases");
        LogUtil.i("!dir.exists()=" + !dir.exists());
        LogUtil.i("!dir.isDirectory()=" + !dir.isDirectory());

        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdir();
        }

        File file = new File(dir, SqliteFileName);
        InputStream inputStream = null;
        OutputStream outputStream = null;

        //通过IO流的方式，将assets目录下的数据库文件，写入到SD卡中。
        if (!file.exists()) {
            try {
                file.createNewFile();

                inputStream = App.getApplication().getClass().getClassLoader().getResourceAsStream("assets/" + SqliteFileName);
                outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int len;

                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }

            }

        }
        return file.getPath();
    }

    public static final String APP_ID = "6ec9742d2e"; // TODO 替换成bugly上注册的appid

}
