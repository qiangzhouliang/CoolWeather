package com.qzl.coolweather.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 从服务器端获取数据工具类
 * Created by Qzl on 2016-09-29.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();//打开连接
                    connection.setRequestMethod("GET");//设置请求方式
                    connection.setConnectTimeout(8000);//设置连接超出时间
                    connection.setReadTimeout(8000);//设置读取超出时间
                    InputStream in = connection.getInputStream();//从网络获取数据
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));//缓存
                    StringBuilder response = new StringBuilder();//StringBuilder对象是动态对象，允许扩充它所封装的字符串中字符的数量
                    String line;
                    //读取数据
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if (listener != null){
                        //接口回调
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null){
                        listener.onError(e);
                    }
                }finally {
                    if (connection != null){
                        connection.disconnect();//断开连接
                    }
                }
            }
        }).start();
    }

    public interface HttpCallbackListener{
        //加载结束
        void onFinish(String response);

        //加载出错
        void onError(Exception e);
    }

}
