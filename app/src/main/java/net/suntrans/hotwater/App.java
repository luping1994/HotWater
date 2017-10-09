package net.suntrans.hotwater;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Looney on 2017/2/20.
 */

public class App extends net.suntrans.looney.App {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashReport.initCrashReport(getApplicationContext(), "ee82d6b8f4", false);

    }

}
