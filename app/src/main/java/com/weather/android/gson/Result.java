package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by syy on 2018/5/24.
 */

public class Result {
    @SerializedName("citynm")
    public String cityName;

    @SerializedName("uptime")
    public String updateTime;

    @SerializedName("weather")
    public String info;

}
