package com.example.lin1wang.bdmapdemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin1.wang on 2018/2/5.
 */

public class Info implements Serializable {

    private static final long serialVersionUID = 6588672796119685094L;
    private double latitude;
    private double longitude;
    private int imgId;
    private String name;
    private String distance;
    private int zan;

    public static List<Info> infos = new ArrayList<>();

    static {
        infos.add(new Info(31.190784, 121.304796, R.drawable.xingkong1, "星空1", "距离355米", 345));
        infos.add(new Info(31.184784, 121.308796, R.drawable.xingkong1, "星空2", "距离786米", 1234));
    }

    public Info(double latitude, double longitude, int imgId, String name, String distance, int zan) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.imgId = imgId;
        this.name = name;
        this.distance = distance;
        this.zan = zan;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }


}
