package com.example.holidaytest4.utils;

import android.app.Activity;
import android.view.View;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class UiUtils {

    /**
     * 界面设置状态栏字体颜色
     */
    public static void changeStatusBarTextImgColor(Activity activity, boolean isBlack) {
        if (isBlack) {
            //设置状态栏黑色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            //恢复状态栏白色字体
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        //创建通信客户端实例
        OkHttpClient client=new OkHttpClient();
        //创建http请求实例，address为网络地址
        Request request=new Request.Builder().url(address).build();
        //client请求返回数据，并加入队列
        client.newCall(request).enqueue(callback);
    }
}