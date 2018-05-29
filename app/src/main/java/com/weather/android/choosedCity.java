package com.weather.android;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.weather.android.Adapter.cityAdapter;
import com.weather.android.db.chooseCity;
import com.weather.android.gson.Weather;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class choosedCity extends AppCompatActivity {
    private SwipeMenuListView swipeMenuListView;
    private FloatingActionButton addButton;
    private List<chooseCity> chooseCityList = new ArrayList<>();
    private com.weather.android.Adapter.cityAdapter cityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         Log.d("choosedCity",getClass().getSimpleName()+"onCreate");
        setContentView(R.layout.choosed_city);
        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        chooseCityList = DataSupport.findAll(com.weather.android.db.chooseCity.class);
        cityAdapter = new cityAdapter(choosedCity.this, R.layout.city_item, chooseCityList);
        swipeMenuListView = (SwipeMenuListView) findViewById(R.id.choosedCity_list_view);
        swipeMenuListView.setAdapter(cityAdapter);
        addButton = (FloatingActionButton) findViewById(R.id.fab);
        addButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(choosedCity.this, choose.class);
                startActivity(intent);


            }
        });
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                switch (menu.getViewType()) {
                    case 0:
                        break;
                    case 1:
                         SwipeMenuItem deleteItem = new SwipeMenuItem(
                            getApplicationContext());
                    // set item background
                         deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                            0x3F, 0x25)));
                    // set item width
                         deleteItem.setWidth(dp2px(60));
                    // set a icon
                         deleteItem.setIcon(R.drawable.delete);
                    // add to nav_menu

                         menu.addMenuItem(deleteItem);
                        break;
                }
            }
        };

        swipeMenuListView.setMenuCreator(creator);
        swipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        DataSupport.deleteAll(chooseCity.class, "cityname=?", chooseCityList.get(position).getCityName());
                      //  DataSupport.deleteAll(cacheCity.class,"cityname=?",chooseCityList.get(position).getCityName());
                        chooseCityList.remove(position);
                        cityAdapter.notifyDataSetChanged();
                        break;

                }
                return true;
            }
        }
        );

        swipeMenuListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = chooseCityList.get(position).getCityName();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putString("name", name);
                Intent intent = new Intent(choosedCity.this, Main.class);
               // intent.setFlags(intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.fade,0);
            }
        });

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("choosedCity",getClass().getSimpleName()+"onRestart");
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("choosedCity",getClass().getSimpleName()+"onNewIntent");
        if(intent.getExtras()==null) {
            List<chooseCity> chooseCityList = DataSupport.findAll(com.weather.android.db.chooseCity.class);
            chooseCityList.clear();
            chooseCityList.addAll(chooseCityList);
            cityAdapter.notifyDataSetChanged();
        }
        handleIntent(intent);
    }
    private void handleIntent(Intent intent){
        if(intent.getExtras()!=null){
            String weatherId=intent.getExtras().getString("weatherId");
            String cityName=intent.getExtras().getString("cityName");
            List<chooseCity> list = DataSupport.where("cityname=?", cityName).find(chooseCity.class);
            if(list.isEmpty()){
                requestWeather(weatherId);
            }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Main", getClass().getSimpleName()+"onDestroy");
    }
    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }



    public void requestWeather(final String weatherId) {

        String url = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=33164f04b13f40fe9daf7f29def65a08";
        HttpUtil.sendOkHttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(choosedCity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {

                            saveWeather(weather, weatherId);
                        } else {
                            Toast.makeText(choosedCity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
        });

    }

    private void saveWeather(Weather weather, String weatherId) {
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "°";
        String weatherInfo = weather.now.more.info;
        chooseCity city = new chooseCity();
        city.setCityName(cityName);
        city.setCityCode(weatherId);
        city.setImgId(Utility.getImageView(weatherInfo));
        city.setTemperature(degree);
        city.save();
        List<chooseCity> list = DataSupport.findAll(com.weather.android.db.chooseCity.class);
        chooseCityList.clear();
        chooseCityList.addAll(list);
        cityAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

