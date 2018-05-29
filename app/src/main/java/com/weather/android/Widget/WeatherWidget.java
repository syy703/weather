package com.weather.android.Widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.weather.android.service.WidgetService;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {
    private static final int UPDATE_DURATION =  60 * 1000; // Widget 更新间隔
    private PendingIntent pendingIntent = null;
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, WidgetService.class));


    }
    /**
     * 第一个widget被添加调用
     * @param context
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);

        context.startService(new Intent(context, WidgetService.class));

    }
    /**
     * 最后一个widget被删除时调用
     * @param context
     */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        context.stopService(new Intent(context, WidgetService.class));
    }

    /**
     * widget被删除时调用
     * @param context
     * @param appWidgetIds
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}

