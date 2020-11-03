package com.example.opencv320test;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.bumptech.glide.Glide;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.opencv.imgcodecs.Imgcodecs;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static org.opencv.core.CvType.CV_32F;


public class takephoto extends AppCompatActivity implements EasyPermissions.PermissionCallbacks{
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};
    static String picPath,file_pic_path;

    //PATH TO OUR MODEL FILE AND NAMES OF THE INPUT AND OUTPUT NODES
    //各节点名称
    private static  String MODEL_FILE = "file:///android_asset/srcnn_model.pb"; //model文件路径
    String INPUT_NAME = "input_1";                      //输入节点名称
    String OUTPUT_NAME = "output_1";                    //输出节点名称
    Bitmap bitmap;


    //保存图片和图片尺寸的
    float[] PREDICTIONS;                              //生成输出数组
    float[] floatValues = new float[33*33];           //用宽度以及高度生成输入数组

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

        photoAndCamera();
        getbit();
        //transplant();


        findViewById(R.id.gotodata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(takephoto.this, data.class);
                intent.putExtra("picPath",getRealPathFromURI(Uri.parse((picPath))));
                //saveBitmap2file(bitmap,"test");
                Log.d("piccc", "onClick: "+getRealPathFromURI(Uri.parse((picPath))));

                startActivity(intent);
            }
        });


    }

    //转换uri地址
    private String getRealPathFromURI(Uri contentUri) { //传入图片uri地址
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(takephoto.this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }







    public void transplant(){


        //tf = new TensorFlowInferenceInterface(getAssets(),MODEL_PATH);=======

        gotodata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    //imageView.setImageBitmap(bitmap);

                    //byte []result = predict();         //传入灰度化图片
                    //将byte数组转化为图片


                }catch(Exception e){

                }


            }
        });

    }



    public void predict(String blankimage) {
        Log.i("running", "TensorFlow Mobile running ......");
        //生成float数组保存图片信息
        Mat blank = Imgcodecs.imread(blankimage);
        //转化为float数组
        blank.convertTo(blank,CV_32F);
        //将blank传入模型
        tf.feed(INPUT_NAME,floatValues,1,33,33,1);
        //运行模型
        tf.run(new String[]{OUTPUT_NAME});
        //输出数据
        tf.fetch(OUTPUT_NAME,PREDICTIONS);
        Log.i("stoprunning", "TensorFlow Mobile running ......");
        //将float数组变为mat对象
        //Mat result = new Mat(33,33,CV_32F,PREDICTIONS);
        //将新的mat对象保存为图片
        //Imgcodecs.imwrite("result.jpg",result);
    }

    //TODO:预处理，将灰度图切割为n块33*33的图片，将小图片传入模型中；解决float数组转Mat对象问题，将保存的图片找到并显示在data界面中。


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
            Log.d("taggg", "showpic: picPath ="+media.getPath());
        }
    }

    public void getbit(){
         bitmap=BitmapFactory.decodeFile(picPath);
    }

    //bitmap测试
    public static void saveBitmap2file(Bitmap bmp, String filename)
    {
        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try
        {

            stream = new FileOutputStream(Environment
                    .getExternalStorageDirectory().getPath()
                    + "/"
                    + filename
                    + ".jpg");
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        bmp.compress(format, quality, stream);
        try
        {
            stream.flush();
            stream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
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
