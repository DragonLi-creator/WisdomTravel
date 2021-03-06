package com.example.holidaytest4.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import com.example.holidaytest4.R;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.holidaytest4.adapter.ItemClickListener;
import com.example.holidaytest4.adapter.SearchAdapter;
import com.example.holidaytest4.adapter.SearchHistoryAdapter;
import com.example.holidaytest4.beans.SearchHistoryInfo;
import com.example.holidaytest4.utils.UiUtils;
import org.litepal.LitePal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener, SearchView.OnQueryTextListener {

    private RecyclerView recyclerView;
    //POI搜索
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    //poi搜索数据
    private ArrayList<PoiItem> poiItems;
    //历史搜索数据
    private ArrayList<SearchHistoryInfo> historyInfos;
    //历史搜索适配器
    private SearchHistoryAdapter historyAdapter;
    //历史搜索时间比较器
    private HistoryInfoDateComparator historyInfoDateComparator;
    //历史记录item点击接口
    private ItemClickListener mItemClickListener;
    private TextView tv_search_empty;
    //弹出对话框
    private AlertDialog deteleDialog;
    private AlertDialog deteleAllDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        initView();
        initHistoryAdapter();
    }

    /**
     * 初始化View
     */
    private void initView() {
        //设置toolbar
        Toolbar toolbar = findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        //找到RecyclerView控件
        recyclerView = findViewById(R.id.recyclerView_search);
        //用来指定RecycleView的布局方式
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);//1 表示列数
        recyclerView.setLayoutManager(layoutManager);
        //显示空的历史记录
        tv_search_empty = findViewById(R.id.tv_search_empty);
        tv_search_empty.setVisibility(View.GONE);
        //返回键
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //将状态栏字体变为黑色
        UiUtils.changeStatusBarTextImgColor(this, true);
    }

    /**
     * 获取历史搜索记录
     */
    private void initHistoryData() {
        //初始化历史记录列表
        if (historyInfos == null) {
            historyInfos = new ArrayList<>();
        }
        if (historyInfoDateComparator == null) {
            historyInfoDateComparator = new HistoryInfoDateComparator();
        }
        //从数据库中获取数据
        historyInfos = (ArrayList<SearchHistoryInfo>) LitePal.findAll(SearchHistoryInfo.class);
        //没有历史记录时,不显示删除全部历史记录信息
        if (historyInfos.isEmpty()) {
            tv_search_empty.setVisibility(View.VISIBLE);
        } else {
            tv_search_empty.setVisibility(View.GONE);
            //按时间顺序排序
            Collections.sort(historyInfos, historyInfoDateComparator);
        }
    }

    /**
     * 设置历史搜索适配器
     */
    private void initHistoryAdapter() {
        initHistoryData();
        historyAdapter = new SearchHistoryAdapter(historyInfos);
        if (mItemClickListener == null) {
            mItemClickListener = new MyItemClickListener();
        }
        historyAdapter.setItemClickListener(mItemClickListener);
        //为recyclerView添加适配器
        recyclerView.setAdapter(historyAdapter);
    }

    /**
     * 搜索成功后进行数据显示,设置适配器
     */
    private void initSearcherAdapter(ArrayList<PoiItem> poiItems) {
        //若未搜索成功，则显示“无”，否，则不显示，直接显示成功列表
        if (poiItems.isEmpty()) {
            tv_search_empty.setVisibility(View.VISIBLE);
        } else {
            tv_search_empty.setVisibility(View.GONE);
        }
        SearchAdapter searchAdapter = new SearchAdapter(poiItems);
        searchAdapter.setItemClickListener(mItemClickListener);
        recyclerView.setAdapter(searchAdapter);
    }

    /**
     * 寻找兴趣点,高德开发平台
     */
    private void searchPOI(String keyWord, String cityCode) {
        query = new PoiSearch.Query(keyWord, "", cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        //构造 PoiSearch 对象，并设置监听。
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        //发送请求。
        poiSearch.searchPOIAsyn();
    }

    /**
     * 寻找兴趣点的回调方法
     */
    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        if (1000 == i) {   //搜索成功
            //获取搜索到的数据
            poiItems = poiResult.getPois();
            initSearcherAdapter(poiItems);
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    //反射菜单布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        //获取search的item对象
        MenuItem search = menu.findItem(R.id.search_toolbar);//搜索框外的menu对象
        SearchView searchView = (SearchView) search.getActionView();//SearchView对象
        //找到SearchView中Text中的控件对象
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextSize(14);
        // 直接展开搜索框,且当没有输入时右边没有×,有输入时才有
        searchView.onActionViewExpanded();
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("搜索");
        return true;
    }

    /**
     * 当用户提交搜索结果时激活该方法
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        //开始进行用户兴趣点搜索
        searchPOI(query, "");
        return true;
    }

    /**
     * 增加一条历史记录
     */
    private void addHistory(String title, double latitude, double longitude) {
        //先查看数据库中是否已经有相同的记录
        List<SearchHistoryInfo> list = LitePal.select("title").find(SearchHistoryInfo.class);
        for (SearchHistoryInfo historyInfo : list) {
            if (historyInfo.getTitle().equals(title)) {    //如果搜索重复
                historyInfo.setDate(System.currentTimeMillis());   //更新搜索时间
                historyInfo.save();
                return;   //结束
            }
        }
        //添加记录
        SearchHistoryInfo historyInfo = new SearchHistoryInfo();
        historyInfo.setTitle(title);
        historyInfo.setDate(System.currentTimeMillis());
        historyInfo.setLatitude(latitude);
        historyInfo.setLongitude(longitude);
        historyInfo.save();
    }

    /**
     * 用户输入字符时激发该方法
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            initHistoryAdapter();
        }
        return true;
    }

    /**
     * recyclerView的item点击事件
     */
    class MyItemClickListener implements ItemClickListener {

        @Override
        public void onItemClick(View view, int position) {
            switch (view.getId()) {
                case R.id.view_search_adapter: {    //点击搜索到的地点
                    //获取当前item
                    PoiItem poiItem = poiItems.get(position);
                    double latitude = poiItem.getLatLonPoint().getLatitude();
                    double longitude = poiItem.getLatLonPoint().getLongitude();
                    //新增一条历史记录
                    addHistory(poiItem.getTitle(), latitude, longitude);
                    //回传经纬度给高德地图进行显示
                    Intent intent = new Intent(SearchActivity.this, MapActivity.class);
                    intent.putExtra("Latitude", latitude);
                    intent.putExtra("Longitude", longitude);
                    Log.d("经纬度：", latitude + "," + longitude);
                    startActivity(intent);
                    finish();
                    break;
                }
                case R.id.view_history_adapter_item: {    //点击历史搜索的地点
                    SearchHistoryInfo historyInfo = historyInfos.get(position);
                    Intent intent = new Intent(SearchActivity.this, MapActivity.class);
                    intent.putExtra("Latitude", historyInfo.getLatitude());
                    intent.putExtra("Longitude", historyInfo.getLongitude());
                    startActivity(intent);
                    finish();
                    break;
                }

            }
        }

        @Override
        public void onLongClick(View view, int position) {
            initLongClickSelectBox(position);
        }

        @Override
        public void onItemClick(int i) {

        }

        @Override
        public void onLongClick(int i) {

        }
    }

    /**
     * 长按item时弹出一个对话框,可进行删除等操作
     */
    private void initLongClickSelectBox(final int position) {
        LayoutInflater inflater = LayoutInflater.from(this);
        //自定义的全新的对话框布局
        View view = inflater.inflate(R.layout.search_one_bottom_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        deteleDialog = builder.create();
        Button delete = view.findViewById(R.id.search_one_dialog_delete);
        Button cancle = view.findViewById(R.id.search_one_dialog_cancel);
        Button deleteAll = view.findViewById(R.id.tv_delete_history);
        delete.setOnClickListener(new MyOnClickListener(position));
        cancle.setOnClickListener(new MyOnClickListener(position));
        deleteAll.setOnClickListener(new MyOnClickListener(position));
        deteleDialog.show();
    }

    /**
     * 删除所有的历史记录
     */
    private void initDeleteAllSelectBox() {
        LayoutInflater inflater = LayoutInflater.from(this);
        //自定义的全新的对话框布局
        View view = inflater.inflate(R.layout.search_all_bottom_dialog_layout, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        deteleAllDialog = builder.create();
        Button deleteAll = view.findViewById(R.id.search_all_dialog_delete);
        Button cancleAll = view.findViewById(R.id.search_all_dialog_cancel);
        deleteAll.setOnClickListener(new MyOnClickListener());
        cancleAll.setOnClickListener(new MyOnClickListener());
        deteleAllDialog.show();
    }

    /**
     * 点击事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        //长按时点击的item位置
        private int position;

        MyOnClickListener(int position) {
            this.position = position;
        }

        MyOnClickListener() {
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_delete_history:
                    //弹出一个删除全部的选择框
                    initDeleteAllSelectBox();
                    deteleDialog.dismiss();
                    break;
                case R.id.search_one_dialog_delete:
                    //删除某一条历史纪录
                    deleteOneHistory(position);
                    deteleDialog.dismiss();
                    break;
                case R.id.search_one_dialog_cancel:
                    deteleDialog.dismiss();
                    break;
                case R.id.search_all_dialog_delete:
                    //删除所有历史纪录
                    deleteAllHistory();
                    deteleAllDialog.dismiss();
                    break;
                case R.id.search_all_dialog_cancel:
                    deteleAllDialog.dismiss();
                    break;
            }
        }
    }

    /**
     * 删除全部历史记录
     */
    private void deleteAllHistory() {
        LitePal.deleteAll(SearchHistoryInfo.class);
        //重新初始化
        initHistoryAdapter();
    }

    /**
     * 删除某一个历史记录
     */
    private void deleteOneHistory(int position) {
        //删除某一个记录
        SearchHistoryInfo historyInfo = historyInfos.get(position);
        LitePal.delete(SearchHistoryInfo.class, historyInfo.getId());
        //重新初始化
        initHistoryAdapter();
    }

    /**
     * 搜索时间比较器
     */
    private class HistoryInfoDateComparator implements Comparator<SearchHistoryInfo> {
        //比较两条搜索记录的时间
        @Override
        public int compare(SearchHistoryInfo info1, SearchHistoryInfo info2) {
            if (info1.getDate() < info2.getDate()) {
                return 1;
            } else if (info1.getDate() == info2.getDate()) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
