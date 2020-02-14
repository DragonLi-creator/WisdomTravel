package com.example.holidaytest4.utils;

import com.example.holidaytest4.R;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取图片的工具类
 */
public class ImageUtil {

    //通过存在数据库的图片名找到相应图片的集合    不可以直接往数据库中加入资源id,因为当有新的文件导入项目中时,资源id可能发生变化
    private static Map<String, Integer> imageMap = new HashMap<>();

    //把图片名称与图片资源id一一对应起来
    static {
        //安徽
        imageMap.put("光明顶", R.drawable.gmt);
        imageMap.put("飞来石", R.drawable.fls);
        imageMap.put("一线天", R.drawable.yxt);
        imageMap.put("迎客松", R.drawable.yks);
        imageMap.put("步仙桥", R.drawable.bxq);
        imageMap.put("中国科学技术大学", R.drawable.zkd);
        imageMap.put("九华山", R.drawable.jus);
        imageMap.put("黄山", R.drawable.gmt);
        imageMap.put("颍上一中", R.drawable.ys);


        //重庆
        imageMap.put("磁器口古镇", R.drawable.ci_qi_kou);
        imageMap.put("解放碑步行街", R.drawable.jie_fang_bei);
        imageMap.put("武隆天生三桥", R.drawable.san_qiao);
        imageMap.put("大足石刻", R.drawable.shi_ke);
        imageMap.put("白公馆", R.drawable.bai_gong_guan);
        imageMap.put("长江索道", R.drawable.suo_dao);
        imageMap.put("南山风景区", R.drawable.nan_shan);
        imageMap.put("白帝城景区", R.drawable.bai_di_cheng);

        //上海
        imageMap.put("外滩", R.drawable.wai_tan);
        imageMap.put("上海迪士尼度假区", R.drawable.di_shi_ni);
        imageMap.put("南京路步行街", R.drawable.nan_jing_lu_bxj);
        imageMap.put("上海长风海洋世界", R.drawable.chang_feng);
        imageMap.put("朱家角古镇景区", R.drawable.zj_jz);
    }

    //获取相应的图片资源id
    public static Integer getImageId(String storeName) {
        //如果集合中有这个图片,则返回其资源id
        if (imageMap.containsKey(storeName)) {
            return imageMap.get(storeName);
        }
        return 0;
    }
}

