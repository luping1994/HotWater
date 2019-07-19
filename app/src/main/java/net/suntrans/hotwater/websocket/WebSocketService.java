package net.suntrans.hotwater.websocket;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.arch.persistence.room.Room;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;

import net.suntrans.hotwater.App;
import net.suntrans.hotwater.MainActivity;
import net.suntrans.hotwater.bean.CmdMsg;
import net.suntrans.hotwater.bean.Message;
import net.suntrans.hotwater.bean.WarningHis;
import net.suntrans.hotwater.database.AppDatabase;
import net.suntrans.hotwater.database.DatabaseUtils;
import net.suntrans.hotwater.utils.LogUtil;
import net.suntrans.hotwater.utils.RxBus;
import net.suntrans.hotwater.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.WebSocket;

/**
 * Created by Looney on 2017/5/27.
 */

public class WebSocketService extends Service implements WebSocketWrapper.onReceiveListener {
    private WebSocketWrapper webSocketWrapper;
    private IBinder binder;
    public static Map<String, String> warningDictionaries;
    private NotificationManager nm;
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.i("service is onCreate!");
        initDictionaries();
        if (webSocketWrapper == null) {
            webSocketWrapper = new WebSocketWrapper();
            webSocketWrapper.setOnReceiveListener(this);
        }
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(myNetReceiver, mFilter);   //注册接收网络连接状态改变广播接收器
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        LogUtil.i("service is onBind!");
        binder = new ibinder() {
            @Override
            public boolean sendOrder(String order) {
                try {
                    JSONObject jsonObject = new JSONObject(order);
                    jsonObject.put("user_id",App.user_id);
                    order = jsonObject.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                boolean isSuccess = webSocketWrapper.sendMessage(order);
                if (!isSuccess) {
                    webSocketWrapper.connect();
                }
                return isSuccess;
            }
        };
        return binder;
    }

    private void initDictionaries() {
        warningDictionaries = new HashMap<>();
        warningDictionaries.put("slc10_1_warning_ID", "十通道1异常");
        warningDictionaries.put("slc10_2_warning_ID", "十通道2异常");
        warningDictionaries.put("slc6_1_warning_ID", "六通道1异常");
        warningDictionaries.put("slc6_2_warning_ID", "六通道2异常");
        warningDictionaries.put("slc6_3_warning_ID", "六通道3异常");
        warningDictionaries.put("slc6_4_warning_ID", "六通道4异常");
        warningDictionaries.put("Jire_temp_down_warning_ID", "集热水箱下温度T1异常");
        warningDictionaries.put("Jire_temp_up_warning_ID", "集热水箱上温度T2异常");
        warningDictionaries.put("Jire_temp_tank_warning_ID", "集热水箱温度T3异常");
        warningDictionaries.put("Hengwen_temp_tank_warning_ID", "恒温水箱温度T4异常");
        warningDictionaries.put("Bath_temp_warning_ID", "洗浴末端回水温度T5异常");
        warningDictionaries.put("Dining_temp_warning_ID", "食堂末端回水温度T6异常");
        warningDictionaries.put("Huanjing_temp_warning_ID", "环境温度T7异常");
        warningDictionaries.put("Jire_level_warning_ID", "集热水箱水位H1异常");
        warningDictionaries.put("Hengwen_level_warning_ID", "恒温水箱水位H2异常");
        warningDictionaries.put("Supply_press_warning_ID", "热水供应水压异常");
        warningDictionaries.put("Feire_press_warning_ID", "废热循环水压异常");

        warningDictionaries.put("solar_pump1_warning_ID", "太阳能循环泵1异常报警");
        warningDictionaries.put("solar_pump2_warning_ID", "太阳能循环泵2异常报警");
        warningDictionaries.put("xieshui_fa_warning_ID", "泄水阀异常报警");
        warningDictionaries.put("feire_pump1_warning_ID", "废热循环泵1异常报警");
        warningDictionaries.put("feire_pump2_warning_ID", "废热循环泵2异常报警");
        warningDictionaries.put("hottrans_pump1_warning_ID", "热水转移泵1异常报警");
        warningDictionaries.put("hottrans_pump2_warning_ID", "热水转移泵2异常报警");
        warningDictionaries.put("hottrans_fa_warning_ID", "热水转移阀异常报警");
        warningDictionaries.put("ranqi_jizu_warning_ID", "燃气机组异常报警");
        warningDictionaries.put("hotsupply_pump1_warning_ID", "热水供应泵1异常报警");
        warningDictionaries.put("hotsupply_pump2_warning_ID", "热水供应泵2异常报警");
        warningDictionaries.put("jiresupply_fa_warning_ID", "集热补水阀异常报警");
        warningDictionaries.put("hengwensupply_fa_warning_ID", "恒温补水阀异常报警");
        warningDictionaries.put("bathback_fa_warning_ID", "洗浴末端回水阀异常报警");
        warningDictionaries.put("diningback_fa_warning_ID", "食堂末端回水阀异常报警");
    }

    @Override
    public void onMessage(String s) {
        CmdMsg msg = new CmdMsg();
        msg.status = 1;
        msg.msg = s;

        try {
            JSONObject jsonObject = new JSONObject(msg.msg);
            if (jsonObject.has("action")) {
                if (jsonObject.getString("action").equals("Warning")) {
                    String name = jsonObject.getString("name");
                    String message = jsonObject.getString("message");
                    String s1 = warningDictionaries.get(name);
                    if (message.equals("1")) {
                        Message message1 = new Message();
                        message1.message = s1;
                        message1.title = "热水智能管控系统";
                        WarningHis warningHis = new WarningHis();
                        warningHis.setMessage(s1);
                        warningHis.setTime(new Date().toLocaleString());
                        tasks.add(new InsertTask().execute(warningHis));

                        //显示报警通知栏
                        Notification notification = UiUtils.getNotification(this, message1);
                        nm.notify(NOTIFICATION_ID, notification);
//                        System.out.println("发出报警:" + s1);
                        RxBus.getInstance().post(msg);

                    }

                } else {
                    RxBus.getInstance().post(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<AsyncTask> tasks = new ArrayList<>();

    class InsertTask extends AsyncTask<WarningHis, Void, Boolean> {

        @Override
        protected Boolean doInBackground(WarningHis... params) {
            //更新历史数据库数据
            DatabaseUtils.getInstance().warningDao().insertAll(params);

            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
           tasks.remove(this);
        }
    }

    @Override
    public void onFailure(Throwable t) {
        t.printStackTrace();
        CmdMsg msg = new CmdMsg();
        msg.status = 0;
        msg.msg = "连接服务器失败";
        RxBus.getInstance().post(msg);
    }


    @Override
    public void onOpen() {
        CmdMsg msg = new CmdMsg();
        msg.status = 0;
        msg.msg = "通讯成功";
        System.out.println("通讯成功");
        RxBus.getInstance().post(msg);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        LogUtil.e("关闭码：" + code + "关闭原因：" + reason);
    }

    public class ibinder extends Binder {
        /****
         * 发送命令
         *
         * @param order 控制命令内容，从控制子地址开始，到校验码之前
         * @return 发送成功返回true，失败返回false
         */
        public boolean sendOrder(String order) {
            return true;
        }
    }

    @Override
    public void onDestroy() {
        LogUtil.i("service is onDestroy!");

        if (webSocketWrapper != null)
            webSocketWrapper.closeWebSocket();
        if (myNetReceiver != null) {
            unregisterReceiver(myNetReceiver);   //注销接收网络变化的广播通知的广播接收器
        }

        tasks.clear();
        tasks=null;
        super.onDestroy();
    }

    private ConnectivityManager mConnectivityManager;
    private NetworkInfo netInfo;
    /////////////监听网络状态变化的广播接收器
    private BroadcastReceiver myNetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                netInfo = mConnectivityManager.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isAvailable()) {
                    webSocketWrapper.connect();
                    String name = netInfo.getTypeName();
                    LogUtil.i(name);
                } else {

                }
            }

        }
    };

}
