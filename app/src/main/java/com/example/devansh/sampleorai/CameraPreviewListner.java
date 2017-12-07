package com.example.devansh.sampleorai;

import android.hardware.Camera;
import android.util.Log;

/**
 * Created by devansh on 24/11/17.
 */

public class CameraPreviewListner implements Camera.PreviewCallback {

    private boolean shouldDetect = true;
    private static DefaultExecutorSupplier defaultExecutorSupplier = DefaultExecutorSupplier.getInstance();
    private static DecodeByteToBitmap decodeByteToBitmap = new DecodeByteToBitmap();

    public void setShouldDetect(boolean b){
        shouldDetect = b;
    }

    public void onPreviewFrame(byte[] data, Camera camera)
    {
       // Log.d("SMILE_DETECT","onpreview frame "+shouldDetect);
        if(shouldDetect && data != null && data.length > 0) {
            shouldDetect = false;
            decodeByteToBitmap.setData(data);
         //   Log.d("DETECT_BUG","   ");
          //  Log.d("DETECT_BUG","launching thread to detect smile "+System.currentTimeMillis());
            defaultExecutorSupplier.forBackgroundTasks().execute(decodeByteToBitmap);
//        try
//        {
//            BitmapFactory.Options opts = new BitmapFactory.Options();
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//,opts);
//        }
//        catch(Exception e)
//        {
//
//        }
        }
    }

}
