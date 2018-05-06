package com.weather.android.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by syy on 2018/4/16.
 */

public class Now {

    @SerializedName("tmp")
    public String temperature;//当前温度

    @SerializedName("fl")
    public String bodyTemperature;//体感温度

    @SerializedName("hum")
    public String humidity;//湿度

    @SerializedName("pcpn")
    public String precipitation;//降水量(毫米)

    @SerializedName("pres")
    public String press;//压强（百帕)

    @SerializedName("vis")
    public String visibility;//能见度(公里)

    @SerializedName("wind_dir")
    public String windDirection;//风向

    @SerializedName("wind_sc")
    public String windPower;//风力

    @SerializedName("wind_spd")
    public String windSpeed;//风速(公里/小时)

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public  String info;//天气状况


    }
}
