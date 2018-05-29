package com.weather.android.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.weather.android.Fragment.MyFragment;
import com.weather.android.Main;
import com.weather.android.MainActivity;
import com.weather.android.R;
import com.weather.android.db.City;
import com.weather.android.db.County;
import com.weather.android.db.Province;
import com.weather.android.gson.Weather;
import com.weather.android.gson.histroyWeather;
import com.weather.android.gson.hourWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by syy on 2018/4/15.
 */

public class Utility {
//处理JSON数据
    public static boolean handleProvinceResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince=new JSONArray(response);
                for(int i=0;i<allProvince.length();i++){
                    JSONObject provinceObject=allProvince.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
             return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity=new JSONArray(response);
                for(int i=0;i<allCity.length();i++){
                    JSONObject cityObject=allCity.getJSONObject(i);
                    City city=new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try {
                JSONArray allCounty=new JSONArray(response);
                for(int i=0;i<allCounty.length();i++){
                    JSONObject countyObject=allCounty.getJSONObject(i);
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
             return true;
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
        return false;
    }
//处理JSON数据
    public static Weather handleWeatherResponse(String response){
        try {
            if (response != null && response.startsWith("\ufeff")) {
                response = response.substring(1);
            }
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static hourWeather handleHourWeatherResponse(String response){
        try {
            if (response != null && response.startsWith("\ufeff")) {
                response = response.substring(1);
            }
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,hourWeather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static histroyWeather handleHistroyWeatherResponse(String response){
        try {
            if (response != null && response.startsWith("\ufeff")) {
                response = response.substring(1);
            }
            JSONObject jsonObject=new JSONObject(response);
            String weatherContent=jsonObject.toString();
            return new Gson().fromJson(weatherContent,histroyWeather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //转化日期为星期
    public static  String getWeek(String sdate) {
        // 再转换为时间
        Date date = strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
//将天气信息转化为图标信息
    public static int getImageView(String data){
        int imgId=0;
        switch (data){
            case "晴":
                imgId= R.drawable.iclockweather_w1;
                break;
            case "多云":
                imgId= R.drawable.iclockweather_w2;
                break;
            case "晴间多云":
                imgId= R.drawable.iclockweather_w2;
                break;
            case "少云":
                imgId= R.drawable.iclockweather_w3;
                break;
            case "阴":
                imgId= R.drawable.iclockweather_w3;
                break;
            case "阵雨":
                imgId= R.drawable.iclockweather_w8;
                break;
            case "雷阵雨":
                imgId= R.drawable.iclockweather_w9;
                break;
            case "强雷阵雨":
                imgId= R.drawable.iclockweather_w9;
                break;
            case "雷阵雨伴有冰雹":
                imgId= R.drawable.iclockweather_w18;
                break;
            case "强阵雨":
                imgId= R.drawable.iclockweather_w9;
                break;
            case "小雨":
                imgId=R.drawable.iclockweather_w4;
                break;
            case "中雨":
                imgId=R.drawable.iclockweather_w5;
                break;
            case "大雨":
                imgId=R.drawable.iclockweather_w6;
                break;
            case "暴雨":
                imgId=R.drawable.iclockweather_w7;
                break;
            case "大暴雨":
                imgId=R.drawable.iclockweather_w7;
                break;
            case "特大暴雨":
                imgId=R.drawable.iclockweather_w7;
                break;
            case "极端降雨":
                imgId=R.drawable.iclockweather_w7;
                break;
            case "毛毛雨/细雨":
                imgId=R.drawable.iclockweather_w4;
                break;
            case "小雪":
                imgId=R.drawable.iclockweather_w11;
                break;
            case "中雪":
                imgId=R.drawable.iclockweather_w12;
                break;
            case "大雪":
                imgId=R.drawable.iclockweather_w13;
                break;
            case "暴雪":
                imgId=R.drawable.iclockweather_w14;
                break;
            case "雾":
                imgId=R.drawable.iclockweather_w17;
                break;
            case "薄雾":
                imgId=R.drawable.iclockweather_w17;
                break;
            case "霾":
                imgId=R.drawable.iclockweather_w17;
                break;
            case "扬沙":
                imgId=R.drawable.iclockweather_w17;
                break;
            case "雨夹雪":
                imgId=R.drawable.iclockweather_w10;
                break;
            case "雨雪天气":
                imgId=R.drawable.iclockweather_w10;
                break;
            case "台风":
                imgId=R.drawable.typhoon;
                break;
            case "冰雹":
                imgId=R.drawable.iclockweather_w18;
                break;
            default:
                imgId=R.drawable.unknown;
                break;
        }
        return imgId;
    }
//判断网络是否可用
    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getApplicationContext().getSystemService(
                        Context.CONNECTIVITY_SERVICE);

        if (manager == null) {
            return false;
        }

        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        if (networkinfo == null || !networkinfo.isAvailable()) {
            return false;
        }

        return true;
    }
    //获取每个月的天数
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    //截取LinerLayout
    public static Bitmap getLinearLayoutBitmap(LinearLayout linearLayout) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            h += linearLayout.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(linearLayout.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        linearLayout.draw(canvas);
        return bitmap;
    }
    //截取LinerLayout
    public static Bitmap getFrameLayoutBitmap(FrameLayout linearLayout) {
        int h = 0;
        Bitmap bitmap;
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            h += linearLayout.getChildAt(i).getHeight();

        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(linearLayout.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        linearLayout.draw(canvas);
        return bitmap;
    }
//截取ScrollView
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    /**
     * 压缩图片
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            // 重置baos
            baos.reset();
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            // 每次都减少10
            options -= 10;
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return bitmap;
    }

    /**
     * 保存到sdcard
     * @param b
     * @return
     */
    public static String savePic(Bitmap b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.US);
        File outfile = new File("/sdcard/image");
        // 如果文件不存在，则创建一个新文件
        if (!outfile.isDirectory()) {
            try {
                outfile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fname = outfile + "/" + sdf.format(new Date()) + ".png";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fname);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fname;
    }
    /**
     * 保存到sdcard
     * @param b
     * @return
     */
    public static File saveBitmp(Bitmap b) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
                Locale.US);
        File outfile = new File("/sdcard/image");
        // 如果文件不存在，则创建一个新文件
        if (!outfile.isDirectory()) {
            try {
                outfile.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String fname = outfile + "/" + sdf.format(new Date()) + ".png";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fname);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file=new File(fname);
        return  file;
    }
    /**
     * 获取文件
     * @param bitmap
     */
    public static File saveBitmaptoFile(Bitmap bitmap){
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/screenLongShot.png");
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
    /**
     * 分享图片到微信
     * @param activity
     * @param file
     */
    public static void shareWeChat(Activity activity, File file){
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageUris.add(Uri.fromFile(file));
        }else {
            Uri uri = null;
            try {
                uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(activity.getContentResolver(), file.getAbsolutePath(), "share.jpg", null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageUris.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        activity.startActivity(intent);
    }
    /**
     * 分享图片到微信朋友圈
     * @param activity
     * @param file
     */
    public static void shareWeChatCircle(Activity activity,File file){
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");
        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageUris.add(Uri.fromFile(file));
        }else {
            Uri uri = null;
            try {
                uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(activity.getContentResolver(), file.getAbsolutePath(), "share.jpg", null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageUris.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        activity.startActivity(intent);
    }
    public static Bitmap shotActivity(Activity ctx) {

        View view = ctx.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        Bitmap bp = Bitmap.createBitmap(view.getDrawingCache(), 0, 0, view.getMeasuredWidth(),
                view.getMeasuredHeight());

        view.setDrawingCacheEnabled(false);
        view.destroyDrawingCache();

        return bp;
    }
    /**
     * 分享图片到QQ
     * @param activity
     * @param file
     */
    public static void shareQQ(Activity activity, File file){
        Intent intent = new Intent();
        ComponentName comp = new ComponentName("com.tencent.mobileqq", "com.tencent.mobileqq.activity.JumpActivity");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageUris.add(Uri.fromFile(file));
        }else {
            Uri uri = null;
            try {
                uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(activity.getContentResolver(), file.getAbsolutePath(), "share.jpg", null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageUris.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        activity.startActivity(intent);
    }
    /**
     * 分享图片到空间
     * @param activity
     * @param file
     */
    public static void shareQQzone(Activity activity, File file){
        Intent intent = new Intent("android.intent.action.SEND");
        ComponentName comp = new ComponentName("com.qzone", "com.qzonex.module.operation.ui.QZonePublishMoodActivity");
        intent.setComponent(comp);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("image/*");

        ArrayList<Uri> imageUris = new ArrayList<Uri>();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            imageUris.add(Uri.fromFile(file));
        }else {
            Uri uri = null;
            try {
                uri = Uri.parse(android.provider.MediaStore.Images.Media.insertImage(activity.getContentResolver(), file.getAbsolutePath(), "share.jpg", null));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            imageUris.add(uri);
        }
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        activity.startActivity(intent);
    }
}
