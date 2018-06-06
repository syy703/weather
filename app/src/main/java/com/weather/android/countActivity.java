package com.weather.android;

import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weather.android.View.PieChartView;
import com.weather.android.db.CountCache;
import com.weather.android.db.LocationCity;
import com.weather.android.gson.PieChartData;
import com.weather.android.db.cacheCity;
import com.weather.android.gson.Forecast;
import com.weather.android.gson.Result;
import com.weather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

public class countActivity extends AppCompatActivity {
    private Button back;
    private TextView goodDayText;
    private ProgressBar goodDayBar;
    private TextView badDayText;
    private ProgressBar badDayBar;
    private TextView worstDayText;
    private ProgressBar worstDayBar;
    private TextView unKnowDayText;
    private ProgressBar unKnowDayBar;
    private PieChartView pieChartView;
    private TextView good;
    private TextView bad;
    private TextView worst;
    private TextView unKnow;
    private List<PieChartData> pieChartDatas;
    private List<LocationCity> locationCityList=new ArrayList<>();
    private String locationCity;
    private List<cacheCity> cacheCityList=new ArrayList<>();
    private List<Forecast> list=new ArrayList<>();
    private List<Result> resultList=new ArrayList<>();
    private List<CountCache> countCacheList=new ArrayList<>();
    private float goodDay=0;
    private float badDay=0;
    private float worstDay=0;
    private float unKnowDay=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        back=(Button)findViewById(R.id.count_back);
        goodDayText=(TextView)findViewById(R.id.goodDay_text);
        goodDayBar=(ProgressBar)findViewById(R.id.goodDay_bar);
        badDayText=(TextView)findViewById(R.id.badDay_text);
        badDayBar=(ProgressBar)findViewById(R.id.badDay_bar);
        worstDayText=(TextView)findViewById(R.id.worstDay_text);
        worstDayBar=(ProgressBar)findViewById(R.id.worstDay_bar);
        unKnowDayText=(TextView)findViewById(R.id.unKnowDay_text);
        unKnowDayBar=(ProgressBar)findViewById(R.id.unKnowDay_bar);
        good=(TextView)findViewById(R.id.goodDay);
        bad=(TextView)findViewById(R.id.badDay);
        worst=(TextView)findViewById(R.id.worstDay);
        unKnow=(TextView)findViewById(R.id.unKnowDay);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        /**
         * 初始化饼图
         */
        pieChartView = (PieChartView) findViewById(R.id.customPieChart);
        // 从12点钟方向开始
        pieChartView.setStartAngle(270);
        // 分割线与背景色相同
        pieChartView.setLineColor(ContextCompat.getColor(this, R.color.color2));
        // 字体白色
        pieChartView.setTextColor(ContextCompat.getColor(this, R.color.color1));
        // 扇形半径和遮盖圆半径
        pieChartView.setRadius(275, 25);

        /**
         * 添加数据
         */
        locationCityList= DataSupport.findAll(LocationCity.class);
        if(!locationCityList.isEmpty()) {
             locationCity = locationCityList.get(0).getLocationCity();
             cacheCityList=DataSupport.where("cityname=?",locationCity).find(cacheCity.class);
        }
        if(!cacheCityList.isEmpty()){
            list= Utility.handleWeatherResponse(cacheCityList.get(0).getResponseText()).forecastList;
            for(Forecast forecast:list){
                String info=forecast.more.info;
                if(info.contains("晴")||info.contains("多云")||info.contains("阴")){
                    goodDay++;
                }else if(info.contains("雨")){
                    badDay++;
                }else if(info.contains("沙")||info.contains("雪")||info.contains("冰")){
                    worstDay++;
                }else if(info.contains("雾")||info.contains("霾")){
                    unKnowDay++;
                }
        }
        }
        pieChartDatas = new ArrayList<>();
        good.setVisibility(View.INVISIBLE);
        goodDayBar.setVisibility(View.INVISIBLE);
        bad.setVisibility(View.INVISIBLE);
        badDayBar.setVisibility(View.INVISIBLE);
        worst.setVisibility(View.INVISIBLE);
        worstDayBar.setVisibility(View.INVISIBLE);
        unKnow.setVisibility(View.INVISIBLE);
        unKnowDayBar.setVisibility(View.INVISIBLE);
        if(goodDay!=0) {
            PieChartData pcd1 = new PieChartData();
            pcd1.percent = goodDay;
            pcd1.content = "晴或阴天";
            pcd1.color = ContextCompat.getColor(this, R.color.color11);
        //    goodDayText.setText(format.format(new BigDecimal(String.valueOf(goodDay / 7))));
            goodDayText.setText((int)goodDay+"/"+7);
            goodDayBar.setMax(7);
            goodDayBar.setProgress((int) goodDay);
            good.setVisibility(View.VISIBLE);
            goodDayBar.setVisibility(View.VISIBLE);
            pieChartDatas.add(pcd1);
        }
        if(badDay!=0) {
            PieChartData pcd2 = new PieChartData();
            pcd2.percent = badDay;
            pcd2.content = "有雨";
            pcd2.color = ContextCompat.getColor(this, R.color.color13);
          //  badDayText.setText(format.format(new BigDecimal(String.valueOf(badDay / 7))));
            badDayText.setText((int)badDay+"/"+7);
            badDayBar.setMax(7);
            badDayBar.setProgress((int) badDay);
            bad.setVisibility(View.VISIBLE);
            badDayBar.setVisibility(View.VISIBLE);
            pieChartDatas.add(pcd2);
        }
        if(worstDay!=0) {
            PieChartData pcd3 = new PieChartData();
            pcd3.percent = worstDay;
            pcd3.content = "极端天气";
            pcd3.color = ContextCompat.getColor(this, R.color.color5);
         //worstDayText.setText(format.format(new BigDecimal(String.valueOf(worstDay / 7))));
            worstDayText.setText((int)worstDay+"/"+7);
            worstDayBar.setMax(7);
            worstDayBar.setProgress((int) worstDay);
            worst.setVisibility(View.VISIBLE);
            worstDayBar.setVisibility(View.VISIBLE);
            pieChartDatas.add(pcd3);
        }
        if(unKnowDay!=0) {
            PieChartData pcd4 = new PieChartData();
            pcd4.percent = unKnowDay;
            pcd4.content = "雾霾天";
            pcd4.color = ContextCompat.getColor(this, R.color.color6);
          //  unKnowDayText.setText(format.format(new BigDecimal(String.valueOf(unKnowDay / 7))));
            unKnowDayText.setText((int)unKnowDay+"/"+7);
            unKnowDayBar.setMax(7);
            unKnowDayBar.setProgress((int) unKnowDay);
            unKnow.setVisibility(View.VISIBLE);
            unKnowDayBar.setVisibility(View.VISIBLE);
            pieChartDatas.add(pcd4);
        }
        pieChartView.setPieChartData(pieChartDatas);
    }
}
