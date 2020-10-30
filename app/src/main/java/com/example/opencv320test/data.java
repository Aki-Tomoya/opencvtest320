package com.example.opencv320test;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class data extends AppCompatActivity {
    private static String TAG="dataeLog";
    private static boolean flag = true;
    private  static  FileInputStream fis;
    String pic_path;
    Button OpenCvstart;
    ImageView image_input;
    Bitmap srcBitmap;
    Bitmap grayBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        pic_path=getIntent().getStringExtra("picPath");
        initUI();
        OpenCvstart.setOnClickListener(new data.ProcessClickListener());
    }

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

    public void initUI() {
        OpenCvstart = (Button) findViewById(R.id.opencvstart);
        image_input = (ImageView) findViewById(R.id.inputview);
        Log.i(TAG, "初始化UI成功");

    }

    public void procSrc2Gray() {
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();

        try {
             fis= new FileInputStream(pic_path);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "procSrc2Gray: "+pic_path);
        }

        srcBitmap = BitmapFactory.decodeStream(fis);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);

        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray成功");
    }

    private class ProcessClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            procSrc2Gray();
            if (flag) {
                image_input.setImageBitmap(grayBitmap);
                OpenCvstart.setText("查看原图");
                flag = false;
            } else {
                image_input.setImageBitmap(srcBitmap);
                OpenCvstart.setText("灰度化");
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
