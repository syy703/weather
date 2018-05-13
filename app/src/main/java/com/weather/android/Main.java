package com.weather.android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
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
import com.weather.android.db.LocationCity;
import com.weather.android.db.chooseCity;
import com.weather.android.gson.Forecast;
import com.weather.android.gson.Hourly;
import com.weather.android.gson.Weather;
import com.weather.android.gson.hourWeather;
import com.weather.android.util.HttpUtil;
import com.weather.android.util.Utility;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Main extends AppCompatActivity {
    private List<View> viewList = new ArrayList<>();//ViewPager数据源
    private List<Fragment> fragmentList = new ArrayList<>();
    private MyPagerAdapter myPagerAdapter;//适配器
    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager viewPager;
    private CircleIndicator circleIndicator;
    public String cityName;
    public String locationCityName;
    boolean isConnected;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button cityButton;

    //主界面
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.main);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        navigationView=(NavigationView)findViewById(R.id.nav_view);
        drawerLayout.setFitsSystemWindows(true);
        drawerLayout.setClipToPadding(false);
        isConnected = Utility.isNetworkAvailable(getApplicationContext());
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        circleIndicator=(CircleIndicator)findViewById(R.id.indicator);
        cityButton=(Button)findViewById(R.id.cityButton) ;
        cityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        initMain();
        Log.d("Main",getClass().getSimpleName()+ "onCreate");
    }


    @Override
    protected void onRestart() {
        super.onRestart();
      //  initMain();
        Log.d("Main", getClass().getSimpleName()+"onRestart");
        Log.d("Main", getClass().getSimpleName());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Main", getClass().getSimpleName()+"onDestroy");
    }

    public void initMain() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id=item.getItemId();
                switch (id){
                    case R.id.home:
                        Intent intent1=new Intent(getApplicationContext(),choosedCity.class);
                        startActivity(intent1);
                        break;
                    case R.id.about:
                          Intent intent=new Intent(getApplicationContext(),about.class);
                          startActivity(intent);
                        break;
                }
                return  true;
            }
        });
        final List<chooseCity> cityList = DataSupport.findAll(com.weather.android.db.chooseCity.class);
        List<String> arrayList = new ArrayList<>();
        for (chooseCity city : cityList) {
            arrayList.add(city.getCityName());
        }

        locationCityName=getIntent().getStringExtra("cityName");
        if(locationCityName!=null) {
            List<chooseCity> list = DataSupport.where("cityname=?", locationCityName).find(chooseCity.class);
            DataSupport.deleteAll(LocationCity.class);
            LocationCity locationCity=new LocationCity();
            locationCity.setLocationCity(locationCityName);
            locationCity.save();
            if (list.isEmpty()) {
                arrayList.add(locationCityName);
            }
        }
        else{
              List<LocationCity> locationCityList=DataSupport.findAll(LocationCity.class);
              locationCityName=locationCityList.get(0).getLocationCity();
        }
        viewPager.setOffscreenPageLimit(arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            MyFragment myFragment = new MyFragment().newInstance(arrayList.get(i),locationCityName);
            fragmentList.add(myFragment);
            viewPagerAdapter = new ViewPagerAdapter(this, getSupportFragmentManager(), fragmentList);
        }
        viewPager.setAdapter(viewPagerAdapter);
        if(arrayList.size()>1) {
            circleIndicator.setViewPager(viewPager);
        }
        viewPagerAdapter.notifyDataSetChanged();
        if(isConnected==false){
            viewPager.setCurrentItem(0);
        }
        else {
           // int viewPosition = getIntent().getExtras().getInt("position");
            Bundle bundle = getIntent().getExtras();
            if(bundle!=null) {
                int viewPosition = bundle.getInt("position");
                viewPager.setCurrentItem(viewPosition);
            }
            else {
                viewPager.setCurrentItem(0);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

