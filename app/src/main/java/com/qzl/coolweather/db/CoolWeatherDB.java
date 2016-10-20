package com.qzl.coolweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qzl.coolweather.model.City;
import com.qzl.coolweather.model.County;
import com.qzl.coolweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装常用的数据库操作
 * 使用单利模式
 * Created by Qzl on 2016-09-27.
 */

public class CoolWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";
    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    public static final String PROVINCE = "Province";//表名
    public static final String PROVINCE_NAME = "province_name";//列名
    public static final String PROVINCE_CODE = "province_code";
    public static final String CITY_NAME = "city_name";
    public static final String CITY_CODE = "city_code";
    public static final String PROVINCE_ID = "province_id";
    public static final String CITY = "City";
    public static final String COUNTY_NAME = "county_name";
    public static final String COUNTY_CODE = "county_code";
    public static final String COUNTY = "County";

    private static CoolWeatherDB sCoolWeatherDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private CoolWeatherDB(Context context){
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取CoolWeatherDB的实例
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if (sCoolWeatherDB == null){
            sCoolWeatherDB = new CoolWeatherDB(context);
        }
        return sCoolWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put(PROVINCE_NAME,province.getProvinceName());
            values.put(PROVINCE_CODE,province.getProvinceCode());
            //参数二：指定在添加数据的情况下给某些可为空的列自动赋值为空
            db.insert(PROVINCE,null,values);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces(){
        List<Province>list = new ArrayList<>();
        Cursor cursor = db.query(PROVINCE,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex(PROVINCE_NAME)));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex(PROVINCE_CODE)));
                list.add(province);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    /**
     * 将city实例存储到数据库
     */
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put(CITY_NAME,city.getCityName());
            values.put(CITY_CODE,city.getCityCode());
            values.put(PROVINCE_ID,city.getProvincedId());
            db.insert(CITY,null,values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     */
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query(CITY, null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null);
        if (cursor.moveToFirst()){
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex(CITY_NAME)));
                city.setCityCode(cursor.getString(cursor.getColumnIndex(CITY_CODE)));
                city.setProvincedId(provinceId);
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    /**
     * 将county实例存储到数据库
     */
    public void saveCpunty(County county){
        if (county != null){
            ContentValues values = new ContentValues();
            values.put(COUNTY_NAME,county.getCountyName());
            values.put(COUNTY_CODE,county.getCountyCode());
            values.put("city_id",county.getCityId());
            db.insert(COUNTY,null,values);
        }
    }

    /**
     * 从数据库库读取城市下所有县的信息
     */
    public List<County> loadCountys(int cityId){
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query(COUNTY, null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
        if (cursor.moveToFirst()){
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex(COUNTY_NAME)));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex(COUNTY_CODE)));
                county.setCityId(cityId);
                list.add(county);
            }while (cursor.moveToNext());
        }
        return list;
    }
}
