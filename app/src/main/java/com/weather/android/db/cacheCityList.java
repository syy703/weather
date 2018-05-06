package com.weather.android.db;

import com.google.gson.annotations.SerializedName;
import com.weather.android.gson.Weather;

import org.litepal.crud.DataSupport;

/**
 * Created by syy on 2018/5/6.
 */

public class cacheCityList extends DataSupport {
    private int id;

    private String cityName;

    private int imgId;

   private String responseText;

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }


}
