package net.suntrans.hotwater_demon;

import android.content.Intent;

import com.tencent.bugly.crashreport.CrashReport;

import net.suntrans.hotwater_demon.websocket.WebSocketService;
import net.suntrans.looney.AppBase;

/**
 * Created by Looney on 2017/2/20.
 */

public class App extends AppBase {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "ee82d6b8f4", false);
        startService(new Intent(this, WebSocketService.class));
    }

}
