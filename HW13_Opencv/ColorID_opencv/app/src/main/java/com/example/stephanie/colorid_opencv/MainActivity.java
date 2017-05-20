package com.example.stephanie.colorid_opencv;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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
    private Scalar normRGB;
    private Scalar normHSV;

    TextView touch_coordinates; // Creating variables which will let us modify display in layout
    TextView touch_color;

    double x = -1; // Coordinates of touch location
    double y = -1;

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

        touch_coordinates = (TextView) findViewById(R.id.touch_coordinates);
        touch_color = (TextView) findViewById(R.id.touch_color);

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

    //Scaling only works for portrait mode, which sucks :(

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int cols = colors.cols();
        int rows = colors.rows();

        // Only need y limits because feed spans across entire width of the screen in portrait mode
        double yMin = (double)cameraView.getHeight()*0.2401961;
        double yMax = (double)cameraView.getHeight()*0.7696078;

        double xScale = (double) cols/ (double) cameraView.getWidth();
        double yScale = (double) rows/ (yMax - yMin);

        x = event.getX();
        y = event.getY();
        y = y - yMin;

        x = x*xScale;
        y = y*yScale;

        if((x<0) || (y<0) || (x>cols) || (y>rows)) return false;

        touch_coordinates.setText("X: " + Double.valueOf(x) + ", Y: " + Double.valueOf(y));

        Rect touch_spot = new Rect();

        touch_spot.x = (int) x;
        touch_spot.y = (int) y;

        // Size of region sampled
        touch_spot.width = 8;
        touch_spot.height = 8;

        // Converting rgba values to hsv of rectangle
        Mat rect_color = colors.submat(touch_spot);
        Mat rect_hsv = new Mat();
        Imgproc.cvtColor(rect_color, rect_hsv, Imgproc.COLOR_RGB2HSV_FULL);

        // Averaging HSV values
        normHSV = Core.sumElems(rect_hsv);
        int pointCount = touch_spot.width * touch_spot.height;
        for (int i = 0; i<normHSV.val.length; i++)
            normHSV.val[i] /= pointCount;

        normRGB = convertScalarHsv2Rgba(normHSV);

        // Setting text to display color values
        touch_color.setText("Color Detected: #" + String.format("%02X", (int)normRGB.val[0])
                + String.format("%02X", (int)normRGB.val[1])
                + String.format("%02X", (int)normRGB.val[2]));

        // Changing text color to match color values
        touch_coordinates.setTextColor(Color.rgb((int)normRGB.val[0], (int)normRGB.val[1], (int)normRGB.val[2]));
        touch_color.setTextColor(Color.rgb((int)normRGB.val[0], (int)normRGB.val[1], (int)normRGB.val[2]));

        return false;
    }

    private Scalar convertScalarHsv2Rgba(Scalar hsvColor){ // Passing in 1x1 mx
        Mat pointMatRgba = new Mat();
        Mat pointMatHsv = new Mat(1, 1, CvType.CV_8UC3, hsvColor);
        Imgproc.cvtColor(pointMatHsv, pointMatRgba, Imgproc.COLOR_HSV2RGB_FULL, 4);

        return new Scalar(pointMatRgba.get(0,0));
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        colors = new Mat(); // Save new matrix to colors private class variable
        normRGB = new Scalar(255);
        normHSV = new Scalar(255);
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
