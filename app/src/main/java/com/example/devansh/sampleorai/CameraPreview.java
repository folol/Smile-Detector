package com.example.devansh.sampleorai;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by devansh on 22/11/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    public interface PreviewExceptionListener{
        void onException();
        void onNoExeption();
    }

//    public static float radius , xCor , yCor;
//
//    private Path clipPath;
//    private Paint paint;
    private PreviewExceptionListener previewExceptionListener;
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera ,PreviewExceptionListener p) {
        super(context);
        mCamera = camera;
        previewExceptionListener = p;
       // init();
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

//    private void init() {
////        clipPath = new Path();
////        //TODO: define the circle you actually want
////        clipPath.addCircle(710, 330, 250, Path.Direction.CW);
////        setZOrderMediaOverlay(false);
////        Log.d("CIRCLE_DIMEN",xCor+" "+yCor+" "+radius);
//        clipPath = new Path();
//        clipPath.addCircle(xCor,yCor,radius, Path.Direction.CW);
//        paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        this.setZOrderOnTop(true);
//    }

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        canvas.clipPath(clipPath);
//        canvas.drawPath(clipPath, paint);
//        Log.d("CIRCLE_DIMEN","dispatch draw "+xCor+" "+yCor+" "+radius);
//        super.dispatchDraw(canvas);
//    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            Log.d("CIRCLE_DIMEN","start preview 1");
//            if(previewExceptionListener != null)
//                previewExceptionListener.onNoExeption();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            if(previewExceptionListener != null)
                previewExceptionListener.onException();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            Log.d("CIRCLE_DIMEN","start preview 2");
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            if(previewExceptionListener != null)
                previewExceptionListener.onException();
        }
    }


}
