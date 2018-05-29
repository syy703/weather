package com.weather.android.db;

import com.google.gson.annotations.SerializedName;
import com.weather.android.gson.Weather;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by syy on 2018/5/6.
 */

public class cacheCity extends DataSupport {
    private int id;

    private String cityName;

    private int imgId;

   private String responseText;

    private Date updateTime;

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

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
