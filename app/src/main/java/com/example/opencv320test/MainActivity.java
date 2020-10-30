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

import com.skyfishjy.library.RippleBackground;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //snipper框
        NiceSpinner niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("SRCNN", "Misakanet", "xujie", "aoran", "kaixuan"));
        niceSpinner.attachDataSource(dataset);

//        niceSpinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
//            @Override
//            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
//                // This example uses String, but your type can be any
//                String item = parent.getItemAtPosition(position);
//            }
//        });

        //水波纹扩散特效
        rippleBackground=(RippleBackground)findViewById(R.id.content);
        ImageView imageView = (ImageView) findViewById(R.id.takephoto);
        rippleBackground.startRippleAnimation();

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