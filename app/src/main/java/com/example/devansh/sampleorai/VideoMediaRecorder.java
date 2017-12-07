package com.example.devansh.sampleorai;

import android.app.Activity;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by devansh on 23/11/17.
 */

public class VideoMediaRecorder {

    private MediaRecorder mVideoRecorder;
    private Camera mCamera;

    String path;

    public VideoMediaRecorder(Camera camera){
        mCamera = camera;
        mVideoRecorder = new MediaRecorder();
        mVideoRecorder.setCamera(mCamera);
    }

    public String getPath(){
        return path;
    }


    public static void setCameraDisplayOrientation(AppCompatActivity activity,
                                                   int cameraId,Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result = (info.orientation + degrees) % 360;
        result = (360 - result) % 360;  // compensate the mirror
      //  Log.d("RECORD_PREFS","orientation "+result);
        camera.setDisplayOrientation(result);
    }


    public boolean setUpMediaProperties(CameraPreview cameraPreview){
        if(mVideoRecorder != null && mCamera != null){
         //   CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            mVideoRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mVideoRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
         //   mVideoRecorder.setVideoSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
            mVideoRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mVideoRecorder.setVideoSize(640, 480);
            mVideoRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            mVideoRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        //    mVideoRecorder.setProfile(camcorderProfile);
            mVideoRecorder.setOutputFile(getOutputMediaFile());
            mVideoRecorder.setPreviewDisplay(cameraPreview.getHolder().getSurface());
            mVideoRecorder.setOrientationHint(270);
            try {
                mVideoRecorder.prepare();
                return true;
            } catch (IllegalStateException e) {
                resetNreleaseMediaRecorder();
            } catch (IOException e) {
                resetNreleaseMediaRecorder();
            }
        }
        return false;
    }

    private String getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"SampleORAI");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
              //  Log.d("SampleORAI", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        //Log.d("RECORD_PREFS","path  "+mediaFile.getAbsolutePath());
        path = mediaFile.getAbsolutePath();
        return path;
    }

    public void startRecording(){
        if(mVideoRecorder != null)
            mVideoRecorder.start();
    }

    public void stopRecording(){
        if(mVideoRecorder != null){
            mVideoRecorder.stop();
            resetNreleaseMediaRecorder();
        }
        if(mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.lock();
            mCamera.stopPreview();
            mCamera.release();
        }
    }

    private void resetNreleaseMediaRecorder(){
        if (mVideoRecorder != null) {
            mVideoRecorder.reset();   // clear recorder configuration
            mVideoRecorder.release(); // release the recorder object
        }
    }

//    private void releaseCamera(){
//        if (mCamera != null){
//            mCamera.release();        // release the camera for other applications
//        }
//    }

}
