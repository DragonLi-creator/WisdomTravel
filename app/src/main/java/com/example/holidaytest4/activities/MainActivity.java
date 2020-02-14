package com.example.holidaytest4.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import com.example.holidaytest4.R;
import com.example.holidaytest4.adapter.SampleFragmentPagerAdapter;
import com.example.holidaytest4.services.BigDatesIntentServices;
import com.example.holidaytest4.utils.UiUtils;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity  {

    private int defaultTabIndex = 0;
    ViewPager viewPager;
    TabLayout tabLayout;
    private int imageDefaultId[] = new int[]{R.drawable.home,
            R.drawable.found,
            R.drawable.mine};
    private int imageActiveId[] = new int[]{R.drawable.home_pressed,
            R.drawable.found_pressed,
            R.drawable.mine_pressed};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this, true);
        setContentView(R.layout.main_scenic);
        //可以在内部存储空间创建文件夹，返回的是文件夹对象
        this.getFilesDir();
        //动态申请权限
        applyPermission();

        defaultTabIndex = getIntent().getIntExtra("index", 0);
        initComponents();
        //有问题！！！
        Intent intent = new Intent(this, BigDatesIntentServices.class);
        startService(intent);

    }
    void initComponents() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        SampleFragmentPagerAdapter pagerAdapter =
                new SampleFragmentPagerAdapter(getSupportFragmentManager(), this);

        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);

        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                View view = pagerAdapter.getTabView(i);
                ImageView imageView = null;
                if (view != null && i == defaultTabIndex) {
                    imageView = view.findViewById(R.id.imageView);
                    imageView.setImageResource(imageActiveId[i]);
                }
                tab.setCustomView(view);
            }
        }
        viewPager.setCurrentItem(defaultTabIndex);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < tabLayout.getTabCount(); i++) {
                    TabLayout.Tab tab = tabLayout.getTabAt(i);
                    if (tab != null) {
                        View view = tab.getCustomView();
                        ImageView imageView = null;
                        if (view != null) {
                            imageView = view.findViewById(R.id.imageView);
                            if (i == position)
                                imageView.setImageResource(imageActiveId[i]);
                            else
                                imageView.setImageResource(imageDefaultId[i]);
                        }
                        tab.setCustomView(view);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //跳转登录界面,方便返回值
    public void jumpLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * 动态申请危险权限
     */
    private void applyPermission() {
        //权限集合
        List<String> permissionList = new ArrayList<>();
        //检查Manifest中的权限是否已申请
        //若未申请，则加入权限列表，待之后申请
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.
                SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.SEND_SMS);
        }
        //若权限列表不为空，则进行权限的申请
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(MainActivity.this, "必须同意所有权限才可以使用本程序!", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
}