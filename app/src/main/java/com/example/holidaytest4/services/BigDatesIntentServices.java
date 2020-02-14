package com.example.holidaytest4.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.Nullable;
import com.example.holidaytest4.consts.Consts;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 启动app后在后台直接向服务器请求大数据
 */
public class BigDatesIntentServices extends IntentService {
    public BigDatesIntentServices(String name) {
        super(name);
    }

    public BigDatesIntentServices() {
        super("BigDatesIntentServices");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(5, TimeUnit.SECONDS)//设置连接超时时间
                .readTimeout(2, TimeUnit.MINUTES)
                .build();
        Request request_cq = new Request.Builder().url(Consts.BIG_DATA_SERVER_URL_CQ).build();
        Request request_sh = new Request.Builder().url(Consts.BIG_DATA_SERVER_URL_SH).build();
        Request request_ah = new Request.Builder().url(Consts.BIG_DATA_SERVER_URL_AH).build();
        getBigDate(client, request_cq, 1);
        getBigDate(client, request_sh, 2);
        getBigDate(client, request_ah, 3);
    }

    private void getBigDate(OkHttpClient client, Request request, int code) {
        //真机访问电脑本机地址
        //发送请求并获取服务器返回的数据
        try {
            //  温度/湿度/能见度/降雨情况（0为不降雨1为降雨）
            Response response = client.newCall(request).execute();
            if (response.body() != null) {
                String responseData = response.body().string();
                switch (code) {
                    case 1:
                        Log.d("重庆：", "responseData:" + responseData);
                        saveDates(responseData, 1);
                        break;
                    case 2:
                        Log.d("上海：", "responseData:" + responseData);
                        saveDates(responseData, 2);
                        break;
                    case 3:
                        Log.d("安徽：", "responseData:" + responseData);
                        saveDates(responseData, 3);
                        break;
                }
            } else {
                Log.d("response", "请求失败！");
            }
        } catch (Exception e) {
            Log.d("Exception", "连接失败!!");
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("chenyisheng", "IntentService onDestroy!!");
    }

    /**
     * 保存从服务器返回的天气数据
     * 温度/湿度/能见度/降雨情况（0为不降雨1为降雨）
     * 今天                                              明日
     * 24/82/914/11584/1，24/82/914/11584/1，24/82/914/11584/1；24/82/914/11584/1，24/82/914/11584/1，24/82/914/11584/1
     */
    private void saveDates(String responseData, int code) throws JSONException {
        //第一个参数是存储时的名称，第二个参数则是文件的打开方式，只允许被自己的应用访问
        SharedPreferences.Editor editor = getSharedPreferences("BigDates", MODE_PRIVATE).edit();

        switch (code) {
            case 1:
                putCQ(responseData, editor);
                break;
            case 2:
                //putSHJSON(responseData,editor);
                putSH(responseData, editor);
                break;
            case 3:
                putAHJSON(responseData,editor);
                //putAH(responseData, editor);
                //putAHJSON1(responseData,editor);
                break;
        }
        editor.apply();
    }

    private void putAHJSON(String responseData, SharedPreferences.Editor editor) throws JSONException {
        JSONObject ah = new JSONObject(responseData);
        JSONObject zkd = ah.getJSONObject("中科大今");
        JSONObject ys = ah.getJSONObject("颍上一中今");
        JSONObject jhs = ah.getJSONObject("九华山今");
        JSONObject hs = ah.getJSONObject("黄山今");

        JSONObject zkd_t = ah.getJSONObject("中科大明");
        JSONObject ys_t= ah.getJSONObject("颍上一中明");
        JSONObject jhs_t = ah.getJSONObject("九华山明");
        JSONObject hs_t = ah.getJSONObject("黄山明");

        String[] dates_zkd = {zkd.getString("temperature_ah_zkd"),
                zkd.getString("humidity_ah_zkd"),
                zkd.getString("visibility_ah_zkd"),
                zkd.getString("rainfall_ah_zkd")};
        String[] dates_ys = {ys.getString("temperature_ah_ys"),
                ys.getString("humidity_ah_ys"),
                ys.getString("visibility_ah_ys"),
                ys.getString("rainfall_ah_ys")};
        String[] dates_jhs = {jhs.getString("temperature_ah_jhs"),
                jhs.getString("humidity_ah_jhs"),
                jhs.getString("visibility_ah_jhs"),
                jhs.getString("rainfall_ah_jhs")};
        String[] dates_hs = {hs.getString("temperature_ah_hs"),
                hs.getString("humidity_ah_hs"),
                hs.getString("visibility_ah_hs"),
                hs.getString("rainfall_ah_hs")};

        //Toast.makeText(BigDatesIntentServices.this,dates_zkd[0],Toast.LENGTH_SHORT).show();

        editor.putString("temperature_ah_zkd", dates_zkd[0]);
        editor.putString("humidity_ah_zkd", dates_zkd[1]);
        editor.putString("visibility_ah_zkd", dates_zkd[2]);
        editor.putString("rainfall_ah_zkd", dates_zkd[3]);

        editor.putString("temperature_ah_ys", dates_ys[0]);
        editor.putString("humidity_ah_ys", dates_ys[1]);
        editor.putString("visibility_ah_ys", dates_ys[2]);
        editor.putString("rainfall_ah_ys", dates_ys[3]);

        editor.putString("temperature_ah_jhs", dates_jhs[0]);
        editor.putString("humidity_ah_jhs", dates_jhs[1]);
        editor.putString("visibility_ah_jhs", dates_jhs[2]);
        editor.putString("rainfall_ah_jhs", dates_jhs[3]);

        editor.putString("temperature_ah_hs", dates_hs[0]);
        editor.putString("humidity_ah_hs", dates_hs[1]);
        editor.putString("visibility_ah_hs", dates_hs[2]);
        editor.putString("rainfall_ah_hs", dates_hs[3]);

        String[] dates_zkd_t = {zkd_t.getString("temperature_ah_zkd_t"),
                zkd_t.getString("humidity_ah_zkd_t"),
                zkd_t.getString("visibility_ah_zkd_t"),
                zkd_t.getString("rainfall_ah_zkd_t")};
        String[] dates_ys_t = {ys_t.getString("temperature_ah_ys_t"),
                ys_t.getString("humidity_ah_ys_t"),
                ys_t.getString("visibility_ah_ys_t"),
                ys_t.getString("rainfall_ah_ys_t")};
        String[] dates_jhs_t = {jhs_t.getString("temperature_ah_jhs_t"),
                jhs_t.getString("humidity_ah_jhs_t"),
                jhs_t.getString("visibility_ah_jhs_t"),
                jhs_t.getString("rainfall_ah_jhs_t")};
        String[] dates_hs_t = {hs_t.getString("temperature_ah_hs_t"),
                hs_t.getString("humidity_ah_hs_t"),
                hs_t.getString("visibility_ah_hs_t"),
                hs_t.getString("rainfall_ah_hs_t")};

        editor.putString("temperature_ah_zkd_t", dates_zkd_t[0]);
        editor.putString("humidity_ah_zkd_t", dates_zkd_t[1]);
        editor.putString("visibility_ah_zkd_t", dates_zkd_t[2]);
        editor.putString("rainfall_ah_zkd_t", dates_zkd_t[3]);

        editor.putString("temperature_ah_ys_t", dates_ys_t[0]);
        editor.putString("humidity_ah_ys_t", dates_ys_t[1]);
        editor.putString("visibility_ah_ys_t", dates_ys_t[2]);
        editor.putString("rainfall_ah_ys_t", dates_ys_t[3]);

        editor.putString("temperature_ah_jhs_t", dates_jhs_t[0]);
        editor.putString("humidity_ah_jhs_t", dates_jhs_t[1]);
        editor.putString("visibility_ah_jhs_t", dates_jhs_t[2]);
        editor.putString("rainfall_ah_jhs_t", dates_jhs_t[3]);

        editor.putString("temperature_ah_hs_t", dates_hs_t[0]);
        editor.putString("humidity_ah_hs_t", dates_hs_t[1]);
        editor.putString("visibility_ah_hs_t", dates_hs_t[2]);
        editor.putString("rainfall_ah_hs_t", dates_hs_t[3]);

    }
    private void putAH (String responseData, SharedPreferences.Editor editor){
        String[] dates = responseData.split(";");
        //今日 安徽
        String[] dates_today = dates[0].split(",");
        String[] dates_zkd = dates_today[0].split("/");      //中科大
        String[] dates_ys = dates_today[1].split("/");      //颍上一中
        String[] dates_jhs = dates_today[2].split("/");      //九华山
        String[] dates_hs = dates_today[3].split("/");      //黄山

        editor.putString("temperature_ah_zkd", dates_zkd[0]);
        editor.putString("humidity_ah_zkd", dates_zkd[1]);
        editor.putString("visibility_ah_zkd", dates_zkd[2]);
        editor.putString("rainfall_ah_zkd", dates_zkd[3]);

        editor.putString("temperature_ah_ys", dates_ys[0]);
        editor.putString("humidity_ah_ys", dates_ys[1]);
        editor.putString("visibility_ah_ys", dates_ys[2]);
        editor.putString("rainfall_ah_ys", dates_ys[3]);

        editor.putString("temperature_ah_jhs", dates_jhs[0]);
        editor.putString("humidity_ah_jhs", dates_jhs[1]);
        editor.putString("visibility_ah_jhs", dates_jhs[2]);
        editor.putString("rainfall_ah_jhs", dates_jhs[3]);

        editor.putString("temperature_ah_hs", dates_hs[0]);
        editor.putString("humidity_ah_hs", dates_hs[1]);
        editor.putString("visibility_ah_hs", dates_hs[2]);
        editor.putString("rainfall_ah_hs", dates_hs[3]);

        String[] dates_tomorrow = dates[1].split(",");
        String[] dates_zkd_t = dates_tomorrow[0].split("/");      //中科大
        String[] dates_ys_t = dates_tomorrow[1].split("/");      //颍上一中
        String[] dates_jhs_t = dates_tomorrow[2].split("/");      //九华山
        String[] dates_hs_t = dates_tomorrow[3].split("/");      //黄山

        editor.putString("temperature_ah_zkd_t", dates_zkd_t[0]);
        editor.putString("humidity_ah_zkd_t", dates_zkd_t[1]);
        editor.putString("visibility_ah_zkd_t", dates_zkd_t[2]);
        editor.putString("rainfall_ah_zkd_t", dates_zkd_t[3]);

        editor.putString("temperature_ah_ys_t", dates_ys_t[0]);
        editor.putString("humidity_ah_ys_t", dates_ys_t[1]);
        editor.putString("visibility_ah_ys_t", dates_ys_t[2]);
        editor.putString("rainfall_ah_ys_t", dates_ys_t[3]);

        editor.putString("temperature_ah_jhs_t", dates_jhs_t[0]);
        editor.putString("humidity_ah_jhs_t", dates_jhs_t[1]);
        editor.putString("visibility_ah_jhs_t", dates_jhs_t[2]);
        editor.putString("rainfall_ah_jhs_t", dates_jhs_t[3]);

        editor.putString("temperature_ah_hs_t", dates_hs_t[0]);
        editor.putString("humidity_ah_hs_t", dates_hs_t[1]);
        editor.putString("visibility_ah_hs_t", dates_hs_t[2]);
        editor.putString("rainfall_ah_hs_t", dates_hs_t[3]);

    }
    private void putCQ(String responseData, SharedPreferences.Editor editor) {
        String[] dates = responseData.split(";");
        //今日 重庆
        String[] dates_today = dates[0].split(",");
        String[] dates_sq = dates_today[0].split("/");      //重庆市区
        String[] dates_dz = dates_today[1].split("/");      //大足区
        String[] dates_wl = dates_today[2].split("/");      //武隆区
        String[] dates_fj = dates_today[3].split("/");      //奉节区

        editor.putString("temperature_cq_sq", dates_sq[0]);
        editor.putString("humidity_cq_sq", dates_sq[1]);
        editor.putString("visibility_cq_sq", dates_sq[2]);
        editor.putString("rainfall_cq_sq", dates_sq[3]);

        editor.putString("temperature_cq_dz", dates_dz[0]);
        editor.putString("humidity_cq_dz", dates_dz[1]);
        editor.putString("visibility_cq_dz", dates_dz[2]);
        editor.putString("rainfall_cq_dz", dates_dz[3]);

        editor.putString("temperature_cq_wl", dates_wl[0]);
        editor.putString("humidity_cq_wl", dates_wl[1]);
        editor.putString("visibility_cq_wl", dates_wl[2]);
        editor.putString("rainfall_cq_wl", dates_wl[3]);

        editor.putString("temperature_cq_fj", dates_fj[0]);
        editor.putString("humidity_cq_fj", dates_fj[1]);
        editor.putString("visibility_cq_fj", dates_fj[2]);
        editor.putString("rainfall_cq_fj", dates_fj[3]);

        //明日 重庆
        String[] dates_tomorrow = dates[1].split(",");
        String[] dates_sq_t = dates_tomorrow[0].split("/");      //重庆市区
        String[] dates_dz_t = dates_tomorrow[1].split("/");      //大足区
        String[] dates_wl_t = dates_tomorrow[2].split("/");      //武隆区
        String[] dates_fj_t = dates_tomorrow[3].split("/");      //奉节区

        editor.putString("temperature_cq_sq_t", dates_sq_t[0]);
        editor.putString("humidity_cq_sq_t", dates_sq_t[1]);
        editor.putString("visibility_cq_sq_t", dates_sq_t[2]);
        editor.putString("rainfall_cq_sq_t", dates_sq_t[3]);

        editor.putString("temperature_cq_dz_t", dates_dz_t[0]);
        editor.putString("humidity_cq_dz_t", dates_dz_t[1]);
        editor.putString("visibility_cq_dz_t", dates_dz_t[2]);
        editor.putString("rainfall_cq_dz_t", dates_dz_t[3]);

        editor.putString("temperature_cq_wl_t", dates_wl_t[0]);
        editor.putString("humidity_cq_wl_t", dates_wl_t[1]);
        editor.putString("visibility_cq_wl_t", dates_wl_t[2]);
        editor.putString("rainfall_cq_wl_t", dates_wl_t[3]);

        editor.putString("temperature_cq_fj_t", dates_fj_t[0]);
        editor.putString("humidity_cq_fj_t", dates_fj_t[1]);
        editor.putString("visibility_cq_fj_t", dates_fj_t[2]);
        editor.putString("rainfall_cq_fj_t", dates_fj_t[3]);
    }
    private void putSHJSON(String responseData, SharedPreferences.Editor editor) throws JSONException {
        JSONObject sh = new JSONObject(responseData);
        JSONObject sq = sh.getJSONObject("上海市区今");
        JSONObject dz = sh.getJSONObject("浦东新区今");
        JSONObject wl = sh.getJSONObject("上海区今");
        JSONObject fj = sh.getJSONObject("青浦今");

        JSONObject sq_t = sh.getJSONObject("上海市区明");
        JSONObject dz_t= sh.getJSONObject("浦东新区明");
        JSONObject wl_t = sh.getJSONObject("上海区明");
        JSONObject fj_t = sh.getJSONObject("青浦明");

        String[] dates_sq = {sq.getString("temperature_sh_sq1"),
                sq.getString("humidity_sh_sq1"),
                sq.getString("visibility_sh_sq1"),
                sq.getString("rainfall_sh_sq1")};
        String[] dates_dz = {dz.getString("temperature_sh_pd"),
                dz.getString("humidity_sh_pd"),
                dz.getString("visibility_sh_pd"),
                dz.getString("rainfall_sh_pd")};
        String[] dates_wl = {wl.getString("temperature_sh_sq2"),
                wl.getString("humidity_sh_sq2"),
                wl.getString("visibility_sh_sq2"),
                wl.getString("rainfall_sh_sq2")};
        String[] dates_fj = {fj.getString("temperature_sh_qp"),
                fj.getString("humidity_sh_qp"),
                fj.getString("visibility_sh_qp"),
                fj.getString("rainfall_sh_qp")};

        editor.putString("temperature_sh_sq1", dates_sq[0]);
        editor.putString("humidity_sh_sq1", dates_sq[1]);
        editor.putString("visibility_sh_sq1", dates_sq[2]);
        editor.putString("rainfall_sh_sq1", dates_sq[3]);

        editor.putString("temperature_sh_pd", dates_dz[0]);
        editor.putString("humidity_sh_pd", dates_dz[1]);
        editor.putString("visibility_sh_pd", dates_dz[2]);
        editor.putString("rainfall_sh_pd", dates_dz[3]);

        editor.putString("temperature_sh_sq2", dates_wl[0]);
        editor.putString("humidity_sh_sq2", dates_wl[1]);
        editor.putString("visibility_sh_sq2", dates_wl[2]);
        editor.putString("rainfall_sh_sq2", dates_wl[3]);

        editor.putString("temperature_sh_qp", dates_fj[0]);
        editor.putString("humidity_sh_qp", dates_fj[1]);
        editor.putString("visibility_sh_qp", dates_fj[2]);
        editor.putString("rainfall_sh_qp", dates_fj[3]);

        String[] dates_sq_t = {sq_t.getString("temperature_sh_sq1_t"),
                sq_t.getString("humidity_sh_sq1_t"),
                sq_t.getString("visibility_sh_sq1_t"),
                sq_t.getString("rainfall_sh_sq1_t")};
        String[] dates_dz_t = {dz_t.getString("temperature_sh_pd_t"),
                dz_t.getString("humidity_sh_pd_t"),
                dz_t.getString("visibility_sh_pd_t"),
                dz_t.getString("rainfall_sh_pd_t")};
        String[] dates_wl_t = {wl_t.getString("temperature_sh_sq2_t"),
                wl_t.getString("humidity_sh_sq2_t"),
                wl_t.getString("visibility_sh_sq2_t"),
                wl_t.getString("rainfall_sh_sq2_t")};
        String[] dates_fj_t = {fj_t.getString("temperature_sh_qp_t"),
                fj_t.getString("humidity_sh_qp_t"),
                fj_t.getString("visibility_sh_qp_t"),
                fj_t.getString("rainfall_sh_qp_t")};

        editor.putString("temperature_sh_sq1_t", dates_sq_t[0]);
        editor.putString("humidity_sh_sq1_t", dates_sq_t[1]);
        editor.putString("visibility_sh_sq1_t", dates_sq_t[2]);
        editor.putString("rainfall_sh_sq1_t", dates_sq_t[3]);

        editor.putString("temperature_sh_pd_t", dates_dz_t[0]);
        editor.putString("humidity_sh_pd_t", dates_dz_t[1]);
        editor.putString("visibility_sh_pd_t", dates_dz_t[2]);
        editor.putString("rainfall_sh_pd_t", dates_dz_t[3]);

        editor.putString("temperature_sh_sq2_t", dates_wl_t[0]);
        editor.putString("humidity_sh_sq2_t", dates_wl_t[1]);
        editor.putString("visibility_sh_sq2_t", dates_wl_t[2]);
        editor.putString("rainfall_sh_sq2_t", dates_wl_t[3]);

        editor.putString("temperature_sh_qp_t", dates_fj_t[0]);
        editor.putString("humidity_sh_qp_t", dates_fj_t[1]);
        editor.putString("visibility_sh_qp_t", dates_fj_t[2]);
        editor.putString("rainfall_sh_qp_t", dates_fj_t[3]);

    }

    private void putSH(String responseData, SharedPreferences.Editor editor) {
        String[] dates = responseData.split(";");
        //今日 上海
        String[] dates_today = dates[0].split(",");
        String[] dates_sq = dates_today[0].split("/");      //上海市区
        String[] dates_dz = dates_today[1].split("/");      //浦东新区
        String[] dates_wl = dates_today[2].split("/");      //上海区
        String[] dates_fj = dates_today[3].split("/");      //青浦区

        editor.putString("temperature_sh_sq1", dates_sq[0]);
        editor.putString("humidity_sh_sq1", dates_sq[1]);
        editor.putString("visibility_sh_sq1", dates_sq[2]);
        editor.putString("rainfall_sh_sq1", dates_sq[3]);

        editor.putString("temperature_sh_pd", dates_dz[0]);
        editor.putString("humidity_sh_pd", dates_dz[1]);
        editor.putString("visibility_sh_pd", dates_dz[2]);
        editor.putString("rainfall_sh_pd", dates_dz[3]);

        editor.putString("temperature_sh_sq2", dates_wl[0]);
        editor.putString("humidity_sh_sq2", dates_wl[1]);
        editor.putString("visibility_sh_sq2", dates_wl[2]);
        editor.putString("rainfall_sh_sq2", dates_wl[3]);

        editor.putString("temperature_sh_qp", dates_fj[0]);
        editor.putString("humidity_sh_qp", dates_fj[1]);
        editor.putString("visibility_sh_qp", dates_fj[2]);
        editor.putString("rainfall_sh_qp", dates_fj[3]);

        //明日 上海
        String[] dates_tomorrow = dates[1].split(",");
        String[] dates_sq_t = dates_tomorrow[0].split("/");      //上海市区
        String[] dates_dz_t = dates_tomorrow[1].split("/");      //浦东新区
        String[] dates_wl_t = dates_tomorrow[2].split("/");      //上海市区
        String[] dates_fj_t = dates_tomorrow[3].split("/");      //青浦区

        editor.putString("temperature_sh_sq1_t", dates_sq_t[0]);
        editor.putString("humidity_sh_sq1_t", dates_sq_t[1]);
        editor.putString("visibility_sh_sq1_t", dates_sq_t[2]);
        editor.putString("rainfall_sh_sq1_t", dates_sq_t[3]);

        editor.putString("temperature_sh_pd_t", dates_dz_t[0]);
        editor.putString("humidity_sh_pd_t", dates_dz_t[1]);
        editor.putString("visibility_sh_pd_t", dates_dz_t[2]);
        editor.putString("rainfall_sh_pd_t", dates_dz_t[3]);

        editor.putString("temperature_sh_sq2_t", dates_wl_t[0]);
        editor.putString("humidity_sh_sq2_t", dates_wl_t[1]);
        editor.putString("visibility_sh_sq2_t", dates_wl_t[2]);
        editor.putString("rainfall_sh_sq2_t", dates_wl_t[3]);

        editor.putString("temperature_sh_qp_t", dates_fj_t[0]);
        editor.putString("humidity_sh_qp_t", dates_fj_t[1]);
        editor.putString("visibility_sh_qp_t", dates_fj_t[2]);
        editor.putString("rainfall_sh_qp_t", dates_fj_t[3]);
    }
}