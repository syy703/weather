package com.weather.android;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.weather.android.db.Setting;
import com.weather.android.service.AutoUpdateService;
import com.weather.android.util.Utility;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    public LocationClient locationClient;
    private ImageView bingPicImg ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        notification();
        startService(new Intent(this,AutoUpdateService.class));
        boolean isConnected = Utility.isNetworkAvailable(getApplicationContext());
        if (isConnected == false) {
            Toast.makeText(getApplicationContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(MainActivity.this,Main.class);
            startActivity(intent);
            finish();
        } else {
            locationClient = new LocationClient(getApplicationContext());
            locationClient.registerLocationListener(new MyLocationListener());

            List<String> list = new ArrayList<>();

            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!list.isEmpty()) {
                String[] permissions = list.toArray(new String[list.size()]);
                ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
            } else {
                requestLocation();
            }

        }
    }
    private void requestLocation(){
        LocationClientOption option=new LocationClientOption();
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
        locationClient.start();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有权限才能使用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else {
                    Toast.makeText(this,"未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            String a=location.getCity().substring(0,location.getCity().length()-1);
            Intent intent=new Intent(MainActivity.this,Main.class);
            intent.putExtra("cityName",a);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade,0);
              //DataSupport.deleteAll(Setting.class);
//            List<cacheCityList> list=DataSupport.findAll(cacheCityList.class);
//            for(cacheCityList cityList:list){
//                System.out.println(cityList.getCityName());
//                System.out.println(cityList.getImgId());
//         //       System.out.println(cityList.getWeather());
//            }

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void notification(){
        long systemTime = System.currentTimeMillis();//当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long selectTime = calendar.getTimeInMillis();//设定定点时间
        if(systemTime > selectTime) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selectTime = calendar.getTimeInMillis();
        }
        long time = selectTime - systemTime;
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime = SystemClock.elapsedRealtime() + time;
        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //manager.cancel(pi);
        //  manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, AlarmManager.INTERVAL_DAY, pi);
        }
            else{
                manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, AlarmManager.INTERVAL_DAY, pi);
            }

    }
}
