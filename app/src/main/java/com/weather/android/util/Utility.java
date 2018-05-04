package com.weather.android.util;

import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.weather.android.R;
import com.weather.android.db.City;
import com.weather.android.db.County;
import com.weather.android.db.Province;
import com.weather.android.gson.Weather;
import com.weather.android.gson.hourWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by syy on 2018/4/15.
 */

public class Utility {

    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince=new JSONArray(response);
                for(int i=0;i<allProvince.length();i++){
                    JSONObject provinceObject=allProvince.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
             return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity=new JSONArray(response);
                for(int i=0;i<allCity.length();i++){
                    JSONObject cityObject=allCity.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty=new JSONArray(response);
                for(int i=0;i<allCounty.length();i++){
                    JSONObject countyObject=allCounty.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
             return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response){
        try {
            if (response != null && response.startsWith("\ufeff")) {
                response = response.substring(1);
            }
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static hourWeather handleHourWeatherResponse(String response){
        try {
            if (response != null && response.startsWith("\ufeff")) {
                response = response.substring(1);
            }
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,hourWeather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //转化日期为星期
    public static  String getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
//将天气信息转化为图标信息
    public static int getImageView(String data){
        int imgId=0;
        switch (data){
            case "晴":
                imgId= R.drawable.clear;
                break;
            case "多云":
                imgId= R.drawable.cloudy;
                break;
            case "阴":
                imgId= R.drawable.overcast;
                break;
            case "阵雨":
                imgId= R.drawable.showerrain;
                break;
            case "雷阵雨":
                imgId= R.drawable.thunder;
                break;
            case "小雨":
                imgId=R.drawable.lightrain;
                break;
            case "中雨":
                imgId=R.drawable.moderaterain;
                break;
            case "大雨":
                imgId=R.drawable.heavyrain;
                break;
            case "暴雨":
                imgId=R.drawable.rainstrom;
                break;
            case "小雪":
                imgId=R.drawable.lightsnow;
                break;
            case "中雪":
                imgId=R.drawable.moderatesnow;
                break;
            case "大雪":
                imgId=R.drawable.heavysnow;
                break;
            case "暴雪":
                imgId=R.drawable.blizzard;
                break;
            case "雾":
                imgId=R.drawable.fog;
                break;
            case "霾":
                imgId=R.drawable.haze;
                break;
            case "雨夹雪":
                imgId=R.drawable.sleet;
                break;
            case "台风":
                imgId=R.drawable.typhoon;
                break;
            case "冰雹":
                imgId=R.drawable.ice;
                break;
            default:
                imgId=R.drawable.unknown;
                break;
        }
        return imgId;
    }


}
