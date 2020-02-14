# WisdomTravel
## 1 景智行简介
**这是一款基于高德地图开发的旅游APP，它实现了市面上导航，出游APP中的一些基础功能。（还有很多未能完善）**
## 2 功能以及技术应用
### 2.1 首次权限申请
**在第一次打开APP时，会向用户请求一些危险权限的授权，如手机状态读取，网络等等**
## 3 三大主界面
### 3.1 MineFragment
**这个界面是首次注册/登录界面，利用LitePal数据库使注册信息持久化，方便以后的登录登出。登入后界面顶部是自定义Toolbar，可显示头像和文字，同时也是
另一个活动MyInfoActivity的入口。**
#### 3.11 MyInfoActivity
**显示头像，昵称，账号，手机号，并且可以更改和保存。头像有两种选择，拍照和相册，点击会跳出权限申请，选择的图片会进行裁剪并且保存（裁剪那一块在不同手机上的裁剪框
不一样，有的显示圆形有的是方形，都实验过，可正常使用）**
### 3.2 HomeFragment
**软件打开第一个界面，整体LinearLayout布局，内部嵌套ViewPager用于图片的自动滑动。以及RecyclerView + SwipeRefreshLayout实现的下拉刷新列表，列表中是旅游信息，
点击可跳转到地图界面MapActivity。其次，界面中有搜索框，是基于高德API的POI地区搜索（搜索功能基本完成，可使用）POI搜索结果以RecyclerView显示，点击可进入地图。**
### 3.3 FoundFragment
**整体LinearLayout布局，顶部实现AppBarLayout + Toolbar，下半部分使用ScrollView，内嵌CardView，每一个Card里包含一个地区的景点，可点击，可跳转**
#### 3.31 ViewPointDetailActivity
**此活动为景点的详细介绍界面，内部显示图片，天气，简介。其中天气信息存在我的笔记本中，通过node.js将手机与笔记本建立联系，在同一局域网中手机可访问
电脑上的数据以更新景点天气信息（自己写的一份JSON格式的文件，让APP结合okhttp3访问后以json格式解析并存储SharedPreferences）**

**三大主界面实现了实现多页滑动效果：ViewPager + Fragment + TabLayout 每一界面显示在ViewPager中**

## 4地图MapActivity
### 4.1侧滑栏DrawerLayout + NavigationView 
**整体布局为DrawerLayout，主布局为com.amap.api.maps.MapView显示高德地图，侧滑栏向左滑即可显现，包含四个活动**
### 4.2 拍照识别，室内定位
**调用摄像机继续拍照上传，有加载效果结合AlertDialog使用（功能不完全，目前只有加载效果），室内定位会申请蓝牙权限，但并无实际功能，会显示一张图和一个小动画
结合AnimatorSet使用**
### 4.3 反馈FeedbackActivity
**对景区的反馈，评价等等，填写后上传（未实现）**
### 4.4 路线规划
**参考高德API，但并非是真正的导航，是我记录经纬度以及调用polyline画线，只画了4条路线**
![1]()
