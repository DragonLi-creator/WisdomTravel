package com.example.holidaytest4.utils;


public class AStarUtils {
    public static int A_star(int[] numbers, String myLocation, String targetLocation) {
        if (myLocation.equals("光明顶") && targetLocation.equals("飞来石")) {
            return RoadPlanningUtils.ROAD_1;
        }
        if (myLocation.equals("光明顶") && targetLocation.equals("步仙桥")) {
            return RoadPlanningUtils.ROAD_2;
        }
        if (myLocation.equals("一线天") && targetLocation.equals("飞来石")) {
            return RoadPlanningUtils.ROAD_3;
        }
        if (myLocation.equals("一线天") && targetLocation.equals("步仙桥")) {
            return RoadPlanningUtils.ROAD_4;
        }
        return -1;
    }
}