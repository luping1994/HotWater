package net.suntrans.hotwater;

import android.content.Intent;
import android.view.ViewGroup;

import com.tencent.bugly.crashreport.CrashReport;

import net.suntrans.hotwater.websocket.WebSocketService;
import net.suntrans.looney.AppBase;

/**
 * Created by Looney on 2017/2/20.
 */

public class App extends AppBase {
    public static String user_id="0";
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "ee82d6b8f4", false);
        startService(new Intent(this, WebSocketService.class));

    }

}
