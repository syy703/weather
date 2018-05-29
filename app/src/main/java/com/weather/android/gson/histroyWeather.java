package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by syy on 2018/5/24.
 */

public class histroyWeather {
    private String success;

    @SerializedName("result")
    public List<Result> resultList;
}
