package com.example.devansh.sampleorai;

import android.content.Context;
import android.graphics.LinearGradient;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by devansh on 23/11/17.
 */

public class DragTouchListner implements View.OnTouchListener {

    public interface ButtonOverlapListener{
        void onOverlap();
    }

    int lastAction;
    float dX;
    float dY;
    float setX , setY;

    boolean hasBtn = false;

    float a,b,c,d;

    public static int maxX , maxY, maxXcopy;

    float e,g;

    int previewWidth,previewHeigth;

    private ButtonOverlapListener buttonOverlapListener;


    public static void getDeviceCordinates(AppCompatActivity context){
        Display mdisp = context.getWindowManager().getDefaultDisplay();
        int maxX= mdisp.getWidth();
        int maxY= mdisp.getHeight();

        DragTouchListner.maxX = maxX;
        DragTouchListner.maxXcopy = maxX;
        DragTouchListner.maxY = maxY;
        Log.d("VIDEO_DRAG","maxes before trim "+maxX+" "+maxY);
    }

    public static void removeHeight(int width,int height){
        maxX = maxX - width;
       // Log.d("PREVIEW_DRAG","setting ymax "+maxY+" "+height);
        maxY = maxY - height;
        Log.d("VIDEO_DRAG","maxes after trim "+maxX+" "+maxY);
    }

    public DragTouchListner(ButtonOverlapListener b){
        buttonOverlapListener = b;
    }

    public void setPreviewDimen(int w,int h){
        previewWidth = w;
        previewHeigth = h;
    }

    public void setHasBtn(boolean b){
        hasBtn = b;
    }

    public void setBtnCor(float aa , float bb , float cc , float dd){
        a = aa;
        b = bb;
        c = cc;
        d = dd;
      //  Log.d("OVERLAP_BUG","new btn cor "+a+" "+b+" "+c+" "+d);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                lastAction = MotionEvent.ACTION_DOWN;
                break;

            case MotionEvent.ACTION_MOVE:
                if(YboundCheck(view.getY()))
                    view.setY(event.getRawY() + dY);
                if(XboundCheck(view.getX()))
                    view.setX(event.getRawX() + dX);
                lastAction = MotionEvent.ACTION_MOVE;
                if(hasBtn && isOverlaping(view.getX(),view.getY())){
                    if(buttonOverlapListener != null) {
                        buttonOverlapListener.onOverlap();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                if (lastAction == MotionEvent.ACTION_MOVE) {
                    if(!XboundCheck(view.getX()))
                        view.setX(setX);
                    if(!YboundCheck(view.getY()))
                        view.setY(setY);
                }
                break;

            default:
                return false;
        }
        return true;
    }


    private boolean XboundCheck(float x){
        Log.d("VIDEO_DRAG","xbound "+x+" "+maxX);
        if(x < 0) {
            setX = 0f;
            return false;
        }
        else if(x > maxX){
            setX = maxX;
            return false;
        }
        else
            return true;
    }

    private boolean YboundCheck(float y){
        //Log.d("VIDEO_DRAG","ybound "+y+" "+maxY);
        if(y < 0) {
            setY = 0f;
            return false;
        }
        else if(y > maxY){
            setY = maxY;
            return false;
        }
        else
            return true;
    }

    private boolean isOverlaping(float f,float h){
        e = f + previewWidth;
        g = h + previewHeigth;
        if(b < f && f < a && d < g && g < c) {
          //  Log.d("OVERLAP_BUG","case 1 "+b+" "+f+" "+a+" "+d+" "+g+" "+c);
            return true;
        }
        else if(b < f && f < a && d > h && g > c) {
          //  Log.d("OVERLAP_BUG","case 2");
            return true;
        }
        else if(h < d && d < g && b >f && a < e) {
           // Log.d("OVERLAP_BUG","case 3");
            return true;
        }
        else if(b < e && e < a && d < g && g < c) {
          //  Log.d("OVERLAP_BUG","case 4 "+b+" "+e+" "+a+" "+d+" "+g+" "+c);
            return true;
        }
        else if(b < e && e < a && d > h && g > c) {
          //  Log.d("OVERLAP_BUG","case 5");
            return true;
        }
        else if (d < h && h < c && e > a && f < b) {
          //  Log.d("OVERLAP_BUG","case 6");
            return true;
        }
      //  Log.d("OVERLAP_BUG","case 7");
        return false;

    }

}
