package com.weather.android.Receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.weather.android.Main;
import com.weather.android.R;
import com.weather.android.db.LocationCity;
import com.weather.android.db.cacheCity;
import com.weather.android.gson.Weather;
import com.weather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        List<LocationCity> locationList = DataSupport.findAll(LocationCity.class);

        if (!locationList.isEmpty()&&locationList.get(0)!=null) {
            String name=locationList.get(0).getLocationCity();
            List<cacheCity> list=DataSupport.where("cityname=?",name).find(cacheCity.class);
            Weather weather= Utility.handleWeatherResponse(list.get(0).getResponseText());
            String max=weather.forecastList.get(1).temperature.max;
            String min=weather.forecastList.get(1).temperature.min;
            String info=weather.forecastList.get(1).more.info;
            String text="明天白天"+info+" "+"最高气温"+max+ "°"+"最低气温"+min +"°";
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent2 = new Intent(context, Main.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
            Notification notify = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icon)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle("天气提醒")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                   // .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setContentText(text)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notify);
        }
    }
}
