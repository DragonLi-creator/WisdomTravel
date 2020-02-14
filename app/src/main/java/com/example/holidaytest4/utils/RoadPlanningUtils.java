package com.example.holidaytest4.utils;

import com.amap.api.maps.model.LatLng;
import java.util.List;

/**
 * 路径规划类
 */
public class RoadPlanningUtils {

    //1 醉乡-->普贤塔
    public static final int ROAD_1 = 1;     // 途经普贤塔
    public static final int ROAD_2 = 2;     // 途经象眼岩
    public static final int ROAD_3 = 3;     // 途经桂林抗战遗址

    //2 醉乡-->象眼岩
    public static final int ROAD_4 = 4;     // 途经普贤塔

    /**
     * 1 光明顶-->飞来石
     */
    //途经普贤塔
    public static void gmdToFls(List<LatLng> latLngs) {
        //                             增加往上       增加往右
        LatLng latLng0 = new LatLng(30.132262, 118.169170);       //醉乡
        LatLng latLng1 = new LatLng(30.132335, 118.168721);       //地标1
        LatLng latLng2 = new LatLng(30.132131, 118.168544);       //地标2
        LatLng latLng3 = new LatLng(30.132326, 118.167895);       //地标3
        LatLng latLng4 = new LatLng(30.132525, 118.167627);       //地标3
        LatLng latLng5 = new LatLng(30.133022, 118.166683);       //地标4
        LatLng latLng6 = new LatLng(30.133076, 118.165865);       //地标5
        LatLng latLng7 = new LatLng(30.133173, 118.165642);       //地标6
        LatLng latLng8 = new LatLng(30.133206, 118.165254);       //地标7
        LatLng latLng9 = new LatLng(30.133684, 118.16495);       //地标8
        LatLng latLng10 = new LatLng(30.135566, 118.163966);      //普贤塔

        latLngs.add(latLng0);
        latLngs.add(latLng1);
        latLngs.add(latLng2);
        latLngs.add(latLng3);
        latLngs.add(latLng4);
        latLngs.add(latLng5);
        latLngs.add(latLng6);
        latLngs.add(latLng7);
        latLngs.add(latLng8);
        latLngs.add(latLng9);
        latLngs.add(latLng10);
    }


    /**
     *  光明顶-->步仙桥
     */
    public static void gmdToBxq(List<LatLng> latLngs) {
        //                            增加往上       增加往右
        LatLng latLng1 = new LatLng(30.132262, 118.169170);      //醉乡
        LatLng latLng2 = new LatLng(30.132134, 118.168548);      //地标2
        LatLng latLng3 = new LatLng(30.131224, 118.169497);      //地标3
        LatLng latLng4 = new LatLng(30.129991, 118.168456);      //地标4
        LatLng latLng5 = new LatLng(30.130775, 118.166332);      //地标5
        LatLng latLng6 = new LatLng(30.131642, 118.164149);      //地标6
        LatLng latLng7 = new LatLng(30.132027, 118.161901);      //地标7
        LatLng latLng8 = new LatLng(30.131698, 118.160877);      //地标8
        LatLng latLng9 = new LatLng(30.131698, 118.160877);      //地标9
        LatLng latLng10 = new LatLng(30.131545, 118.158205);     //地标10
        LatLng latLng11 = new LatLng(30.131893, 118.155673);     //地标11
        LatLng latLng12 = new LatLng(30.131766, 118.155638);     //步仙桥
        latLngs.add(latLng1);
        latLngs.add(latLng2);
        latLngs.add(latLng3);
        latLngs.add(latLng4);
        latLngs.add(latLng5);
        latLngs.add(latLng6);
        latLngs.add(latLng7);
        latLngs.add(latLng8);
        latLngs.add(latLng9);
        latLngs.add(latLng10);
        latLngs.add(latLng11);
        latLngs.add(latLng12);
    }

    /**
     *  一线天-->飞来石
     * @param latLngs
     */
    public static void yxtToFls(List<LatLng> latLngs) {
        //                             增加往上       增加往右
        LatLng latLng1 = new LatLng(30.126444, 118.169330);         //一线天
        LatLng latLng2 = new LatLng(30.127499, 118.167111);
        LatLng latLng3 = new LatLng(30.129244, 118.166981);
        LatLng latLng4 = new LatLng(30.129494, 118.167829);
        LatLng latLng5 = new LatLng(30.131285, 118.169481);
        LatLng latLng6 = new LatLng(30.131285, 118.169481);
        LatLng latLng7 = new LatLng(30.132326, 118.167895);
        LatLng latLng8 = new LatLng(30.132525, 118.167627);
        LatLng latLng9 = new LatLng(30.133022, 118.166683);
        LatLng latLng10 = new LatLng(30.133076, 118.165865);
        LatLng latLng11 = new LatLng(30.133173, 118.165642);
        LatLng latLng12 = new LatLng(30.133206, 118.165254);
        LatLng latLng13 = new LatLng(30.133684, 118.16495);
        LatLng latLng14 = new LatLng(30.135566, 118.163966);       //飞来石
        latLngs.add(latLng1);
        latLngs.add(latLng2);
        latLngs.add(latLng3);
        latLngs.add(latLng4);
        latLngs.add(latLng5);
        latLngs.add(latLng6);
        latLngs.add(latLng7);
        latLngs.add(latLng8);
        latLngs.add(latLng9);
        latLngs.add(latLng10);
        latLngs.add(latLng11);
        latLngs.add(latLng12);
        latLngs.add(latLng13);
        latLngs.add(latLng14);
    }

    /**
     * 2 一线天-->步仙桥
     */
    public static void yxtToBxq(List<LatLng> latLngs) {
        //                             增加往上       增加往右
        LatLng latLng1 = new LatLng(30.126444, 118.169330);      //一线天
        LatLng latLng2 = new LatLng(30.127499, 118.167111);
        LatLng latLng3 = new LatLng(30.129244, 118.166981);
        LatLng latLng5 = new LatLng(30.129991, 118.168456);
        LatLng latLng6 = new LatLng(30.130775, 118.166332);
        LatLng latLng7 = new LatLng(30.131642, 118.164149);
        LatLng latLng8 = new LatLng(30.132027, 118.161901);
        LatLng latLng9 = new LatLng(30.131698, 118.160877);
        LatLng latLng10 = new LatLng(30.131698, 118.160877);
        LatLng latLng11 = new LatLng(30.131545, 118.158205);
        LatLng latLng12 = new LatLng(30.131893, 118.155673);
        LatLng latLng13 = new LatLng(30.131766, 118.155638);    //步仙桥

        latLngs.add(latLng1);
        latLngs.add(latLng2);
        latLngs.add(latLng3);
        latLngs.add(latLng5);
        latLngs.add(latLng6);
        latLngs.add(latLng7);
        latLngs.add(latLng8);
        latLngs.add(latLng9);
        latLngs.add(latLng10);
        latLngs.add(latLng11);
        latLngs.add(latLng12);
        latLngs.add(latLng13);
    }
}