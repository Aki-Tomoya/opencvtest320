package com.example.opencv320test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Opencvtest extends AppCompatActivity {

    Button btnProcess;
    Bitmap srcBitmap;
    Bitmap grayBitmap;
    ImageView imgHuaishi;
    private static boolean flag = true;
    //private static boolean isFirst = true;
    private static final String TAG = "MainActivity";

    //OpenCV库加载并初始化成功后的回调函数
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            // TODO Auto-generated method stub
            switch (status) {
                case BaseLoaderCallback.SUCCESS:
                    Log.i(TAG, "成功加载");
                    break;
                default:
                    super.onManagerConnected(status);
                    Log.i(TAG, "加载失败");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencvtest);

        initUI();
        btnProcess.setOnClickListener(new Opencvtest.ProcessClickListener());


    }

    public void initUI() {
        btnProcess = (Button) findViewById(R.id.btn_gray_process);
        imgHuaishi = (ImageView) findViewById(R.id.img_2);
        Log.i(TAG, "initUI sucess...");

    }

    public void procSrc2Gray() {
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();

        srcBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_1);

        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);
        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray sucess...");
    }

    private class ProcessClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            procSrc2Gray();
            if (flag) {
                imgHuaishi.setImageBitmap(grayBitmap);
                btnProcess.setText("查看原图");
                flag = false;
            } else {
                imgHuaishi.setImageBitmap(srcBitmap);
                btnProcess.setText("灰度化");
                flag = true;
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_13, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
}