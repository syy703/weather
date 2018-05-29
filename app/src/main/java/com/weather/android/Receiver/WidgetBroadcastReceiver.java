package com.weather.android.Receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weather.android.service.WidgetService;

import java.util.List;

public class WidgetBroadcastReceiver extends BroadcastReceiver {

    public static final String SERVICE_NAME = "com.weather.android.service.WidgetService";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_TIME_TICK)) {
            if (!isServiceWork(context, SERVICE_NAME)) {
                context.startService(new Intent(context, WidgetService.class));
            }
        }
    }

    /**
     * 判断service是否在运行
     * @param mContext
     * @param serviceName
     * @return
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(100);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }
}