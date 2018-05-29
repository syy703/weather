package com.weather.android.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.ActivityChooserView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weather.android.R;
import com.weather.android.db.LocationCity;
import com.weather.android.db.chooseCity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by syy on 2018/5/1.
 */

public class cityAdapter extends ArrayAdapter<chooseCity> {
//    public static final int TYPE_SWIPE = 0;
//    public static final int TYPE_NON_SWIPE = 1;
    private List<chooseCity> list;
    private int resourceId;
    private List<LocationCity> city= DataSupport.findAll(LocationCity.class);
    public cityAdapter(Context context, int textViewResourceId, List<chooseCity> list){
        super(context,textViewResourceId,list);
        resourceId=textViewResourceId;
    }

    @Override
    public int getViewTypeCount() {
        // menu type count
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // 设置定位城市不能滑动
        chooseCity choose=getItem(position);
        boolean is=choose.getCityName().equals(city.get(0).getLocationCity());
        return is ? 0:1;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        chooseCity chooseCity=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.cityInfo=(ImageView)view.findViewById(R.id.cityInfo);
            viewHolder.cityName=(TextView)view.findViewById(R.id.cityName);
            viewHolder.cityTemperature=(TextView)view.findViewById(R.id.cityTemperature);
            viewHolder.location=(ImageView)view.findViewById(R.id.location);
            view.setTag(viewHolder);
        }
        else {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.cityTemperature.setText(chooseCity.getTemperature());
        viewHolder.cityName.setText(chooseCity.getCityName());
        viewHolder.cityInfo.setImageResource(chooseCity.getImgId());
        for(LocationCity locationCity:city){
            if(chooseCity.getCityName().equals(locationCity.getLocationCity())){
                viewHolder.location.setImageResource(R.drawable.left_location_city);
            }
        }
        return view;
    }
    class ViewHolder{
        ImageView cityInfo;
        TextView cityName;
        TextView cityTemperature;
        ImageView location;
    }


}
