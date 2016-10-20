package com.qzl.coolweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.qzl.coolweather.service.AutoUpdateService;

/**
 * Created by Qzl on 2016-10-20.
 */

public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        //启动服务
        context.startService(i);
    }
}
