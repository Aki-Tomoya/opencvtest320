package com.example.opencv320test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import org.opencv.imgproc.Imgproc;



import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.InputStream;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static org.opencv.core.CvType.CV_32FC1;

public class takephoto extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    static String picPath,file_pic_path;

    //PATH TO OUR MODEL FILE AND NAMES OF THE INPUT AND OUTPUT NODES
    //各节点名称
    String MODEL_PATH = "src\\main\\assets\\srcnn_model.pb";//file:///assets/squeezenet.pb
    String INPUT_NAME = "input_1";
    String OUTPUT_NAME = "output_1";


    //TensorFlowInferenceInterface tf;============

    //ARRAY TO HOLD THE PREDICTIONS AND FLOAT VALUES TO HOLD THE IMAGE DATA
    //保存图片和图片尺寸的
    float[] PREDICTIONS = new float[1000];
    float[] floatValues;

    public static final String EXTRAS_ENDLESS_MODE = "EXTRAS_ENDLESS_MODE";
    private TensorFlowInferenceInterface tf;



    ImageView iv;
    Button gotodata;

    /*
     * 在需要调用TensoFlow的地方，加载so库“System.loadLibrary("tensorflow_inference");
     * 并”import org.tensorflow.contrib.android.TensorFlowInferenceInterface;就可以使用了
     * */
    //Load the tensorflow inference library
    //static{}(即static块)，会在类被加载的时候执行且仅会被执行一次，一般用来初始化静态变量和调用静态方法。
    static {
        //System.loadLibrary("tensorflow_inference");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takephoto);
        iv=findViewById(R.id.setPhoto);

        //tf = new TensorFlowInferenceInterface(getAssets(),MODEL_PATH);



        photoAndCamera();
        //transplant();
        //Mat mat = Imgcodecs.imread("G:\\cover.png");


        findViewById(R.id.gotodata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(takephoto.this, data.class);
                intent.putExtra("picPath",picPath);
                startActivity(intent);
            }
        });


    }





    public void transplant(){


        //tf = new TensorFlowInferenceInterface(getAssets(),MODEL_PATH);=======

        gotodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    //imageView.setImageBitmap(bitmap);

                    predict();

                }catch(Exception e){

                }


            }
        });

    }



    public void predict(){

        //Runs inference in background thread
        new AsyncTask<Integer,Integer,Integer>(){

            @Override
            protected Integer doInBackground(Integer ...params){
                //预处理操作
               // Imgproc.cvtColor(picPath,mRgba,Imgproc.COLOR_YCrCb2BGR);   //灰度化

               // floatValues = Imgcodecs.imread();    //添加图片

                //Pass input into the tensorflow
               tf.feed(INPUT_NAME,floatValues,1,33,33,3);

                //compute predictions
               tf.run(new String[]{OUTPUT_NAME});

                //copy the output into the PREDICTIONS array
               tf.fetch(OUTPUT_NAME,PREDICTIONS);

               // CvMat labelsMat = cvMat(16, 1, CV_32FC1, labels);



                return 0;
            }

        }.execute(0);

    }


    public Object[] argmax(float[] array){

        int best = -1;
        float best_confidence = 0.0f;
        for(int i = 0;i < array.length;i++){
            float value = array[i];
            if (value > best_confidence){
                best_confidence = value;
                best = i;
            }
        }
        return new Object[]{best,best_confidence};
    }

    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    //初始化相机相册
    public void photoAndCamera(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_WeChat_style)
                .selectionMode(PictureConfig.SINGLE)
                .loadImageEngine(GlideEngine.createGlideEngine())
                .isEnableCrop(true)
                .forResult(PictureConfig.CHOOSE_REQUEST);

    }

    //循环获取所选图片地址
    public void showpic(List<LocalMedia> selectList){
        for (LocalMedia media : selectList) {
            picPath=media.getPath();
            int i=media.getPath().length();
            file_pic_path=media.getPath().substring(8,i);
            Log.d("taggg", "showpic: picPath ="+media.getPath().substring(8,i));
        }
    }

    //Glide图片显示
    public void glidepic(String path){
        Log.d("tagg", "正常使用的地址 "+path);
        Glide.with(this)
                .load(path)
                .into(iv);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    showpic(selectList);
                    glidepic(picPath);
                    break;
                default:
                    break;
            }
        }
    }

    //为了解决Android6之后的权限问题，使用EasyPermissions接口动态获取权限
    /*===========================================权限========================================================*/

    private void getPermission() {
        if (EasyPermissions.hasPermissions(this, permissions)) {
            //已经打开权限
            Toast.makeText(this, "已经获取相关权限", Toast.LENGTH_SHORT).show();
        } else {
            //没有打开相关权限、申请权限
            EasyPermissions.requestPermissions(this, "需要获取相册、照相使用权限", 1, permissions);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    //成功打开权限
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

        Toast.makeText(this, "相关权限获取成功", Toast.LENGTH_SHORT).show();
    }
    //用户未同意权限
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "请同意相关权限，否则功能无法使用", Toast.LENGTH_SHORT).show();
    }

    /*===========================================权限========================================================*/

    /*******************************************前端**********************************************************/



}
