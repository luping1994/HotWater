package net.suntrans.looney;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import net.suntrans.looney.utils.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * Created by Looney on 2017/2/20.
 */

public class AppBase extends Application {
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
//
        super.onCreate();
        application = this;

    }


    // 复制和加载区域数据库中的数据
    public String CopySqliteFileFromRawToDatabases(String SqliteFileName) throws IOException {

        // 第一次运行应用程序时，加载数据库到data/data/当前包的名称/database/<db_name>

        File dir = new File("data/data/" + AppBase.getApplication().getPackageName() + "/databases");
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

                inputStream = AppBase.getApplication().getClass().getClassLoader().getResourceAsStream("assets/" + SqliteFileName);
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
