package com.weather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by syy on 2018/5/24.
 */

public class CountCache extends DataSupport {
    private int id;

    private String responseText;

    private String cityName;

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

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }
}
