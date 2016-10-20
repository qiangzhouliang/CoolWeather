package com.qzl.coolweather.model;

/**
 * 市实体类
 * Created by Qzl on 2016-09-27.
 */

public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvincedId() {
        return provinceId;
    }

    public void setProvincedId(int provinced) {
        this.provinceId = provinced;
    }
}
