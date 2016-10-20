package com.qzl.coolweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 创建数据库
 * Created by Qzl on 2016-09-26.
 */

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {
    public CoolWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);//创建表
        db.execSQL(CREATE_CITY);//创建表
        db.execSQL(CREATE_COUNTY);//创建表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Province创建表语句（省）
     */
    public static final String CREATE_PROVINCE = "CREATE TABLE Province("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "province_name TEXT ,"
            + "province_code TEXT)";

    /**
     * city创建表语句（市）
     */
    public static final String CREATE_CITY = "CREATE TABLE City("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "city_name TEXT ,"
            + "city_code TEXT,"
            + "province_id INTEGER)";

    /**
     * County创建表语句（县）
     */
    public static final String CREATE_COUNTY = "CREATE TABLE County("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "county_name TEXT ,"
            + "county_code TEXT,"
            + "city_id INTEGER)";
}
