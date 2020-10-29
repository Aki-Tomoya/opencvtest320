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


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.takephoto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, takephoto.class);
                startActivity(intent);

            }
        });
        findViewById(R.id.gotoopencv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, Opencvtest.class);
                startActivity(intent);

            }
        });




        /*--------下面是从拍照活动页面拿到了flag和值，匹配后执行跳转到data.fragment--------------*/
        int id = getIntent().getIntExtra("flag", 0);
        if (id==1){
            /*
             *
             *这里是跳转的办法（留给你填了！
             *
             *
             *
             * */
        }
        /*-------------------------------------------------------------*/




    }
}