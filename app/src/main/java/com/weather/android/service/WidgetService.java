package com.weather.android.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.weather.android.Main;
import com.weather.android.R;
import com.weather.android.Widget.WeatherWidget;
import com.weather.android.db.LocationCity;
import com.weather.android.db.cacheCity;
import com.weather.android.gson.Weather;
import com.weather.android.util.Utility;
import org.litepal.crud.DataSupport;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class WidgetService extends Service {
    private static final int ALARM_DURATION  = 300 * 60 * 1000; // service 自启间隔
    private static final int UPDATE_DURATION = 10 * 1000;     // Widget 更新间隔
    private static final int UPDATE_MESSAGE  = 1000;
    private UpdateHandler updateHandler; // 更新 Widget 的 Handler

    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWidget();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(getBaseContext(), WidgetService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getBaseContext(), 0,
                alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + ALARM_DURATION, pendingIntent);

        return START_STICKY;


    }
    @Override
    public void onCreate() {
        super.onCreate();

        Message message = Message.obtain();
        message.what = UPDATE_MESSAGE;
        updateHandler = new UpdateHandler();
        updateHandler.sendMessageDelayed(message, UPDATE_DURATION);
    }



    private void updateWidget() {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.weather_widget);
        List<LocationCity> locationList = DataSupport.findAll(LocationCity.class);
        if (!locationList.isEmpty()&&locationList.get(0)!=null) {
            String name = locationList.get(0).getLocationCity();
            List<cacheCity> list = DataSupport.where("cityname=?", name).find(cacheCity.class);
            if (!list.isEmpty()) {
                Weather weather = Utility.handleWeatherResponse(list.get(0).getResponseText());
                String temperature = weather.now.temperature + "°";
                String info = weather.now.more.info;
                String cityName = weather.basic.cityName;
                Date now = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String str = simpleDateFormat.format(now);
                remoteViews.setImageViewResource(R.id.appwidget_cityInfo_text, Utility.getImageView(info));
                remoteViews.setTextViewText(R.id.appwidget_cityName_text, cityName);
                remoteViews.setTextViewText(R.id.appwidget_cityTemperature_text, temperature);
                remoteViews.setTextViewText(R.id.appwidget_date_text, Utility.getWeek(str));
            }
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(this, Main.class), PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_cityTemperature_text, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_cityName_text,pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_cityInfo_text,pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.appwidget_date_text,pendingIntent);
        ComponentName componentName = new ComponentName(this, WeatherWidget.class);
        AppWidgetManager.getInstance(this).updateAppWidget(componentName, remoteViews);
       // 发送下次更新的消息
        Message message = updateHandler.obtainMessage();
        message.what=UPDATE_MESSAGE;
        updateHandler.sendMessageDelayed(message,UPDATE_DURATION);


    }

    private class UpdateHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_MESSAGE:
                    updateWidget();
                    break;
                default:
                    break;
            }
        }

    }
}

