package com.weather.android.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.weather.android.R;
import com.weather.android.db.cacheCityList;
import com.weather.android.gson.Weather;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

//后台每8小时自动更新数据
public class AutoUpdateServiceFour extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int anHour = 4 * 60 * 60 * 1000; // 这是8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateServiceFour.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 更新天气信息。
     */
    private void updateWeather() {
        List<cacheCityList> list = DataSupport.findAll(cacheCityList.class);
        for (cacheCityList cityList : list) {
            Weather cacheWeather = Utility.handleWeatherResponse(cityList.getResponseText());
            String weatherId = cacheWeather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=33164f04b13f40fe9daf7f29def65a08";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleWeatherResponse(responseText);
                    if (weather != null && "ok".equals(weather.status)) {
                        cacheCityList cCityList=new cacheCityList();
                        try {
                            cCityList.setImgId(loadBackGround(weather));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                            cCityList.setResponseText(responseText);
                            cCityList.setUpdateTime(new Date());
                            cCityList.updateAll("cityname=? ",weather.basic.cityName);

                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public int loadBackGround(Weather weather)throws ParseException {
        Date now=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date time= sdf.parse(sdf.format(now));
        Date date=sdf.parse("18:00");
        boolean isBefore=time.before(date);
        if(isBefore){
            if(weather.now.more.info.equals("晴")){
               // Glide.with(this).load(R.drawable.bg_na).dontAnimate().into(bingPicImg);
                return R.drawable.bg_na;
            }else if(weather.now.more.info.equals("多云")){
              //  Glide.with(this).load(R.drawable.bg_cloudy_day).dontAnimate().into(bingPicImg);
                return R.drawable.cloudy_day;
            }else if(weather.now.more.info.equals("阴")){
              //  Glide.with(this).load(R.drawable.bg_rain).dontAnimate().into(bingPicImg);
                return R.drawable.overcast_day;
            }else if(weather.now.more.info.substring(1,2).equals("雨")){
             //   Glide.with(this).load(R.drawable.bg_overcast).dontAnimate().into(bingPicImg);
                return R.drawable.rainy_day;
            }
            else if(weather.now.more.info.substring(1,2).equals("雪")){
              //  Glide.with(this).load(R.drawable.bg_snow).dontAnimate().into(bingPicImg);
                return R.drawable.bg_snow;
            }else if(weather.now.more.info.equals("雷阵雨")){
             //   Glide.with(this).load(R.drawable.bg_thunder_storm).dontAnimate().into(bingPicImg);
                return R.drawable.bg_thunder_storm;
            }else if(weather.now.more.info.contains("雾")){
              //  Glide.with(this).load(R.drawable.bg_fog).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fog;
            }else {
              //  Glide.with(this).load(R.drawable.bg_fine_day).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fine_day;
            }
        }
        else {
            if(weather.now.more.info.equals("晴")){
             //   Glide.with(this).load(R.drawable.bg_fine_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fine_night;
            }else if(weather.now.more.info.equals("多云")){
             //   Glide.with(this).load(R.drawable.city).dontAnimate().into(bingPicImg);
                return R.drawable.bg_cloudy_night;
            }else if(weather.now.more.info.equals("阴")){
              //  Glide.with(this).load(R.drawable.bg_cloudy_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_cloudy_night;
            }else if(weather.now.more.info.substring(1,2).equals("雨")){
             //   Glide.with(this).load(R.drawable.bg_rain).dontAnimate().into(bingPicImg);
                return R.drawable.bg_rainy_night;
            }
            else if(weather.now.more.info.substring(1,2).equals("雪")){
             //   Glide.with(this).load(R.drawable.bg_snow_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_snow_night;
            }else if(weather.now.more.info.equals("雷阵雨")){
             //   Glide.with(this).load(R.drawable.bg_thunder_storm).dontAnimate().into(bingPicImg);
                return R.drawable.bg_thunder_storm;
            }else if(weather.now.more.info.contains("雾")){
             //   Glide.with(this).load(R.drawable.bg_fog).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fog;
            }else {
             //   Glide.with(this).load(R.drawable.bg_fine_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fine_night;
            }
        }
    }

    }





