package com.weather.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.weather.android.gson.FirstCity;
import com.weather.android.gson.Forecast;
import com.weather.android.gson.Weather;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements chooseAreaFragment.CallBackValue{
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView bodyTemperatureText;//体感温度
    private TextView humidityText;//湿度
    private TextView precipitationText;//降水量
    private TextView pressText;//压强
    private TextView visibilityText;//能见度
    private TextView windText;//风
    private TextView apiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private List<FirstCity> list;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    private BottomNavigationBar bottomNavigationBar;
    private String weatherId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_weather);
        bingPicImg =(ImageView) findViewById(R.id.bing_pic_img);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic=prefs.getString("bing_pic",null);
        if(bingPic!=null){
            Glide.with(this).load(bingPic)
                    .dontAnimate()
                    .into(bingPicImg);

        }else {
            loadBingPic();
        }
        weatherLayout=(ScrollView)findViewById(R.id.weather_layout);
        swipeRefresh=(SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        if (weatherLayout != null) {
            weatherLayout.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                @Override
                public void onScrollChanged() {
                    if (swipeRefresh != null) {
                        swipeRefresh.setEnabled(weatherLayout.getScrollY() == 0);
                    }
                }
            });
        }
        titleCity=(TextView)findViewById(R.id.title_city);
        titleUpdateTime=(TextView) findViewById(R.id.title_update_time);
        degreeText=(TextView) findViewById(R.id.degree_text);
        weatherInfoText=(TextView)findViewById(R.id.weather_info_text);
        forecastLayout=(LinearLayout)findViewById(R.id.forecast_layout);
        bodyTemperatureText=(TextView)findViewById(R.id.bodyTemperature_view);
        humidityText=(TextView)findViewById(R.id.humidity_view);
        precipitationText=(TextView)findViewById(R.id.precipitationy_view);
        pressText=(TextView)findViewById(R.id.press_view);
        visibilityText=(TextView)findViewById(R.id.visibility_view);
        windText=(TextView)findViewById(R.id.wind_view) ;
        apiText=(TextView)findViewById(R.id.api_text);
        pm25Text=(TextView)findViewById(R.id.pm25_text);
        comfortText=(TextView)findViewById(R.id.comfort_text);
        carWashText=(TextView)findViewById(R.id.car_wash_text);
        sportText=(TextView)findViewById(R.id.sport_text);
         bottomNavigationBar=(BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED)
        .setInActiveColor(R.color.lightGrey)
        .setActiveColor(R.color.lightGrey)
        .setFirstSelectedPosition(-1);
      //  .setBarBackgroundColor(R.color.white);
         bottomNavigationBar
                .addItem(new BottomNavigationItem(R.drawable.add,""))
        //        .addItem(new BottomNavigationItem(R.drawable.ic_launcher, ""))
                .initialise();
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener(){
            @Override
            public void onTabSelected(int position) {
                switch(position){
                    case 0:
                        Log.d("onTabSelected", "position=" + position);
                Intent intent=new Intent(WeatherActivity.this,choose.class);
                        startActivity(intent);
                        break;


                }
            }
            @Override
            public void onTabUnselected(int position) {
            }
            @Override
            public void onTabReselected(int position) {
            }
        });
        if(weatherId==null) {
            String cityName = getIntent().getStringExtra("cityName");
            weatherId = getJson("city.json", this, cityName);
        }
        else {
            weatherId=getIntent().getStringExtra("id");
        }
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                requestWeather(weatherId);
            }
        });

    }

    public void requestWeather(final  String weatherId){

        String url="http://guolin.tech/api/weather?cityid="+weatherId+"&key=33164f04b13f40fe9daf7f29def65a08";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
              final String responseText=response.body().string();
                final Weather weather=Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });
        loadBingPic();
    }

    private void showWeatherInfo(Weather weather){
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature +"°";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView)view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);
        }
        String bodyTemperature=weather.now.bodyTemperature;
        String humidity=weather.now.humidity;
        String precipitation=weather.now.precipitation;
        String press=weather.now.press;
        String visibility=weather.now.visibility;
        String windDirection=weather.now.windDirection;
        String windPower=weather.now.windPower;
        String windSpeed=weather.now.windSpeed;
        bodyTemperatureText.setText(bodyTemperature+"°");
        humidityText.setText(humidity+"%");
        precipitationText.setText(precipitation+"毫米");
        pressText.setText(press+"百帕");
        visibilityText.setText(visibility+"公里");
        windText.setText(windDirection+" "+windPower+"级 "+windSpeed+"公里/小时");
        if(weather.aqi!=null){
            apiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort="舒适度: "+ weather.suggestion.comfort.info;
        String carWash="洗车指数: "+ weather.suggestion.carWash.info;
        String sport="运动建议: "+ weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }

    public static String getJson(String fileName,Context context,String name){
        StringBuilder stringBuilder = new StringBuilder();
        String cityCode=new String();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            String city= stringBuilder.toString();
            JSONArray jsonArray=new JSONArray(city);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                String cityName=jsonObject.getString("cityName");
                if(cityName.equals(name)) {
                     cityCode = "CN" + jsonObject.getString("cityCode");
                }
            }
            return cityCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

       return null;
    }

    private void loadBingPic(){
        String url="http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic=response.body().string();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
}

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(0,fragment);
        transaction.commit();
    }

    @Override
    public void sendMessage(String value){

    }
}
