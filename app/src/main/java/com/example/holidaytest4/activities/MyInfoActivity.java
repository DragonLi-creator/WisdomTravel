package com.example.holidaytest4.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.holidaytest4.R;
import com.example.holidaytest4.beans.Visitor;
import com.example.holidaytest4.utils.UiUtils;
import org.litepal.LitePal;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//选择相册
    public static final int PICTURE_CUT = 3;//剪切图片
    private Uri imageUri;//相机拍照图片保存地址
    private String imagePath;//打开相册选择照片的路径
    private boolean isClickCamera;//是否是拍照裁剪
    private TextView name_my_info_tv;
    private TextView id_my_info_tv;
    private TextView phone_my_info_tv;
    private Uri headIconUri;
    private ImageView headIcon;
    private Dialog dialog;
    //当前用户登入的账号
    private String userId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info_layout);
        //初始化View,和设置各种点击事件
        initView();
        //获取当前登入的账号
        getUserID();
        //初始化点击头像后生成的选择框
        File file = new File(getExternalFilesDir(null), userId + "headIcon.jpg");
        headIconUri = Uri.fromFile(file);
        initTakePhotoDialog();
    }

    //初始化View

    private void initView() {
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_my_info);
        //找到控件
        LinearLayout head_icon_my_info_box = findViewById(R.id.head_icon_my_info_box);
        LinearLayout name_my_info_box = findViewById(R.id.name_my_info_box);
        LinearLayout id_my_info_box = findViewById(R.id.id_my_info_box);
        LinearLayout phone_my_info_box = findViewById(R.id.phone_my_info_box);
        Button out_btn = findViewById(R.id.out_my_info);
        name_my_info_tv = findViewById(R.id.name_my_info);
        id_my_info_tv = findViewById(R.id.id_my_info);
        phone_my_info_tv = findViewById(R.id.phone_my_info);
        headIcon = findViewById(R.id.head_icon_my_info);
        //点击事件
        head_icon_my_info_box.setOnClickListener(this);
        name_my_info_box.setOnClickListener(this);
        id_my_info_box.setOnClickListener(this);
        phone_my_info_box.setOnClickListener(this);
        out_btn.setOnClickListener(this);
        //标题栏返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this,true);
    }
    /**
     * 获取当前登入的账号
     */
    private void getUserID() {
        Intent intent = getIntent();
        if (intent != null) {
            userId = intent.getStringExtra("userId");
        }
    }

    /**
     * 初始化点击头像后生成的选择框
     */
    private void initTakePhotoDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //反射一个自定义的全新的对话框布局
        View view = inflater.inflate(R.layout.photo_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        //在当前布局中找到控件对象
        Button take_photo = view.findViewById(R.id.take_photo_dialog);
        Button chosen_photo = view.findViewById(R.id.chosen_photo_dialog);
        //监听事件
        take_photo.setOnClickListener(this);
        chosen_photo.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        //头像加载
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(headIconUri));
            headIcon.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            headIcon.setImageResource(R.drawable.user_12);
        }

        //给控件赋值
        List<Visitor> visitors = LitePal.where("userId = ?", userId).find(Visitor.class);
        for (Visitor visitor : visitors) {
            name_my_info_tv.setText(visitor.getUserName());
            id_my_info_tv.setText(visitor.getUserId());
            phone_my_info_tv.setText(visitor.getPhone());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {   //点击头像
            case R.id.head_icon_my_info_box:
                dialog.show();
                break;
            case R.id.take_photo_dialog: {  //点击拍照
                //相机获取照片并剪裁
                if (ContextCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // 进入这儿表示没有权限
                    ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                } else {
                    openCamera();
                }
                dialog.dismiss();
                break;
            }
            case R.id.chosen_photo_dialog: {  //点击相册
                //相册获取照片并剪裁
                if (ContextCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectFromAlbum();//打开相册
                }
                dialog.dismiss();
                break;
            }
            case R.id.name_my_info_box:         //点击昵称,跳转修改昵称界面
                intent = new Intent(this, ModifyNameActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
                break;
            case R.id.id_my_info_box:
                Toast.makeText(this, "账号", Toast.LENGTH_SHORT).show();
                break;
            case R.id.phone_my_info_box:        //点击手机号码,跳转修改手机号码界面
                intent = new Intent(this, ModifyPhoneActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
                break;
            case R.id.out_my_info:               //点击退出账号
                //登入状态清空
                SharedPreferences.Editor editor = getSharedPreferences("login_state",MODE_PRIVATE).edit();
                editor.putString("userId","");
                editor.apply();
                finish();
                break;
        }
    }
    //图片结果处理方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO://拍照
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
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(headIconUri));
                        //bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    } else {
                        bitmap = BitmapFactory.decodeFile(imagePath);
                    }
                    headIcon.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void openCamera() {
        // 创建File对象，用于存储拍照后的图片,一般存放临时缓存数据
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
            imageUri = FileProvider.getUriForFile(MyInfoActivity.this, "com.example.holidaytest4", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }


    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(MyInfoActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MyInfoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
        // 创建File对象，用于存储裁剪后的图片，避免更改原图一般存放长久缓存数据
        File file = new File(getExternalFilesDir(null), userId + "headIcon.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        headIconUri = Uri.fromFile(file);
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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, headIconUri);
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
