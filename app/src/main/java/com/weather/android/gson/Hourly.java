package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by syy on 2018/4/23.
 */

public class Hourly {
    @SerializedName("cond_txt")
    public String weatherHourInfo;

    @SerializedName("time")
    public String updateHourTime;

    @SerializedName("tmp")
    public String hourTemperature;

}
