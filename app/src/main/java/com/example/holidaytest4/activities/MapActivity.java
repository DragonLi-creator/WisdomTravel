package com.example.holidaytest4.activities;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.drawerlayout.widget.DrawerLayout;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.example.holidaytest4.R;
import com.example.holidaytest4.consts.Consts;
import com.example.holidaytest4.utils.AStarUtils;
import com.example.holidaytest4.utils.RoadPlanningUtils;
import com.example.holidaytest4.utils.UiUtils;
import com.google.android.material.navigation.NavigationView;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MapActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //代码请求打开蓝牙，GPS
    private static final int REQUEST_CODE_OPEN_BLUE_TOOTH = 1;
    private static final int REQUEST_CODE_OPEN_GPS = 2;
    private final String Tag = "MapActivity";
    //进行与服务端的实时数据交流
    private WebSocket webSocket;
    //点击接口
    private MyOnClickListener myOnClickListener;
    //部分景点的人数,
    private int[] numbers = new int[3];
    private String[] locations = new String[3];
    //地图
    MapView mMapView = null;
    //地图控制器
    private AMap aMap;
    //路线规划控件
    private LinearLayout road_sign_box;
    private EditText ed_myLocation;
    private EditText ed_targetLocation;
    private String targetLocation;
    private String myLocation;
    //路径规划路线
    private Polyline polyline;
    private Marker endMarker;   //终点标志
    private Marker startMarker;   //起点标志
    //路线规划控件是否可见,默认不可见
    private boolean isVisible = false;
    //各个景点的Maker
    private Marker marker;    //光明顶
    private Marker marker1;   //飞来石
    private Marker marker2;   //一线天
    private Marker marker3;   //迎客松
    private Marker marker4;   //步仙桥
    //室内定位
    private ImageView iv_indoor_position;
    private BluetoothAdapter mBluetoothAdapter;
    //抽屉
    DrawerLayout drawlayout;
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化view并设置接口
        initView(savedInstanceState);
        //设置路线规划空间是否显示
        setRoadSignVisible();
        //road_sign_box.setVisibility(View.VISIBLE);
        //申请权限
        requestLocation();
        //绘制Marker
        drawMarker();
        //webSocket连接
        webSocketConnect();
    }

    /**
     * 初始化view
     */
    private void initView(Bundle savedInstanceState) {
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this, true);//将状态栏字体变为黑色
        //获取地图控件引用
        mMapView = findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        road_sign_box = findViewById(R.id.road_sign_box);
        ed_targetLocation = findViewById(R.id.road_sign_target_location);
        ed_myLocation = findViewById(R.id.road_sign_my_location);
        ImageView iv_road_sign_logo = findViewById(R.id.road_sign_start_logo);
        TextView tv_road_sign_go = findViewById(R.id.road_sign_go);
        iv_indoor_position = findViewById(R.id.iv_indoor_position);
        //  地图上各种功能按钮
        ImageView iv_camera = findViewById(R.id.iv_camera);
        ImageView iv_road_sign = findViewById(R.id.iv_road_sign);
        ImageView iv_feedback = findViewById(R.id.iv_feedback);
        TextView tv_camera = findViewById(R.id.tv_camera);
        TextView tv_road_sign = findViewById(R.id.tv_raod_sign);
        TextView tv_feedback = findViewById(R.id.tv_feedback);
        //抽屉滑动
        navigationView = findViewById(R.id.navigation_view);
        drawlayout = findViewById(R.id.drawerlayout);

        //实现监听器方法
        if (myOnClickListener == null) {
            myOnClickListener = new MyOnClickListener();
        }
        iv_road_sign_logo.setOnClickListener(myOnClickListener);
        tv_road_sign_go.setOnClickListener(myOnClickListener);
        iv_camera.setOnClickListener(myOnClickListener);
        iv_road_sign.setOnClickListener(myOnClickListener);
        iv_feedback.setOnClickListener(myOnClickListener);
        tv_camera.setOnClickListener(myOnClickListener);
        tv_road_sign.setOnClickListener(myOnClickListener);
        tv_feedback.setOnClickListener(myOnClickListener);
        ed_myLocation.setOnClickListener(myOnClickListener);
        ed_targetLocation.setOnClickListener(myOnClickListener);
        iv_indoor_position.setOnClickListener(myOnClickListener);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ed_targetLocation.getWindowToken(), 0);
    }

    /**
     * 控件交互,手势交互
     */
    private void initUiSettings() {
        //实现控件交互,手势交互等
        UiSettings mUiSettings = aMap.getUiSettings();
        //1 定位按钮,默认关闭
        mUiSettings.setMyLocationButtonEnabled(true); //显示默认的定位按钮
        aMap.setMyLocationEnabled(true);// 可触发定位并显示当前位置
        //2 手势设置，禁止地图在拖动时发生旋转.
        mUiSettings.setRotateGesturesEnabled(false);   //旋转手势关闭
        mUiSettings.setTiltGesturesEnabled(false);     //倾斜手势关闭
        //3 缩放按钮，默认打开
        mUiSettings.setZoomControlsEnabled(true);
        //4 比例尺，默认不显示
        mUiSettings.setScaleControlsEnabled(true);
    }

    /**
     * 开始进行定位等功能
     */
    private void requestLocation() {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        //实现定位蓝点
        initPositionDot();
        initUiSettings();
        //默认设置地图的放缩级别
//        aMap.setMaxZoomLevel(20);
//        aMap.setMinZoomLevel(20);
        //移动到指定地点
        if (getIntent() != null) {
            double latitude = getIntent().getDoubleExtra("Latitude", 25.266431);
            double longitude = getIntent().getDoubleExtra("Longitude", 110.295181);
            navigateTo(latitude, longitude);
            Log.d("navigateTo", latitude+","+ longitude);
        }
        //开启室内地图
        aMap.showIndoorMap(true);
    }

    /**
     * 根据经纬度移动到某一个位置并显示
     */
    private void navigateTo(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);//构造一个位置
        //移动地图，最后一个参数为缩放级别
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 19));
    }

    /**
     * 实现定位蓝点,高德地图开发官网
     */
    private void initPositionDot() {
        //1  初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        //   连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
        //  （1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        //2  设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.interval(3000);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
        //3  设置定位圆圈,结果为圆圈透明，即达到不显示效果
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
        //4  设置定位蓝点的Style
        aMap.setMyLocationStyle(myLocationStyle);
    }

    /**
     * 绘制点标记,普贤塔，桂林抗战遗址，象眼岩
     */
    private void drawMarker() {
        //Marker点击接口
        AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
            boolean flag = false;
            // marker 对象被点击时回调的接口
            // 返回true 表示接口已经相应事件，否则返回false
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (flag) {
                    marker.showInfoWindow();
                } else {
                    marker.hideInfoWindow();
                }
                flag = !flag;
                return true;
            }
        };
        //点击Marker后弹出的信息接口
        AMap.OnInfoWindowClickListener infoWindowClickListener = new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                marker.hideInfoWindow();
            }
        };

        // 0 光明顶参数为经纬度
        LatLng latLng = new LatLng(30.132262, 118.169170);
        marker = aMap.addMarker(new MarkerOptions().
                position(latLng).title("光明顶").snippet("人流量：100").visible(true));
        // 1飞来石参数为经纬度
        LatLng latLng1 = new LatLng(30.135566,118.163966);
        marker1 = aMap.addMarker(new MarkerOptions().
                position(latLng1).title("飞来石").snippet("人流量：80").visible(true));
        // 2一线天
        LatLng latLng2 = new LatLng(30.126444, 118.169330);
        marker2 = aMap.addMarker(new MarkerOptions().
                position(latLng2).title("一线天").snippet("人流量：90").visible(true));
        // 3 迎客松
        LatLng latLng3 = new LatLng(30.122344, 118.175755);
        marker3 = aMap.addMarker(new MarkerOptions().
                position(latLng3).title("迎客松").snippet("人流量：70").visible(true));
        // 4 步仙桥
        LatLng latLng4 = new LatLng(30.131766, 118.155638);
        marker4 = aMap.addMarker(new MarkerOptions().
                position(latLng4).title("步仙桥").snippet("人流量：60").visible(true));
        //设置接口
        aMap.setOnMarkerClickListener(markerClickListener);
        aMap.setOnInfoWindowClickListener(infoWindowClickListener);
    }

    /**
     * 路线规划
     */
    private void drawLine() {
        //生成路线定点坐标列表
        List<LatLng> latLngs = new ArrayList<>();

        switch (AStarUtils.A_star(numbers, myLocation, targetLocation)) {
            case RoadPlanningUtils.ROAD_1:
                RoadPlanningUtils.gmdToFls(latLngs);
                start = "光明顶";
                end = "飞来石";
                break;
            case RoadPlanningUtils.ROAD_2:
                RoadPlanningUtils.gmdToBxq(latLngs);
                start = "光明顶";
                end = "步仙桥";
                break;
            case RoadPlanningUtils.ROAD_3:
                RoadPlanningUtils.yxtToFls(latLngs);
                start = "一线天";
                end = "飞来石";
                break;
            case RoadPlanningUtils.ROAD_4:
                RoadPlanningUtils.yxtToBxq(latLngs);
                start = "一线天";
                end = "步仙桥";
                break;
            default:
                Toast.makeText(this,"输入错误...",Toast.LENGTH_SHORT).show();
                return;
        }
        if (polyline != null) {
            polyline.remove();
        }
        polyline = aMap.addPolyline(new PolylineOptions()
                .addAll(latLngs)
                .width(10)
                .setUseTexture(true)       //使用纹理图
                .setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.road)));
        moveLocation(latLngs);
    }

    private String end = "";
    private String start = "";

    private void moveLocation(List<LatLng> points) {
        //动态调整地图显示，将路线呈现在屏幕
        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 1));
        //包含一个经纬度限制的区域，并且是最大可能的缩放级别
        aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        LatLng latLng_start = points.get(0);
        LatLng latLng_end = points.get(points.size() - 1);
        switch (end) {
            case "飞来石":
                //启动导航是蓝色路标隐藏，显示红路标
                marker1.setVisible(false);
                break;
            case "步仙桥":
                marker4.setVisible(false);
                break;
        }
        switch (start) {
            case "光明顶":
                //启动导航是蓝色路标隐藏，显示红路标
                marker.setVisible(false);
                break;
            case "一线天":
                marker2.setVisible(false);
                break;
        }
        //清空开始坐标
        if (startMarker != null) {
            startMarker.remove();
        }
        //获取开始位置的经纬度和图标
        startMarker = aMap.addMarker(new MarkerOptions()
                .position(latLng_start).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.start_logo))));
        if (endMarker != null) {
            endMarker.remove();
        }
        endMarker = aMap.addMarker(new MarkerOptions()
                .position(latLng_end).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(), R.drawable.end_logo))));

    }


    /**
     * 点击List后显示的popupMenu菜单
     * 显示导航栏的初始地
     */
    @SuppressLint("RestrictedApi")
    private void showListPopupMenu_MyLocation(View view) {
        //弹出菜单
        PopupMenu popupMenu = new PopupMenu(MapActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.list_menu_my_location, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gmd_my_location:
                        ed_myLocation.setText("光明顶");
                        break;
                    case R.id.yxt_my_location:
                        ed_myLocation.setText("一线天");
                        break;
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });
        popupMenu.show();
    }

    /*
    显示导航栏的目的地
     */
    @SuppressLint("RestrictedApi")
    private void showListPopupMenu_TargetLocation(View view) {
        PopupMenu popupMenu = new PopupMenu(MapActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.list_menu_target_location, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.fls_target_location:
                        ed_targetLocation.setText("飞来石");
                        break;
                    case R.id.bxq_target_location:
                        ed_targetLocation.setText("步仙桥");
                        break;
                }
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                // 控件消失时的事件
            }
        });
        popupMenu.show();
    }

    /**
     * 设置路线规划控件是否可见
     * visible:可见    invisible:不可见，但保留所占有的空间   gone:不可见，不保留空间
     */
    private void setRoadSignVisible() {
        if (isVisible) {   //可见
            road_sign_box.setVisibility(View.VISIBLE);
        } else {
            road_sign_box.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }


    /**
     * 实现客户端与服务端的实时连接，
     * 可以不断的进行数据交流，服务端可以自主的向客户端发送消息，不用客户端每次都进行请求
     */
    private final class MyWebSocketListener extends WebSocketListener {

        /**
         * 当WebSocket和远端服务器建立连接成功后回调
         */
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
        }

        /**
         * 当接受到服务端传来的信息时回调，消息内容为String类型
         */
        @Override
        public void onMessage(WebSocket webSocket, final String text) {
            //根据服务器发送的信息对地图进行实时更新
            Log.d(Tag, text);
        }


        /**
         * 当接受到服务端传来的信息时回调，消息内容为ByteString类型
         */
        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
        }

        /**
         * 当服务端暗示没有数据交互时回调（即此时准备关闭，但连接还没有关闭）
         */
        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(1000, null);
            Log.d(Tag, "正在关闭");
        }

        /**
         * 当连接已经释放的时候被回调
         */
        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            Log.d(Tag, "关闭");
        }

        /**
         * 失败时被回调（包括连接失败，发送失败等）。
         */
        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            Log.d(Tag, "连接失败");
        }
    }

    /**
     * webSocket连接
     */
    private void webSocketConnect() {
        MyWebSocketListener webSocketListener = new MyWebSocketListener();
        Request request = new Request.Builder()
                .url(Consts.RATE_FLOW_URL)
                .build();
        OkHttpClient client = new OkHttpClient();
        webSocket = client.newWebSocket(request, webSocketListener);
    }


    /**
     * 点击事件
     */
    private class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_camera:
                case R.id.iv_camera:
                    Intent intent1 = new Intent(MapActivity.this, ScenicIdentifyActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.tv_raod_sign:
                case R.id.iv_road_sign:
                    setRoadLogo();
                    break;
                case R.id.tv_feedback:
                case R.id.iv_feedback:
                    Intent intent2 = new Intent(MapActivity.this, FeedbackActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.road_sign_target_location:
                    showListPopupMenu_TargetLocation(v);
                    break;
                case R.id.road_sign_my_location:
                    showListPopupMenu_MyLocation(v);
                    break;
                case R.id.road_sign_start_logo:    //开始进行路线规划
                case R.id.road_sign_go:
                    //获取终点的位置
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ed_targetLocation.getWindowToken(), 0);
                    imm.hideSoftInputFromWindow(ed_myLocation.getWindowToken(), 0);
                    targetLocation = ed_targetLocation.getText().toString();
                    myLocation = ed_myLocation.getText().toString();
                    drawLine();
                    break;
                case R.id.tv_indoor_position:
                case R.id.iv_indoor_position:   //室内定位
                    checkPermissions();
                    Intent indoorPositionIntent = new Intent(MapActivity.this, IndoorPositionActivity.class);
                    startActivity(indoorPositionIntent);

                    break;
            }
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
                Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
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

    /**
     * 设置路径规划的各种图标和布局显示情况
     */
    private void setRoadLogo() {
        isVisible = !isVisible;
        setRoadSignVisible();
        //跳转到光明顶
        navigateTo(30.132262, 118.169170);  //光明顶
        switch (end) {
            case "飞来石":
                //启动导航是蓝色路标隐藏，显示红路标
                marker1.setVisible(true);
                break;
            case "步仙桥":
                marker4.setVisible(true);
                break;
        }
        switch (start) {
            case "光明顶":
                //启动导航是蓝色路标隐藏，显示红路标
                marker.setVisible(true);
                break;
            case "一线天":
                marker2.setVisible(true);
                break;
        }
        if (startMarker != null) {
            startMarker.remove();
            startMarker = null;
        }
        if (endMarker != null) {
            endMarker.remove();
            endMarker = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
        //关闭webSocket连接
        webSocket.close(1000, "WebSocket已断开连接。。。");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次重新进入当前的界面时判断路线规划布局是否应该被显示
        setRoadSignVisible();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

}
