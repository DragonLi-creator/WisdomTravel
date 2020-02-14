package com.example.holidaytest4.activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.example.holidaytest4.R;
import com.example.holidaytest4.utils.DensityUtil;
import com.example.holidaytest4.utils.UiUtils;

/**
 * 室内蓝牙定位
 */
public class IndoorPositionActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OPEN_GPS = 1;
    private static final int REQUEST_CODE_OPEN_BLUE_TOOTH = 2;

    private ImageView img;

    private AnimatorSet set;

    //测试的总长度和宽度
    private int mWidth = 0;
    private int mHeight = 0;

    //Animation 移动单位
    private int mItem_Animation = 800;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.indoor_position_layout);
        initView();
        getWH();
        checkPermissions();
        Toast.makeText(IndoorPositionActivity.this, "开启室内定位", Toast.LENGTH_SHORT).show();
        set = new AnimatorSet();
        //开始动画！！！！！！！！
        startAnimation(set,0,0,1000,2000);
    }

    /**
     * 获取手机屏幕的宽高
     */
    private void getWH() {
        WindowManager manager = getWindowManager();
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        mWidth = metrics.widthPixels - 80;  //以要素为单位
        mHeight = metrics.heightPixels - DensityUtil.dipToPx(this, 50) - 100;
        Log.d("getWH", mWidth + ":::" + mHeight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        set.cancel();
    }

    private void initView() {
        img = findViewById(R.id.test_iv);
        Toolbar toolbar = findViewById(R.id.toolbar_indoor);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Log.d("getHeight()", String.valueOf(toolbar.getMeasuredHeight()));

        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this,true);
    }


    /**
     * 开始执行动画
     */
    private void startAnimation(AnimatorSet set, float startX, float startY, float endX, float endY) {
        //set.end();
        //set.cancel();
        set.playTogether(
                ObjectAnimator.ofFloat(img, "translationX", startX, endX),
                ObjectAnimator.ofFloat(img, "translationY", startY, endY)
        );
        if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 200) {
            set.setDuration(mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 400) {
            set.setDuration(2 * mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 600) {
            set.setDuration(3 * mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 800) {
            set.setDuration(4 * mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 1000) {
            set.setDuration(5 * mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 1200) {
            set.setDuration(6 * mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 1400) {
            set.setDuration(7 * mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 1600) {
            set.setDuration(8 * mItem_Animation).start();
        } else if ((Math.abs((startX - endX)) + Math.abs((startY - endY))) < 1800) {
            set.setDuration(9 * mItem_Animation).start();
        } else {
            set.setDuration(10 * mItem_Animation).start();
        }
    }

    private void checkPermissions() {
        //判断是否已经打开蓝牙
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "请先打开蓝牙...", Toast.LENGTH_LONG).show();
            //2 弹出对话框供用户选择是否打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_CODE_OPEN_BLUE_TOOTH);
        } else {
            if (checkGPSIsOpen()) {
                Toast.makeText(this, "开始扫描", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "请先打开GPS", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
            }
        }
    }

    /**
     * 检查GPS定位是否已经打开
     */
    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_OPEN_BLUE_TOOTH:   //打开蓝牙
                if (mBluetoothAdapter.isEnabled()) {
                    if (checkGPSIsOpen()) {
                        Toast.makeText(this, "开始扫描", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "请先打开GPS", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setAction(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, REQUEST_CODE_OPEN_GPS);
                    }
                } else {
                    Toast.makeText(this, "请先打开蓝牙", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_OPEN_GPS:         //打开GPS
                if (checkGPSIsOpen()) {
                    Toast.makeText(this, "开始扫描", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "请先打开GPS", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
