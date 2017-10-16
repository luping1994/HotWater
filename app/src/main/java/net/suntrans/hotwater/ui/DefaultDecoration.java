package net.suntrans.hotwater.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import net.suntrans.looney.utils.UiUtils;

/**
 * Created by Looney on 2017/9/4.
 */

public class DefaultDecoration extends RecyclerView.ItemDecoration {

    private int offsets = UiUtils.dip2px(1);
    private Paint mPaint;
    private int mOffsetsColor = Color.parseColor("#c6e2ff");
    private final Rect mBounds = new Rect();

    public DefaultDecoration(int offsets, int mOffsetsColor) {
        this.offsets = offsets;
        this.mOffsetsColor = mOffsetsColor;
        init();
    }

    public DefaultDecoration() {
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(mOffsetsColor);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = offsets;
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        canvas.save();
        int childCount = parent.getChildCount();
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            float top = view.getBottom();
            float bottom = view.getBottom() + offsets;
            canvas.drawRect(left, top, right, bottom, mPaint);
        }
        canvas.restore();

    }
}
