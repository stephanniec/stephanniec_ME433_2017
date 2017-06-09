package com.example.stephanie.linefollow;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.hardware.Camera;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Switch;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.CdcAcmSerialDriver;
import com.hoho.android.usbserial.driver.ProbeTable;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static android.graphics.Color.rgb;


public class MainActivity extends Activity implements TextureView.SurfaceTextureListener {

    //############
    //GLOBAL VAR
    //############

    // Motor Control Variables
    SeekBar myControlLeft;
    SeekBar myControlRight;
    TextView myTextView;
    TextView myTextView4;

    Button button;
    TextView myTextView2; // Grey text under button
    ScrollView myScrollView;
    TextView myTextView3; // Text in green box

    private UsbManager manager;
    private UsbSerialPort sPort;
    private final ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;

//    public int left_wheel_rot = 0;
//    public int right_wheel_rot = 0;

    // Road ID Variables
    private Camera mCamera;
    private TextureView mTextureView;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap bmp = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
    private Canvas canvas = new Canvas(bmp);
    private Paint paint1 = new Paint();
    private TextView mTextView;

    private SeekBar myControl;
    private SeekBar threshControl;

    private TextView sliderInstructions;
    private TextView sliderInstructions2;

    public static int sliderVal = 0;
    public static int sliderVal2 = 0;

    static long prevtime = 0; // for FPS calculation
    int l_vel = 64;
    int r_vel = 50;
    float Kp = 0; // proportional control gain
    public float COM = 0; // center of mass location
    public float COMbuff[] = new float[12];
    public float COMavg = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // keeps the screen from turning off

        // Road ID Instantiations
        mTextView = (TextView) findViewById(R.id.cameraStatus); // FPS status

        // see if the app has permission to use the camera
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);
            mSurfaceHolder = mSurfaceView.getHolder();

            mTextureView = (TextureView) findViewById(R.id.textureview);
            mTextureView.setSurfaceTextureListener(this);

            // set the paintbrush for writing text on the image
            paint1.setColor(0xffff0000); // red
            paint1.setTextSize(24);

            mTextView.setText("started camera");
        } else {
            mTextView.setText("no camera permissions");
        }

        myControl = (SeekBar) findViewById(R.id.seek3);
        sliderInstructions = (TextView) findViewById(R.id.textView05);
        sliderInstructions.setText("Move the slider to adjust the camera's sensitivity.");
        threshControl = (SeekBar) findViewById(R.id.seek4);
        sliderInstructions2 = (TextView) findViewById(R.id.textView06);
        sliderInstructions2.setText("Move the slider to set grey ID value.");

        setMyControlListener();
        setMyControlListener2();

        // Motor Control Instantiations
        myControlLeft = (SeekBar) findViewById(R.id.seek1);
        myControlRight = (SeekBar) findViewById(R.id.seek2);

        myTextView = (TextView) findViewById(R.id.textView01); // Left wheel slider value
        myTextView.setText("Left Wheel Value");
        myTextView4 = (TextView) findViewById(R.id.textView04); // Right wheel slider value
        myTextView4.setText("Right Wheel Value");

        myTextView2 = (TextView) findViewById(R.id.textView02); // Grey text under button
        myScrollView = (ScrollView) findViewById(R.id.ScrollView01);
        myTextView3 = (TextView) findViewById(R.id.textView03); // Text in green box
        button = (Button) findViewById(R.id.button1);
        Switch switch_button1 = (Switch) findViewById(R.id.switch_button1);
        Switch switch_button2 = (Switch) findViewById(R.id.switch_button2);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTextView2.setText("L and R PWM percent on click is "+myControlLeft.getProgress()+ " and " + myControlRight.getProgress());

                //String sendString = String.valueOf(myControlLeft.getProgress()) + " " + String.valueOf(myControlRight.getProgress()) + " " + String.valueOf(left_wheel_rot) + " " + String.valueOf(right_wheel_rot) + '\n';
//                try {
//                    sPort.write(sendString.getBytes(), 10); // 10 is the timeout
//                } catch (IOException e) { }
            }
        });

//        switch_button1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
//                if(isChecked){
//                    //Spin forward
//                    left_wheel_rot = 1;
//                }
//
//                else {
//                    //Spin backward
//                    left_wheel_rot = 0;
//                }
//            }
//
//        });
//
//        switch_button2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
//                if(isChecked){
//                    //Spin forward
//                    right_wheel_rot = 1;
//                }
//
//                else {
//                    //Spin backward
//                    right_wheel_rot = 0;
//                }
//            }
//
//        });

        setMyControlListenerLeft();
        setMyControlListenerRight();

        manager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(640, 480);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY); // no autofocusing
        parameters.setAutoExposureLock(false); // keep the white balance constant
        mCamera.setParameters(parameters);
        mCamera.setDisplayOrientation(90); // rotate to portrait mode

        try {
            mCamera.setPreviewTexture(surface);
            mCamera.startPreview();
        } catch (IOException ioe) {
            // Something bad happened
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored, Camera does all the work for us
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mCamera.stopPreview();
        mCamera.release();
        return true;
    }

    private void setMyControlListener() {
        myControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
                sliderInstructions.setText("The sensitivity value is: "+progress);
                sliderVal = (100-progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setMyControlListener2() {
        threshControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar2, int progress2, boolean fromUser) {
                progressChanged = progress2;
                sliderInstructions2.setText("The grey threshold value is: "+progress2);
                sliderVal2 = (100 - progress2);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar2){

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar2){

            }
        });
    }

    // the important function
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // every time there is a new Camera preview frame
        mTextureView.getBitmap(bmp);

        final Canvas c = mSurfaceHolder.lockCanvas();
        if (c != null) {
            int sensativity = sliderVal; // comparison value
            int greyThresh = sliderVal2;
            int[] pixels = new int[bmp.getWidth()]; // pixels[] is the RGBA data

            int buff_ind = 0;
            for (int startY = 360; startY < bmp.getHeight(); startY += 10) { // lines checked
                bmp.getPixels(pixels, 0, bmp.getWidth(), 0, startY, bmp.getWidth(), 1);

                int sum_mr = 0; // the sum of the mass times the radius
                int sum_m = 0; // the sum of the masses
                for (int i = 0; i < bmp.getWidth(); i++) {
                    if (((green(pixels[i]) - red(pixels[i])) > -greyThresh)&&((green(pixels[i]) - red(pixels[i])) < greyThresh)&&(green(pixels[i]) > sensativity)) {
                        pixels[i] = rgb(1, 1, 1); // set the pixel to almost 100% black

                        sum_m = sum_m + green(pixels[i])+red(pixels[i])+blue(pixels[i]);
                        sum_mr = sum_mr + (green(pixels[i])+red(pixels[i])+blue(pixels[i]))*i;
                    }
                }
                // only use the data if there were a few pixels identified, otherwise you might get a divide by 0 error
                if(sum_m>5){
                    COM = sum_mr / sum_m;
                    COMbuff[buff_ind] = COM; // store new COM value into buffer

                }
                else{
                    COM = 0;
                }

                // update the row
                bmp.setPixels(pixels, 0, bmp.getWidth(), 0, startY, bmp.getWidth(), 1);

                buff_ind++;
            }

            // Average COM positions
            COMavg = 0; // reset
            float tmp = 0;

            for(int j=0; j<COMbuff.length; j++){
                tmp += COMbuff[j];
            }
            COMavg = tmp/COMbuff.length;

            // only draw a circle at some position if pixes identified
            canvas.drawCircle(COMavg, 420, 5, paint1); // x position = COM of row, y position, diameter, color

           if (COMavg < 150){ // If dot drifts too far left, stop left wheel
                l_vel = 0;
            }
            else if (COMavg > 500) { // If dot drifts too far right, stop right wheel
                r_vel = 0;
            }
            else {
                // p control for velocity
                Kp = (float) ((COMavg - 320.0) / 1000.0); // Positive if too far right, neg if too far left

                // If too far right, ramp up l_vel and lower r_vel
                l_vel = (int) ((1 + Kp) * 84);
                r_vel = (int) ((1 - Kp) * 70);
            }

            // send motor velocity based on COM avg
            String sendString = String.valueOf(l_vel) + " " + String.valueOf(r_vel) + " " + String.valueOf(1) + " " + String.valueOf(1) + '\n';
            try {
                sPort.write(sendString.getBytes(), 10); // 10 is the timeout
            } catch (IOException e) { }

        }


//        // write the pos as text
        c.drawBitmap(bmp, 0, 0, null);
        mSurfaceHolder.unlockCanvasAndPost(c);

        // calculate the FPS to see how fast the code is running
        long nowtime = System.currentTimeMillis();
        long diff = nowtime - prevtime;
        mTextView.setText("FPS: " + 1000 / diff + " " + "Color Threshold: " + (100-sliderVal2));
        prevtime = nowtime;
    }

    //*******************Motor Control Functions*********************************
    private void setMyControlListenerLeft() {
        myControlLeft.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            int progressChangedL = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedL = progress;
                myTextView.setText("The value is: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setMyControlListenerRight() {
        myControlRight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            int progressChangedR = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedR = progress;
                myTextView4.setText("The value is: "+progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private final SerialInputOutputManager.Listener mListener =
            new SerialInputOutputManager.Listener() {
                @Override
                public void onRunError(Exception e) {

                }

                @Override
                public void onNewData(final byte[] data) {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.this.updateReceivedData(data);
                        }
                    });
                }
            };

    @Override
    protected void onPause(){
        super.onPause();
        stopIoManager();
        if(sPort != null){
            try{
                sPort.close();
            } catch (IOException e){ }
            sPort = null;
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ProbeTable customTable = new ProbeTable();
        customTable.addProduct(0x04D8,0x000A, CdcAcmSerialDriver.class);
        UsbSerialProber prober = new UsbSerialProber(customTable);

        final List<UsbSerialDriver> availableDrivers = prober.findAllDrivers(manager);

        if(availableDrivers.isEmpty()) {
            //check
            return;
        }

        UsbSerialDriver driver = availableDrivers.get(0);
        sPort = driver.getPorts().get(0);

        if (sPort == null){
            //check
        }else{
            final UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
            if (connection == null){
                //check
                PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent("com.android.example.USB_PERMISSION"), 0);
                usbManager.requestPermission(driver.getDevice(), pi);
                return;
            }

            try {
                sPort.open(connection);
                sPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

            }catch (IOException e) {
                //check
                try{
                    sPort.close();
                } catch (IOException e1) { }
                sPort = null;
                return;
            }
        }
        onDeviceStateChange();
    }

    private void stopIoManager(){
        if(mSerialIoManager != null) {
            mSerialIoManager.stop();
            mSerialIoManager = null;
        }
    }

    private void startIoManager() {
        if(sPort != null){
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            mExecutor.submit(mSerialIoManager);
        }
    }

    private void onDeviceStateChange(){
        stopIoManager();
        startIoManager();
    }

    private void updateReceivedData(byte[] data) {
        //do something with received data

        //for displaying:
        String rxString = null;
        try {
            rxString = new String(data, "UTF-8"); // put the data you got into a string
            myTextView3.append(rxString);
            myScrollView.fullScroll(View.FOCUS_DOWN);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
