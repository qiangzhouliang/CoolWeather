package com.qzl.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.qzl.coolweather.R;
import com.qzl.coolweather.db.CoolWeatherDB;
import com.qzl.coolweather.model.City;
import com.qzl.coolweather.model.County;
import com.qzl.coolweather.model.Province;
import com.qzl.coolweather.util.HttpUtil;
import com.qzl.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY= 2;

    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private CoolWeatherDB mCoolWeatherDB;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省级列表
     */
    private List<Province> mProvinceList;
    /**
     * 市级列表
     */
    private List<City> mCityList;
    /**
     * 县级列表
     */
    private List<County> mCountiesList;

    //选中的省份
    private Province selectedProvince;
    //选中的城市
    private City selectedCity;
    //当前选中的级别
    private int currentLevel;

    //是否从WeatherActivity中跳转过来
    private boolean isFromWeatherActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity",false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //已经选择了城市且不是从WeatherActivity跳转过来，才会直接跳转到WeatherActivity
        if (prefs.getBoolean("city_selected",false) && !isFromWeatherActivity){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        init();

    }

    private void init() {
        mListView = (ListView) findViewById(R.id.list_view);
        mTitleText = (TextView) findViewById(R.id.title_text);
        mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        mListView.setAdapter(mAdapter);
        mCoolWeatherDB = CoolWeatherDB.getInstance(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = mProvinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    selectedCity = mCityList.get(position);
                    queryCounties();
                }else if (currentLevel == LEVEL_COUNTY){
                    String countyCode = mCountiesList.get(position).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /**
     * 查寻全国所有的省，优先从数据库查寻，如果没有查寻到再去服务器查寻
     */
    private void queryProvinces() {
        mProvinceList = mCoolWeatherDB.loadProvinces();
        if (mProvinceList.size() > 0){
            dataList.clear();
            for (Province province : mProvinceList) {
                dataList.add(province.getProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer(null,"province");
        }
    }

    /**
     * 县
     */
    private void queryCounties() {
        //从本地获取
        mCountiesList = mCoolWeatherDB.loadCountys(selectedCity.getId());
        if (mCountiesList.size() > 0){
            dataList.clear();
            for (County county : mCountiesList) {
                dataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        }else {
            //从服务器获取
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /**
     * 市
     */
    private void queryCities() {
        mCityList = mCoolWeatherDB.loadCities(selectedProvince.getId());
        if (mCityList.size() > 0){
            dataList.clear();
            for (City city : mCityList) {
                dataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            //根据传入的代号和类型从服务器上查询省市县数据
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查寻省市县数据
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)){
            address = "http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpUtil.HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)){
                    //解析数据
                    result = Utility.handleProvincesResponse(mCoolWeatherDB,response);
                }else if ("city".equals(type)){
                    result = Utility.handCitiesResponse(mCoolWeatherDB,response,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handCountiesResponse(mCoolWeatherDB,response,selectedCity.getId());
                }
                if (result){
                    //通过runOnUIThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            //通过type类型来查询各自对应的数据
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    //关闭进度对话框
    private void closeProgressDialog() {
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    //显示进度对话框
    private void showProgressDialog() {
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载...");
            //防止触摸突出
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        //显示进度对话框
        mProgressDialog.show();
    }

    /**
     * 按键捕获
     * 捕获Back键，根据当前的级别来判断，此时应该返回那一个列表（省 市 县），还是直接退出
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY){
            queryCities();
        }else if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            if (isFromWeatherActivity){
                Intent intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
