package com.hrithikvish.cbsm.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class RoundedCornerLayout extends FrameLayout {

    private float radiusPercent = 0.5f;

    public RoundedCornerLayout(Context context) {
        super(context);
        init();
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedCornerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Path path = new Path();
        float radius = getWidth() * radiusPercent;
        RectF rect = new RectF(0, 0, getWidth(), getHeight());
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path, Region.Op.REPLACE);
        super.onDraw(canvas);
    }

    public void setRadiusPercent(float percent) {
        if (percent < 0 || percent > 1) {
            throw new IllegalArgumentException("Radius percentage should be between 0 and 1");
        }
        this.radiusPercent = percent;
        invalidate();
    }
}
