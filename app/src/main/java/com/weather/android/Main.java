package com.weather.android;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;

import com.mob.MobSDK;
import com.weather.android.Adapter.MyPagerAdapter;
import com.weather.android.Adapter.ViewPagerAdapter;
import com.weather.android.Fragment.MyFragment;
import com.weather.android.db.LocationCity;
import com.weather.android.db.chooseCity;
import com.weather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.onekeyshare.OnekeyShare;
import me.relex.circleindicator.CircleIndicator;

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
                    case R.id.count:
                        Intent intent2=new Intent(getApplicationContext(),countActivity.class);
                        startActivity(intent2);
                        break;
                    case R.id.about:
                          Intent intent=new Intent(getApplicationContext(),about.class);
                          startActivity(intent);
                        break;
//                    case R.id.share:
//
//                          break;
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

