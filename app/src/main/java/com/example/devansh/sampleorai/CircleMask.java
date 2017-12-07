package com.example.devansh.sampleorai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by devansh on 24/11/17.
 */

public class CircleMask extends View {

    private Paint mCutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap mBitmap;
    private Canvas mInternalCanvas;

    private String maskColor = "#242424";

    public CircleMask(Context context) {
        super(context);
        init();
    }

    public CircleMask(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleMask(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mCutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mInternalCanvas != null) {
            mInternalCanvas.setBitmap(null);
            mInternalCanvas = null;
        }

        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mInternalCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mInternalCanvas == null || mBitmap == null) {
            return;
        }

        final int width = getWidth();
        final int height = getHeight();

        // make the radius as large as possible within the view bounds
        final int radius = Math.min(width, height) / 2;

        mInternalCanvas.drawColor(Color.parseColor(maskColor));
        mInternalCanvas.drawCircle(width / 2, height / 2, radius, mCutPaint);

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    public void setMaskColor(String color){
        if(color != null && !color.isEmpty())
            maskColor = color;
    }

}
