package com.weather.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.bumptech.glide.Glide;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;
import com.weather.android.db.cacheCityList;
import com.weather.android.db.chooseCity;
import com.weather.android.gson.Forecast;
import com.weather.android.gson.Hourly;
import com.weather.android.gson.Weather;
import com.weather.android.gson.hourWeather;
import com.weather.android.service.AutoUpdateService;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by syy on 2018/4/29.
 */

public class MyFragment extends Fragment {
    private ScrollView weatherLayout;
    private HorizontalScrollView horizontalScrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private TextView nowDataText;
    private LinearLayout forecastLayout;
    private LinearLayout hourLayout;
    private TextView bodyTemperatureText;//体感温度
    private TextView humidityText;//湿度
    private TextView precipitationText;//降水量
    private TextView pressText;//压强
    private TextView visibilityText;//能见度
    private TextView windText;//风
    private TextView apiText;
    private TextView pm25Text;
    private TextView qltyText;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    private BottomNavigationBar bottomNavigationBar;
    private TextView locationView;
    private String weatherId;
    public String cityName;
    private Button cityButton;
    private String locationCity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if(bundle != null){
            cityName=bundle.getString("cityName");
            locationCity=bundle.getString("locationCity");

        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_weather,container,false);
        init(cityName,view,locationCity);
        return view;
    }
    public void requestWeather(final  String weatherId,final String locationCity){

        String url="http://guolin.tech/api/weather?cityid="+weatherId+"&key=33164f04b13f40fe9daf7f29def65a08";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather= Utility.handleWeatherResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            cacheCityList cCityList=new cacheCityList();
                            try {
                                cCityList.setImgId(loadBackGround(weather));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            List<cacheCityList> list=DataSupport.where("cityname=?",cityName).find(cacheCityList.class);
                            if(list.isEmpty()){

                                cCityList.setResponseText(responseText);
                                cCityList.setCityName(cityName);

                                cCityList.save();
                            }
                            else {
                                cCityList.setResponseText(responseText);
                                cCityList.updateAll("cityname=? ",cityName);
                            }

                            try {
                                showWeatherInfo(weather);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                        }else {
                            Toast.makeText(getActivity(),"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });

    }

    public void requestHourWeather(final  String weatherId){

        String url="https://free-api.heweather.com/s6/weather/hourly?location="+weatherId+"&key=33164f04b13f40fe9daf7f29def65a08";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),"获取逐时天气信息失败",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseHourText=response.body().string();
                final hourWeather hourWeather=Utility.handleHourWeatherResponse(responseHourText);
               getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(hourWeather!=null && "ok".equals(hourWeather.status)){

                            showHourWeatherInfo(hourWeather);
                        }else {
                            Toast.makeText(getActivity(),"获取逐时天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });

            }
        });

    }

    private void showWeatherInfo(Weather weather)throws ParseException{

        if(weather.basic.cityName.equals(locationCity)){
            locationView.setVisibility(View.VISIBLE);
        }
        String cityName=weather.basic.cityName;
        String updateTime=weather.basic.update.updateTime.split(" ")[1];
        String degree=weather.now.temperature +"°";
        String weatherInfo=weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText("上次更新:"+updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        List<chooseCity> list = DataSupport.where("cityname=?", cityName).find(chooseCity.class);
        if (list.isEmpty()) {
            chooseCity city = new chooseCity();
            city.setCityName(cityName);
            city.setCityCode(weatherId);
            city.setImgId(Utility.getImageView(weatherInfo));
            city.setTemperature(degree);
            city.save();
        }
        else {
            chooseCity city=new chooseCity();
            city.setTemperature(degree);
            city.setImgId(Utility.getImageView(weatherInfo));
            city.updateAll("cityname=?",cityName);
        }

        Date now=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        String str=simpleDateFormat.format(now);
        nowDataText.setText(Utility.getWeek(str)+"     "+"今天");
        forecastLayout.removeAllViews();
        for(Forecast forecast:weather.forecastList){
            View view= LayoutInflater.from(getContext()).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=(TextView)view.findViewById(R.id.date_text);
            TextView infoText=(TextView) view.findViewById(R.id.info_text);
            TextView maxText=(TextView)view.findViewById(R.id.max_text);
            TextView minText=(TextView)view.findViewById(R.id.min_text);
            String date=Utility.getWeek(forecast.date);
            dateText.setText(date);
            infoText.setText((forecast.more.info));
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
            qltyText.setText(weather.aqi.city.qlty);
        }
        String comfort="舒适度:"+"  "+ weather.suggestion.comfort.info;
        String carWash="洗车指数:"+"  "+ weather.suggestion.carWash.info;
        String sport="运动建议:"+"  "+ weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
    private void showHourWeatherInfo(hourWeather weather){
        hourLayout.removeAllViews();
        for(Hourly hourly:weather.hourlyList){
            View view= LayoutInflater.from(getContext()).inflate(R.layout.hour_item,hourLayout,false);
            TextView  dateHourText=(TextView)view.findViewById(R.id.date_hour_text);
            TextView  weatherHourText=(TextView)view.findViewById(R.id.hour_weather_text);
            TextView  temperatureHourText=(TextView)view.findViewById(R.id.hour_temperature_text);
            dateHourText.setText(hourly.updateHourTime.split(" ")[1]);
            temperatureHourText.setText(hourly.hourTemperature+"°");
            weatherHourText.setText(hourly.weatherHourInfo);
            hourLayout.addView(view);

        }
        horizontalScrollView.setVisibility(View.VISIBLE);
    }
   public void init(final  String cityName,View view,final String locationCity){
       weatherId = getJson("city.json", getContext(), cityName);
       weatherLayout = (ScrollView) view.findViewById(R.id.weather_layout);
       horizontalScrollView = (HorizontalScrollView) view.findViewById(R.id.horizontalScrollView);
       swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
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
       titleCity = (TextView) view.findViewById(R.id.title_city);
       titleUpdateTime = (TextView) view.findViewById(R.id.title_update_time);
       locationView=(TextView)view.findViewById(R.id.locationView);
       degreeText = (TextView) view.findViewById(R.id.degree_text);
       weatherInfoText = (TextView) view.findViewById(R.id.weather_info_text);
       nowDataText=(TextView)view.findViewById(R.id.now_date_text);
       forecastLayout = (LinearLayout) view.findViewById(R.id.forecast_layout);
       hourLayout = (LinearLayout) view.findViewById(R.id.hour_layout);
       bodyTemperatureText = (TextView) view.findViewById(R.id.bodyTemperature_view);
       humidityText = (TextView) view.findViewById(R.id.humidity_view);
       precipitationText = (TextView) view.findViewById(R.id.precipitationy_view);
       pressText = (TextView) view.findViewById(R.id.press_view);
       visibilityText = (TextView) view.findViewById(R.id.visibility_view);
       windText = (TextView) view.findViewById(R.id.wind_view);
       apiText = (TextView) view.findViewById(R.id.api_text);
       pm25Text = (TextView) view.findViewById(R.id.pm25_text);
       qltyText =(TextView)view.findViewById(R.id.qlty_text) ;
       comfortText = (TextView) view.findViewById(R.id.comfort_text);
       carWashText = (TextView) view.findViewById(R.id.car_wash_text);
       sportText = (TextView) view.findViewById(R.id.sport_text);
       cityButton=(Button)view.findViewById(R.id.cityButton);
       cityButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(), choosedCity.class);
               intent.putExtra("cityName", cityName);
               intent.putExtra("cityCode", weatherId);
               startActivity(intent);
           }
       });



       weatherLayout.setVisibility(View.INVISIBLE);
       horizontalScrollView.setVisibility(View.INVISIBLE);
       locationView.setVisibility(View.INVISIBLE);
       swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
           @Override
           public void onRefresh() {
               requestWeather(weatherId,locationCity);
               requestHourWeather(weatherId);
           }
       });
       bingPicImg = (ImageView) view.findViewById(R.id.bing_pic_img);
       List<cacheCityList> cityLists=DataSupport.where("cityname=?",cityName).find(cacheCityList.class);
       if(!cityLists.isEmpty()){
           for(cacheCityList cityList:cityLists) {
               Glide.with(getContext()).load(cityList.getImgId()).dontAnimate().into(bingPicImg);
               try {
                   showWeatherInfo(Utility.handleWeatherResponse(cityList.getResponseText()));
               } catch (ParseException e) {
                   e.printStackTrace();
               }
           }

       }
       else {
           requestWeather(weatherId, locationCity);
       }
       requestHourWeather(weatherId);


   }
    public static String getJson(String fileName, Context context, String name){
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


    public static MyFragment newInstance(final  String cityName,final String locationCity){
        Bundle bundle = new Bundle();
        bundle.putString("locationCity",locationCity);
        bundle.putString("cityName", cityName);
        MyFragment myFragment=new MyFragment();
        myFragment.setArguments(bundle);
        return myFragment;

    }
    public int loadBackGround(Weather weather)throws ParseException{
        Date now=new Date();
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date time= sdf.parse(sdf.format(now));
        Date date=sdf.parse("18:00");
        boolean isBefore=time.before(date);
        if(isBefore){
            if(weather.now.more.info.equals("晴")){
                Glide.with(this).load(R.drawable.bg_na).dontAnimate().into(bingPicImg);
                return R.drawable.bg_na;
            }else if(weather.now.more.info.equals("多云")){
                Glide.with(this).load(R.drawable.bg_cloudy_day).dontAnimate().into(bingPicImg);
                return R.drawable.bg_cloudy_day;
            }else if(weather.now.more.info.equals("阴")){
                Glide.with(this).load(R.drawable.bg_rain).dontAnimate().into(bingPicImg);
                return R.drawable.bg_rain;
            }else if(weather.now.more.info.substring(1,2).equals("雨")){
                Glide.with(this).load(R.drawable.bg_overcast).dontAnimate().into(bingPicImg);
                return R.drawable.bg_overcast;
            }
            else if(weather.now.more.info.substring(1,2).equals("雪")){
                Glide.with(this).load(R.drawable.bg_snow).dontAnimate().into(bingPicImg);
                return R.drawable.bg_snow;
            }else if(weather.now.more.info.equals("雷阵雨")){
                Glide.with(this).load(R.drawable.bg_thunder_storm).dontAnimate().into(bingPicImg);
                return R.drawable.bg_thunder_storm;
            }else if(weather.now.more.info.contains("雾")){
                Glide.with(this).load(R.drawable.bg_fog).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fog;
            }else {
                Glide.with(this).load(R.drawable.bg_fine_day).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fine_day;
            }
        }
        else {
            if(weather.now.more.info.equals("晴")){
                Glide.with(this).load(R.drawable.bg_fine_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fine_night;
            }else if(weather.now.more.info.equals("多云")){
                Glide.with(this).load(R.drawable.bg_cloudy_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_cloudy_night;
            }else if(weather.now.more.info.equals("阴")){
                Glide.with(this).load(R.drawable.bg_cloudy_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_cloudy_night;
            }else if(weather.now.more.info.substring(1,2).equals("雨")){
                Glide.with(this).load(R.drawable.bg_rain).dontAnimate().into(bingPicImg);
                return R.drawable.bg_rain;
            }
            else if(weather.now.more.info.substring(1,2).equals("雪")){
                Glide.with(this).load(R.drawable.bg_snow_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_snow_night;
            }else if(weather.now.more.info.equals("雷阵雨")){
                Glide.with(this).load(R.drawable.bg_thunder_storm).dontAnimate().into(bingPicImg);
                return R.drawable.bg_thunder_storm;
            }else if(weather.now.more.info.contains("雾")){
                Glide.with(this).load(R.drawable.bg_fog).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fog;
            }else {
                Glide.with(this).load(R.drawable.bg_fine_night).dontAnimate().into(bingPicImg);
                return R.drawable.bg_fine_night;
            }
        }
    }
}
