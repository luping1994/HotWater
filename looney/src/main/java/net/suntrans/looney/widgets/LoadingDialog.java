package net.suntrans.looney.widgets;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import net.suntrans.looney.R;


public class LoadingDialog extends Dialog {

    private Context mContext;

    private TextView mWaitTv = null;

    private String mWaitingTxt = null;

    public LoadingDialog(Context context) {
        this(context, R.style.loading_dialog);
    }


    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        mWaitTv = (TextView) findViewById(R.id.msg);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        getWindow().setGravity(Gravity.TOP);
//        params.x = UiUtils.dip2px(16);
        params.y = dip2px(60, mContext);
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        params.width = display.getWidth() * 5 / 6;
        params.height = dip2px(40, mContext);
        getWindow().setAttributes(params);
        if (mWaitingTxt != null && !mWaitingTxt.isEmpty()) {
            mWaitTv.setVisibility(View.VISIBLE);
            mWaitTv.setText(mWaitingTxt);
        } else {
            mWaitTv.setVisibility(View.GONE);
        }
    }

    public void setWaitText(String text) {
        mWaitingTxt = text;
        if (mWaitTv == null) {
            return;
        }
        if (mWaitingTxt != null && !mWaitingTxt.isEmpty()) {
            mWaitTv.setVisibility(View.VISIBLE);
            mWaitTv.setText(text);
        } else {
            mWaitTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void show() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            try {
                super.show();
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void dismiss() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            try {
                super.dismiss();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void cancel() {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            try {
                super.cancel();
            } catch (Exception e) {
            }
        }
    }


    /**
     * dip转换px
     */
    public int dip2px(float dip, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */

    public int px2dip(int px, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
