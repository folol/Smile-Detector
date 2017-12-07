package com.example.devansh.sampleorai;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by devansh on 24/11/17.
 */

public class DecodeByteToBitmap implements Runnable {

    public static int W , H;
 //   public static long PROCESS_TIME = -1;
    private static Matrix matrix = new Matrix();
   // private static final Rect rect = new Rect(0, 0, W, H);
    public static Handler handler;
    private byte[] data;
    private Bitmap bitmap;
    private ByteArrayOutputStream os = new ByteArrayOutputStream();
  //  private File photoFile;
    private byte[] jpegByteArray;
    YuvImage yuvImage;

    private static FaceDetector detector;
    private Frame frame;
    private SparseArray<Face> mFaces;
    private Face face;

    private static boolean isReleased = false;

   // private static Context mcontext;


    public static void initializeDetector(Context context){
        matrix.postRotate(270);
     //   mcontext = context;
        detector = new FaceDetector.Builder( context )
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        isReleased = false;
    }

    public static void releaseDetector(){
        if(detector != null) {
            detector.release();
            detector = null;
            isReleased = true;
            DefaultExecutorSupplier.getInstance().forBackgroundTasks().shutdownNow();
           // Log.d("DETECT_BUG","is realsed "+isReleased);
        }
    }

    public void setData(byte[] d){
        data = null;
        data = d;
    }

    @Override
    public void run(){
        try {
            Thread.sleep(1);
            //long startTime = System.currentTimeMillis();
          //  Log.d("DETECT_BUG", "thread run to detect smile " + System.currentTimeMillis());
            if (bitmap != null)
                bitmap.recycle();

            yuvImage = null;
            yuvImage = new YuvImage(data, ImageFormat.NV21, W, H, null);
            os.reset();
            yuvImage.compressToJpeg(new Rect(0, 0, W, H), 100, os);
            jpegByteArray = os.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);

            if (bitmap != null && detector != null) {
                rotateBitmap();
                if (!detector.isOperational()) {
                    //Handle contingency
                    //    Log.d("SMILE_DETECT","detector not ready");
                    sendMsg("");
                } else {
                    frame = new Frame.Builder().setBitmap(bitmap).build();
                   // Log.d("DETECT_BUG", "detecting face in thread " + System.currentTimeMillis());
                    mFaces = detector.detect(frame);
                   // Log.d("DETECT_BUG", "face detected in thread " + System.currentTimeMillis());
                    if (mFaces != null && mFaces.size() > 0) {
                        face = mFaces.get(0);
                        //      Log.d("SMILE_DETECT", "smile probability "+face.getIsSmilingProbability());
                        Message msg = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putString("msg", "smile");
                        bundle.putFloat("smile", face.getIsSmilingProbability());
                        msg.setData(bundle);
                        if (handler != null) {
//                        if(PROCESS_TIME == -1) {
//                            long endTime = System.currentTimeMillis();
//                            long PROCESS_TIME = endTime - startTime;
                            //                           Log.d("SMILE_DETECT", "prcoess time "+PROCESS_TIME);
//                        }
                            handler.sendMessage(msg);
                        }
                    } else
                        sendMsg("No face detected");
                }
            } else {
                //   Log.d("SMILE_DETECT", "bitmap decoded it's null");
                sendMsg("");
            }
        }
        catch (Exception ex){
            Log.d("DETECT_BUG", "thread interuupted");
        }
    }

    private void sendMsg(String msgg){
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("msg","error");
        bundle.putString("error",msgg);
        msg.setData(bundle);
        if(handler != null)
            handler.sendMessage(msg);
    }

    private void rotateBitmap(){
        // bitmap = Bitmap.createScaledBitmap(bitmap,W,H,true);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,W,H, matrix, true);
    }

    /*
    private void readBitmap(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        bitmap = BitmapFactory.decodeFile(path, options);
        //selected_photo.setImageBitmap(bitmap);
    }

    private void flushStream(){
        try {
            os.flush();
        }
        catch (Exception e){

        }
    }

    public int[] decodeYUV420SP( byte[] yuv420sp, int width, int height) {

        final int frameSize = width * height;

        int rgb[]=new int[width*height];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) &
                        0xff00) | ((b >> 10) & 0xff);


            }
        }
        return rgb;
    }

    private void saveBitmapThree(int[] rgb){
        Bitmap bmp = Bitmap.createBitmap(rgb, W,H,Bitmap.Config.ARGB_8888);
        File photoFile;
        try {
            photoFile = createImageFile("BITMAPTHREE_");
            FileOutputStream out = new FileOutputStream(photoFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            out=null;
            Log.d("SMILE_DETECT", "array rgb saved "+photoFile.getAbsolutePath()+"\n"+data.length);
        } catch (Exception ex) {
            // Error occurred while creating the File
        }

    }

    private String saveImageYUV(){
        File photoFile;
        try {
            //if(photoFile == null)
                photoFile = createImageFile("YUV_");
            //else if(photoFile.exists())
              //  photoFile.delete();
            FileOutputStream fo = new FileOutputStream(photoFile);
            yuvImage.compressToJpeg(new Rect(0,0,W,H), 100, fo);
            fo.close();
            Log.d("SMILE_DETECT", "yuv saved "+photoFile.getAbsolutePath()+"\n"+data.length);
            return photoFile.getAbsolutePath();
        } catch (Exception ex) {
            // Error occurred while creating the File
            return null;
        }
    }

    private void saveImage(){
        File photoFile;
        try {
            photoFile = createImageFile("BITMAP_");
            FileOutputStream fo = new FileOutputStream(photoFile);
            //int nh = (int) ( bitmap.getHeight() * (512.0 / bitmap.getWidth()) );
            //bitmap = Bitmap.createScaledBitmap(bitmap, SIZE,SIZE, true);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fo);
            fo.close();
            Log.d("SMILE_DETECT", "bitmap saved "+photoFile.getAbsolutePath());
        } catch (Exception ex) {
            // Error occurred while creating the File
        }
    }

    private File createImageFile(String beg) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = beg + timeStamp + "_";
        File storageDir = mcontext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix
                ".jpg",         /* suffix
                storageDir      /* directory
        );
        return image;
    }

    */

}
