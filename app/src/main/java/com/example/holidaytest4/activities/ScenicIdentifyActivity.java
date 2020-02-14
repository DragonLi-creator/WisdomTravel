package com.example.holidaytest4.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.example.holidaytest4.R;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.holidaytest4.utils.UiUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ScenicIdentifyActivity extends AppCompatActivity {
    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//选择相册
    public static final int PICTURE_CUT = 3;//剪切图片
    private Uri imageUri;//相机拍照图片保存地址
    private Uri outputUri;//裁剪万照片保存地址
    private String imagePath;//打开相册选择照片的路径
    private boolean isClickCamera;//是否是拍照裁剪
    //拍照,相册选择弹出
    private AlertDialog takePhotoDialog;
    //拍照,相册选择弹出框
    private AlertDialog CircleDialog;
    //景物识别图片
    private ImageView im_scenic_identify;
    //景物描述信息
    private TextView tv_scenic_describe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_identify_layout);
        initView();
        //初始化点击景物识别时弹出的对话框
        initSelectBox();
    }

    /**
     * 初始化View
     */
    private void initView() {
        im_scenic_identify = findViewById(R.id.im_scenic_identify);
        tv_scenic_describe = findViewById(R.id.tv_scenic_describe);
        im_scenic_identify.setOnClickListener(new MyOnClickListener());
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_scenic_identify);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this,true);
        //getProcessCircle();
    }

    /*
     *  初始化点击景物识别后显示的对话框
     */
    private void initSelectBox() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //自定义的全新的对话框布局
        View view = inflater.inflate(R.layout.photo_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        takePhotoDialog = builder.create();
        //在当前布局中找到控件对象
        Button take_photo = view.findViewById(R.id.take_photo_dialog);
        Button chosen_photo = view.findViewById(R.id.chosen_photo_dialog);
        //监听事件
        take_photo.setOnClickListener(new MyOnClickListener());
        chosen_photo.setOnClickListener(new MyOnClickListener());
    }

    /*
     *  初始化圆形进度条
     */
    private void getProcessCircle() {
        if (CircleDialog == null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            //自定义的全新的对话框布局
            View view = inflater.inflate(R.layout.process_circle_dialog, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            CircleDialog = builder.create();
            //设置不可以点击弹窗之后进行取消
            CircleDialog.setCanceledOnTouchOutside(false);
            //设置旋转动画
            Animation animation = new RotateAnimation(0.0f, 720.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            //设置停留在最后一幁
            animation.setFillAfter(true);
            //设置此动画的加速度曲线。默认为线性插值。若无，效果上会有停顿
            animation.setInterpolator(new LinearInterpolator());
            //设置持续时间
            animation.setDuration(1200);
            //设置动画重复次数，无限
            animation.setRepeatCount(Animation.INFINITE);
            //重复模式
            animation.setRepeatMode(Animation.RESTART);
            ImageView iv_process_circle = view.findViewById(R.id.iv_process_circle);
            iv_process_circle.setAnimation(animation);
        }
    }

    /**
     * 点击事件
     */
    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.im_scenic_identify:           //点击添加图片
                    takePhotoDialog.show();
                    break;
                case R.id.take_photo_dialog:            //点击拍照
                    //相机获取照片并剪裁
                    if (ContextCompat.checkSelfPermission(ScenicIdentifyActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // 进入这儿表示没有权限
                        ActivityCompat.requestPermissions(ScenicIdentifyActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                    } else {
                        openCamera();
                    }
                    takePhotoDialog.dismiss();
                    break;
                case R.id.chosen_photo_dialog:          //点击相册
                    //相册获取照片并剪裁
                    if (ContextCompat.checkSelfPermission(ScenicIdentifyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ScenicIdentifyActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        selectFromAlbum();//打开相册
                    }
                    takePhotoDialog.dismiss();
                    break;
            }
        }
    }

    //图片结果处理方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO://拍照
                //cropPhoto(imageUri);
                if (resultCode == RESULT_OK) {
                    cropPhoto(imageUri);//裁剪图片
                }
                break;
            case CHOOSE_PHOTO://打开相册
                // 判断手机系统版本号
                if (data !=null){
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4及以上系统使用这个方法处理图片
                    handleImageOnKitKat(data);
                } else {
                    // 4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data);
                }}
                break;
            case PICTURE_CUT://裁剪完成
                isClickCamera = true;
                Bitmap bitmap = null;
                try {
                    if (isClickCamera) {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    } else {
                        bitmap = BitmapFactory.decodeFile(imagePath);
                    }

                    im_scenic_identify.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                //裁剪后加载动画
                getProcessCircle();
                CircleDialog.show();
                break;
        }
    }

    private void openCamera() {
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
            //参数二:fileprovider绝对路径 ：项目包名
            imageUri = FileProvider.getUriForFile(ScenicIdentifyActivity.this, "com.example.holidaytest4", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }


    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(ScenicIdentifyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ScenicIdentifyActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // 打开相册
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        File file = new File(getExternalCacheDir(), "crop_image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
        //intent.putExtra("outputX", 400);
        //intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, PICTURE_CUT);
    }

    // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        cropPhoto(uri);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        cropPhoto(uri);
    }

}
