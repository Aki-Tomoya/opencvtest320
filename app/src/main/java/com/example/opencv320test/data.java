package com.example.opencv320test;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;

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
    ImageView image_input,image_output;
    Bitmap srcBitmap;
    Bitmap grayBitmap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        pic_path=getIntent().getStringExtra("picPath");



        initUI();
        Glide.with(this).load(pic_path).into(image_input);

        OpenCvstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                procSrc2Gray();
                Glide.with(data.this).load(grayBitmap).into(image_output);
            }
        });
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
        image_output=findViewById(R.id.outputview);
        Log.i(TAG, "初始化UI成功");

    }

    public void procSrc2Gray() {
        Mat rgbMat = new Mat();
        Mat grayMat = new Mat();

//        int i=pic_path.length();
        srcBitmap = BitmapFactory.decodeFile(pic_path);
        grayBitmap = Bitmap.createBitmap(srcBitmap.getWidth(), srcBitmap.getHeight(), Bitmap.Config.RGB_565);

        Utils.bitmapToMat(srcBitmap, rgbMat);//convert original bitmap to Mat, R G B.
        Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);//rgbMat to gray grayMat
        Utils.matToBitmap(grayMat, grayBitmap); //convert mat to bitmap
        Log.i(TAG, "procSrc2Gray成功");
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
