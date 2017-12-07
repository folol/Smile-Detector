package com.example.devansh.sampleorai;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.glomadrian.grav.GravView;

import java.util.ArrayList;


public class RecordActivity extends AppCompatActivity implements DialogBuilder.DialogListener ,
        CameraPreview.PreviewExceptionListener , DragTouchListner.ButtonOverlapListener
        ,Handler.Callback{

    private final int CAMERA_PERM = 1;
    private int FRAME_DELAY = 0;
    private int cameraIndex = -1;
    private Camera mCamera;
    private CameraPreview cameraPreview;
    private VideoMediaRecorder videoMediaRecorder;
    CameraOpenAsync cameraOpenAsync;

    Button stopBtn;
    TextView smileStatus;
    FrameLayout preview;
    GravView gravView;

    DragTouchListner dragTouchListner;
   // int animationDistance;
    int btnWidth,btnHeight;
   // Animation animation;
    int fiftyDpInPx;
    boolean isLeft = true;

    private static Handler handler;

    CameraPreviewListner cameraPreviewListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        handler = new Handler(this);
        DecodeByteToBitmap.handler = handler;
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        stopBtn = (Button) findViewById(R.id.ar_stop);
        smileStatus = (TextView) findViewById(R.id.ar_smile_status);
        gravView = (GravView) findViewById(R.id.ar_grav);
        stopBtn.setEnabled(false);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        cameraPreviewListner = new CameraPreviewListner();
        DragTouchListner.getDeviceCordinates(this);
        dragTouchListner = new DragTouchListner(this);
        getBtnCord();
        getPreviewCord();
        if(checkCameraHardware(this))
            checkCameraPermission();
        else{
            //no camera detected
            finishWithMsg(getString(R.string.no_camera_msg));
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        gravView.stop();
    }

    @Override
    public boolean handleMessage(Message msg) {
        Bundle bundle = msg.getData();
        if(bundle != null) {
            String message = bundle.getString("msg");
            // Log.d("SMILE_DETECT", "smile probability in activity "+message);
            if(message != null) {
                if (message.equals("smile")) {
                    float smileProb = bundle.getFloat("smile");
                    Log.d("SMILE_DETECT", "probablity found " + smileProb);
                    smileStatus.setText(doCals(smileProb));
                } else if (message.equals("error")) {
                    String err = bundle.getString("error");
                    //   Log.d("SMILE_DETECT", "smile probability in activity err "+err);
                    smileStatus.setText(err);
                }
            }
        }
        enableSmileDetector();
        return true;
    }

    private String doCals(float prob){

        if(prob < 0.25f){
            return "Not smiling";
        }
        else if(prob >= 0.25f && prob <= 0.8f){
            return "I see a smile";
        }
        else if(prob > 0.8f)
            return "I see a LOL";

        return "I do not see";
    }

    private void enableSmileDetector(){
//        if(FRAME_DELAY == 0){
//            if(DecodeByteToBitmap.PROCESS_TIME != -1)
//                FRAME_DELAY = (int) DecodeByteToBitmap.PROCESS_TIME;
//           // Log.d("SMILE_DETECT", "frame delay "+FRAME_DELAY);
//        }
        if(handler != null){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                  if(cameraPreviewListner  != null)
                      cameraPreviewListner.setShouldDetect(true);
                }
            },FRAME_DELAY);
        }
    }

    private void getPreviewCord(){
        ViewTreeObserver vto = preview.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = preview.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                //  float centreX=preview.getX() + preview.getWidth()  / 2;
                // float centreY=preview.getY() + preview.getHeight() / 2;
                DragTouchListner.removeHeight(preview.getWidth(),preview.getHeight());
                dragTouchListner.setPreviewDimen(preview.getWidth(),preview.getHeight());
//                CameraPreview.xCor = centreX;
//                CameraPreview.yCor = centreY;
                //FocusView.xCor = centreX;
                //FocusView.yCor = centreY;
                Resources r = getResources();
                // float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,50,r.getDisplayMetrics());
                fiftyDpInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,30,r.getDisplayMetrics());
                //  CameraPreview.radius = px;
                //FocusView.radius = px;
            }
        });
    }

    private void getBtnCord(){
        ViewTreeObserver vtoTwo = stopBtn.getViewTreeObserver();
        vtoTwo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = stopBtn.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
                btnWidth = stopBtn.getWidth();
                btnHeight = stopBtn.getHeight();
                float b = stopBtn.getX();
                float a = b + stopBtn.getWidth();
                float d = stopBtn.getY();
                float c = d + stopBtn.getHeight();
                dragTouchListner.setBtnCor(a,b,c,d);
                dragTouchListner.setHasBtn(true);
            }
        });
    }

    private void finishWithMsg(String msg){
        Intent i = new Intent();
        i.putExtra("msg",msg);
        setResult(Activity.RESULT_OK,i);
        finish();
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    private void checkCameraPermission(){
        ArrayList<String> perms = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            perms.add(Manifest.permission.CAMERA);
        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED){
            perms.add(Manifest.permission.RECORD_AUDIO);
        }

        if(perms.size() > 0) {
            ActivityCompat.requestPermissions(this,
                    perms.toArray(new String[perms.size()]),
                    CAMERA_PERM);
        }
        else if(perms.size() == 0) {
            isFrontCameraExist();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode == CAMERA_PERM && grantResults.length > 0) {
            boolean isAllAlowed = true;
            for (int isGranted : grantResults) {
                if (isGranted != PackageManager.PERMISSION_GRANTED) {
                    isAllAlowed = false;
                    break;
                }
            }
            if (isAllAlowed)
                isFrontCameraExist();
            else {
                //permission is not granted
                DialogBuilder.buildConfirmDialogueWithMsg(this, getString(R.string.deny_perm_title), getString(R.string.deny_perm_msg),
                        getString(R.string.allow), getString(R.string.cancel), false, this);
            }
        }
    }

    @Override
    public void optionSelected(String option) {
        if ("YES".equalsIgnoreCase(option)) {
            checkCameraPermission();
        } else {
            finish();
        }
    }

    private boolean checkFrontCamera(){
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraIndex = camIdx;
                return true;
            }
        }
        return false;
    }

    private void isFrontCameraExist(){
        if(checkFrontCamera())
            proceed();
        else
            finishWithMsg(getString(R.string.no_camera_msg));
    }

    private void proceed(){
        if(cameraOpenAsync == null && cameraIndex != -1)
            cameraOpenAsync = new CameraOpenAsync(mCamera,cameraIndex,this);
        if(cameraOpenAsync != null)
            cameraOpenAsync.execute(null,null,null);
    }

    public void setmCamera(Camera camera){
        if(camera != null) {
            mCamera = camera;
            setUpCameraParameters();
            DecodeByteToBitmap.W = mCamera.getParameters().getPreviewSize().width;
            DecodeByteToBitmap.H = mCamera.getParameters().getPreviewSize().height;
          //  mCamera.getParameters().setPreviewFormat(ImageFormat.RGB_565);
          //  Log.d("SMILE_DETECT","image format "+mCamera.getParameters().getPreviewFormat()
            //        +"\n"+mCamera.getParameters().getPreviewSize().width+" "+mCamera.getParameters().getPreviewSize().height);
            //Log.d("CIRCLE_DIMEN","radius "+px);
            cameraPreview = new CameraPreview(this, mCamera,this);
            preview.addView(cameraPreview);

            //FrameLayout.LayoutParams lps = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            CircleMask circleMask = new CircleMask(this);
            preview.addView(circleMask);
            VideoMediaRecorder.setCameraDisplayOrientation(this,cameraIndex,mCamera);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    onNoExeption();
                }
            },1000);
        }
        else
            finishWithMsg(getString(R.string.camera_open_err));
    }

    public void unlockCamera(){
        if(mCamera != null)
            mCamera.unlock();
    }

    private void setUpCameraParameters(){
        if(mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            if (params != null) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                mCamera.setParameters(params);
            }
        }
    }

    @Override
    public void onNoExeption(){
        unlockCamera();
        videoMediaRecorder = new VideoMediaRecorder(mCamera);
        if(videoMediaRecorder.setUpMediaProperties(cameraPreview)) {
            preview.setOnTouchListener(dragTouchListner);
            videoMediaRecorder.startRecording();
            stopBtn.setEnabled(true);
            DecodeByteToBitmap.initializeDetector(this);
            mCamera.setPreviewCallback(cameraPreviewListner);
        }
    }

    @Override
    public void onException(){
        Toast.makeText(this,getString(R.string.camera_preview_err),Toast.LENGTH_SHORT).show();
    }


    private void stopRecording(){
        if(videoMediaRecorder != null) {
            videoMediaRecorder.stopRecording();
            cameraPreviewListner = null;
            DecodeByteToBitmap.releaseDetector();
            //launch another activity
            String path = videoMediaRecorder.getPath();
            if(path != null && !path.isEmpty()) {
                Intent i = new Intent(this, VideoPreviewActivity.class);
                i.putExtra("path", path);
                startActivity(i);
                finish();
            }
            else
                finishWithMsg(getString(R.string.camera_preview_err));
        }
    }

    @Override
    public void onOverlap(){
        //Log.d("PREVIEW_OVERLAP","button overaped");
        //Toast.makeText(this,"Button Overlaped",Toast.LENGTH_SHORT).show();
        dragTouchListner.setHasBtn(false);
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        if(isLeft) {
            lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            lps.setMargins(0,0, fiftyDpInPx, fiftyDpInPx);
            isLeft = false;
        }
        else {
            lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lps.setMargins(fiftyDpInPx,0,0, fiftyDpInPx);
            isLeft = true;
        }
        stopBtn.setLayoutParams(lps); //causes layout update
        stopBtn.requestLayout();
        getBtnCord();
//        dragTouchListner.setHasBtn(true);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

}
