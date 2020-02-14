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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.example.holidaytest4.R;
import com.example.holidaytest4.consts.Consts;
import com.example.holidaytest4.utils.UiUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//反馈活动
public class FeedbackActivity extends AppCompatActivity {

    public static final int TAKE_PHOTO = 1;//拍照
    public static final int CHOOSE_PHOTO = 2;//选择相册
    public static final int PICTURE_CUT = 3;//剪切图片
    private Uri imageUri;//相机拍照图片保存地址
    private Uri outputUri;//裁剪万照片保存地址
    private String imagePath;//打开相册选择照片的路径
    private boolean isClickCamera;//是否是拍照裁剪
    private static final String TAG = "FeedbackActivity";
    //拍照,相册选择弹出框
    private AlertDialog dialog;
    private ImageView iv_photo_feedback;
    private EditText et_address_feedback;
    private EditText et_location_feedback;
    private EditText et_title_feedback;
    private EditText et_phone_feedback;
    //我的点击事件
    private MyOnClickListener myOnClickListener;
    //反馈需要提交的信息
    private String address;
    private String location;
    private String title;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_layout);
        initView();
        initSelectBox();
    }

    private void initView() {
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_feedback);
        //获取反馈界面的控件
        et_address_feedback = findViewById(R.id.et_address_feedback);
        et_location_feedback = findViewById(R.id.et_location_feedback);
        iv_photo_feedback = findViewById(R.id.iv_photo_feedback);
        et_title_feedback = findViewById(R.id.et_title_feedback);
        et_phone_feedback = findViewById(R.id.et_phone_feedback);
        Button btn_submit = findViewById(R.id.btn_submit_feedback);
        myOnClickListener = new MyOnClickListener();
        //对ImageView设置监听器，点击即可调用摄像头
        iv_photo_feedback.setOnClickListener(myOnClickListener);
        //对按钮控件设置监听器
        btn_submit.setOnClickListener(myOnClickListener);
        //自动弹出输入法
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this,true);
    }

    /***
     *  初始化点击上传图片后显示的对话框
     */
    private void initSelectBox() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //反射一个自定义的全新的对话框布
        View view = inflater.inflate(R.layout.photo_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //对话框里加载photo_dialog布局
        builder.setView(view);
        dialog = builder.create();
        //在当前布局中找到控件对象
        Button take_photo = view.findViewById(R.id.take_photo_dialog);
        Button chosen_photo = view.findViewById(R.id.chosen_photo_dialog);
        //监听事件
        take_photo.setOnClickListener(myOnClickListener);
        chosen_photo.setOnClickListener(myOnClickListener);
    }

    /**
     * 提交反馈
     */
    private void submitFeedback() {
        //获取控件上的信息
        address = et_address_feedback.getText().toString();
        location = et_location_feedback.getText().toString();
        title = et_title_feedback.getText().toString();
        phone = et_phone_feedback.getText().toString();
        //判断反馈的内容是否已经全部填写完成
        if (address.isEmpty() || location.isEmpty() || title.isEmpty() || phone.isEmpty()) {
            Toast.makeText(FeedbackActivity.this, "请填写完成再提交", Toast.LENGTH_SHORT).show();
        } else {  //填写成功
            Toast.makeText(FeedbackActivity.this, "提交完成", Toast.LENGTH_SHORT).show();
            NetworkTask networkTask = new NetworkTask();
            networkTask.execute(imagePath);
            finish();
        }
    }

    /**
     * 进行图片上传
     */
    private String doPost(String imagePath) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        String result = "error";
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)                    //以文件形式上传
                .addFormDataPart("requestType","景区反馈")    //上传的类型
                .addFormDataPart("place", address)                  //地点名称
                .addFormDataPart("detailed_place", location)        //所在位置
                .addFormDataPart("content", title)                  //问题描述
                .addFormDataPart("contact_info", phone)             //电话号码
                .addFormDataPart("image", imagePath,                         //图片
                        RequestBody.create(MediaType.parse("image/jpg"), new File(imagePath)))
                .build();
        Request.Builder reqBuilder = new Request.Builder();
        Request request = reqBuilder
                .url(Consts.FEEDBACK_SERVER_URL)
                .post(requestBody)
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            Log.d(TAG, "响应码 " + response.code());
            if (response.isSuccessful()) {
                String resultValue = response.body().string();
                Log.d(TAG, "响应体 " + resultValue);
                return resultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 访问网络AsyncTask,访问网络在子线程进行并返回主线程通知访问的结果
     */
    class NetworkTask extends AsyncTask<String, Integer, String> {

        /**
         * 后台任务开始之前调用，通常用来初始化界面操作
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * 执行后台耗时操作，已在子线程中执行
         * @return   对结果进行返回
         */
        @Override
        protected String doInBackground(String... params) {
            return doPost(params[0]);
        }

        /**
         * 当后台任务执行完毕时调用
         * @param result  后台执行任务的返回值
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "服务器响应" + result);
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
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));
                    } else {
                        bitmap = BitmapFactory.decodeFile(imagePath);
                    }

                    iv_photo_feedback.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
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
            imageUri = FileProvider.getUriForFile(FeedbackActivity.this, "com.example.holidaytest4", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }


    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        //image/*指所有图片类型
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

    /**
     * 点击接口
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_photo_feedback:      //添加图片
                    dialog.show();
                    break;
                case R.id.btn_submit_feedback:    //提交反馈
                    submitFeedback();
                    break;
                case R.id.take_photo_dialog:
                    //拍照照片并剪裁
                    if (ContextCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // 进入这儿表示没有权限
                        ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                    } else {
                        openCamera();
                    }
                    dialog.dismiss();
                    break;
                case R.id.chosen_photo_dialog:
                    //相机获取照片并剪裁
                    if (ContextCompat.checkSelfPermission(FeedbackActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(FeedbackActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        selectFromAlbum();//打开相册
                    }
                    dialog.dismiss();
                    break;
            }
        }
    }

}
