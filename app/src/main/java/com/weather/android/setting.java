package com.weather.android;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.weather.android.db.Setting;
import com.weather.android.service.AutoUpdateService;
import com.weather.android.service.AutoUpdateServiceFour;
import com.weather.android.service.AutoUpdateServiceSix;

import org.litepal.crud.DataSupport;

import java.util.List;

public class setting extends AppCompatActivity {

private RadioGroup radioGroup;
private Switch aSwitch;
private Button settingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);
       // Log.d("Setting",getClass().getSimpleName()+"onCreate");
        aSwitch=(Switch)findViewById(R.id.setting_switch);
        radioGroup=(RadioGroup)findViewById(R.id.setting_radio_group);
        settingButton=(Button)findViewById(R.id.setting_back) ;
        List<Setting> list=DataSupport.findAll(Setting.class);
        if(!list.isEmpty()){
            aSwitch.setChecked(list.get(0).isChecked());
            if(list.get(0).getId()==R.id.four){
                radioGroup.getChildAt(0).setEnabled(true);
            }
            else if(list.get(0).getId()==R.id.six){
                radioGroup.getChildAt(1).setEnabled(true);
            }else if(list.get(0).getId()==R.id.eight){
                radioGroup.getChildAt(2).setEnabled(true);
            }
        }
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.deleteAll(Setting.class);
                Setting s=new Setting();
                s.setRadioButtonId(radioGroup.getCheckedRadioButtonId());
                s.setChecked(aSwitch.isChecked());
                s.save();
                finish();
            }
        });
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    stopAllService();
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(true);
                    }
                    int id = radioGroup.getCheckedRadioButtonId();
                    if(id==R.id.four){
                        startService(new Intent(getApplicationContext(),AutoUpdateServiceFour.class));
                    }
                    else if(id==R.id.six){
                        startService(new Intent(getApplicationContext(),AutoUpdateServiceSix.class));
                    }
                    else if(id==R.id.eight){
                        startService(new Intent(getApplicationContext(),AutoUpdateService.class));
                    }
                }
                else {
                    stopAllService();
                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        radioGroup.getChildAt(i).setEnabled(false);
                    }
                }
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.four:
                        stopAllService();
                        stopService(new Intent(getApplicationContext(),AutoUpdateServiceFour.class));
                        break;
                    case R.id.six:
                        stopAllService();
                        startService(new Intent(getApplicationContext(),AutoUpdateServiceSix.class));
                        break;
                    case R.id.eight:
                        stopAllService();
                        startService(new Intent(getApplicationContext(),AutoUpdateService.class));
                        break;

                }
            }
        });

    }

    public void stopAllService(){
        getApplicationContext().stopService(new Intent(getApplicationContext(),AutoUpdateService.class));
        getApplicationContext().stopService(new Intent(getApplicationContext(),AutoUpdateServiceFour.class));
        getApplicationContext().stopService(new Intent(getApplicationContext(),AutoUpdateServiceSix.class));
    }
}
