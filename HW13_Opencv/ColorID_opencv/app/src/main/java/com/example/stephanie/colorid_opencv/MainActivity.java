package com.example.stephanie.colorid_opencv;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, CameraBridgeViewBase.CvCameraViewListener2{

/*
private static final String TAG = "MainActivity";

static {
if(!OpenCVLoader.initDebug()){
Log.d(TAG, "OpenCV is not loaded");
}
else{
Log.d(TAG, "OpenCV loaded");
}
}
*/

    private CameraBridgeViewBase cameraView;
    private Mat colors; // In rgba
    private BaseLoaderCallback loader_cb = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status){
                case LoaderCallbackInterface.SUCCESS: {
                    cameraView.enableView();
                    cameraView.setOnTouchListener(MainActivity.this);
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //When app is on, screen stays open
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Take camera view, process with opencv library, and output result to screen
        cameraView = (CameraBridgeViewBase) findViewById(R.id.colorid_opencv_surface_view);
        cameraView.setVisibility(SurfaceView.VISIBLE);
        cameraView.setCvCameraViewListener(this); //this is JAVA keyword for current object

    }

    @Override
    public void onPause(){
        super.onPause();
        if(cameraView != null)
            cameraView.disableView();   //Lets other apps use camera
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!OpenCVLoader.initDebug()){
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, loader_cb);
        }
        else{
            loader_cb.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(cameraView !=  null) {
            cameraView.disableView();
        }
    }

    //***************
    //App Functions
    //***************
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        colors = new Mat(); // Save new matrix to colors private class variable
    }

    @Override
    public void onCameraViewStopped() {
        colors.release();   // Empty variable
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        colors = inputFrame.rgba();     // takes a frame from camera and returns corresponding rgba matrix
        return colors;
    }
}
