package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by syy on 2018/4/23.
 */

public class hourWeather {

    public String status;

    @SerializedName("hourly")
    public List<Hourly> hourlyList;


}
