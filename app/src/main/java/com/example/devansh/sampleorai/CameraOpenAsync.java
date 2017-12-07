package com.example.devansh.sampleorai;

import android.hardware.Camera;
import android.os.AsyncTask;

/**
 * Created by devansh on 22/11/17.
 */

public class CameraOpenAsync extends AsyncTask<Void, Void, Camera> {

    Camera mainCamera;
    int cameraId;
    RecordActivity mInstance;

    public CameraOpenAsync(Camera mC,int id,RecordActivity ra){
        mainCamera = mC;
        cameraId = id;
        mInstance = ra;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected Camera doInBackground(Void... params) {
        return safeCameraOpen(); //This function will open camera; '0' => back camera
    }

    private Camera safeCameraOpen() {

        Camera camera;

        try {
            releaseCameraAndPreview();
            camera = Camera.open(cameraId);
            if (camera != null) {
                return camera;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void releaseCameraAndPreview() {
        if (mainCamera != null) {
            mainCamera.release();
            mainCamera = null;
        }
    }

    @Override
    protected void onPostExecute(Camera camera) {
        if(mInstance != null)
            mInstance.setmCamera(camera);
    }


}
