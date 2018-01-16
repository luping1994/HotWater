package net.suntrans.hotwater_demon.widgets;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


/**
 * Created by Looney on 2017/11/24.
 * Des:
 */

public class FullScreenDialog extends AppCompatDialog {
    public FullScreenDialog(Context context) {
        this(context, 0);
    }

    public FullScreenDialog(Context context, int theme) {
        super(context, theme);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    protected FullScreenDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            WindowManager m = window.getWindowManager();
            Display d = m.getDefaultDisplay();  //为获取屏幕宽、高
            //设置dialog的宽度未matchparent,高度为去掉状态栏和actionbar的高度
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) (d.getHeight()*0.9));

           //设置gravity
            window.setGravity(Gravity.CENTER);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE;
            getWindow().setAttributes(params);
        }
    }

}
