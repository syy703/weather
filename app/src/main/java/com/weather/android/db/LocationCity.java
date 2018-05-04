package com.weather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by syy on 2018/5/3.
 */

public class LocationCity extends DataSupport {
    private int id;

    private String locationCity;

    public int  getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocationCity() {
        return locationCity;
    }

    public void setLocationCity(String locationCity) {
        this.locationCity = locationCity;
    }
}
