package com.example.devansh.sampleorai;

import android.content.res.Resources;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.github.glomadrian.grav.GravView;

public class VideoPreviewActivity extends AppCompatActivity implements DragTouchListner.ButtonOverlapListener {

    MyVideoView videoView;
    Button ppBtn;
    GravView gravView;

    DragTouchListner dragTouchListner;

    int fiftyDpInPx;

    boolean isLeft = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_preview);

        String path = getIntent().getStringExtra("path");

        if(path != null && !path.isEmpty()){
            videoView = (MyVideoView) findViewById(R.id.videoPlayer);
            gravView = (GravView) findViewById(R.id.ar_grav);
            //CircleMask circleMask = (CircleMask) findViewById(R.id.avp_mask);
            Resources r = getResources();
            int hunderdpx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,r.getDisplayMetrics());
            videoView.setVideoSize(hunderdpx,hunderdpx);
            ppBtn = (Button) findViewById(R.id.avp_stop);
            RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.avr_video_cotnainer);
            videoView.setVideoPath(path);

            ppBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toogleVideo();
                }
            });
            ppBtn.setText(R.string.pause);

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    ppBtn.setText(R.string.play);
                }
            });
            dragTouchListner = new DragTouchListner(this);
            getVideoViewCor(videoContainer);
            getBtnCord();
            videoContainer.setOnTouchListener(dragTouchListner);
            videoView.start();
        }
        else{
            Toast.makeText(this,getString(R.string.camera_preview_err),Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        gravView.stop();
    }

    private void getVideoViewCor(final RelativeLayout preview){
        DragTouchListner.getDeviceCordinates(this);
        ViewTreeObserver vto = preview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = preview.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                Log.d("VIDEO_DRAG","video size "+preview.getWidth()+" "+preview.getHeight());
                DragTouchListner.removeHeight(preview.getWidth(),preview.getHeight());
                dragTouchListner.setPreviewDimen(preview.getWidth(),preview.getHeight());
                Resources r = getResources();
                fiftyDpInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,r.getDisplayMetrics());
            }
        });
    }


    private void getBtnCord(){
        ViewTreeObserver vtoTwo = ppBtn.getViewTreeObserver();
        vtoTwo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = ppBtn.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
//                btnWidth = ppBtn.getWidth();
//                btnHeight = ppBtn.getHeight();
                float b = ppBtn.getX();
                float a = b + ppBtn.getWidth();
                float d = ppBtn.getY();
                float c = d + ppBtn.getHeight();
                dragTouchListner.setBtnCor(a,b,c,d);
                dragTouchListner.setHasBtn(true);
            }
        });
    }


    private void toogleVideo(){
        if(videoView != null){
            if(videoView.isPlaying()){
                videoView.pause();
                ppBtn.setText(R.string.play);
            }
            else{
                videoView.start();
                ppBtn.setText(R.string.pause);
            }
        }
    }

    @Override
    public void onOverlap(){
        dragTouchListner.setHasBtn(false);
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if(isLeft) {
            lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lps.setMargins(0,fiftyDpInPx,0,0);
            isLeft = false;
        }
        else {
            lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            lps.addRule(RelativeLayout.CENTER_HORIZONTAL);
            lps.setMargins(0,0,0, fiftyDpInPx);
            isLeft = true;
        }
        ppBtn.setLayoutParams(lps); //causes layout update
        ppBtn.requestLayout();
        getBtnCord();
    }
}
