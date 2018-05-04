package com.weather.android;

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

import com.weather.android.db.chooseCity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by syy on 2018/5/1.
 */

public class cityAdapter extends ArrayAdapter<chooseCity> {
    private List<chooseCity> list;
    private int resourceId;

    public cityAdapter(Context context, int textViewResourceId, List<chooseCity> list){
        super(context,textViewResourceId,list);
        resourceId=textViewResourceId;
    }

    public void setParams(List<chooseCity> list){
        this.list=list;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        chooseCity chooseCity=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.cityInfo=(ImageView)view.findViewById(R.id.cityInfo);
            viewHolder.cityName=(TextView)view.findViewById(R.id.cityName);
            viewHolder.cityTemperature=(TextView)view.findViewById(R.id.cityTemperature);
            view.setTag(viewHolder);
        }
        else {
            view=convertView;
            viewHolder=(ViewHolder)view.getTag();
        }
        viewHolder.cityTemperature.setText(chooseCity.getTemperature());
        viewHolder.cityName.setText(chooseCity.getCityName());
        viewHolder.cityInfo.setImageResource(chooseCity.getImgId());
        return view;
    }
    class ViewHolder{
        ImageView cityInfo;
        TextView cityName;
        TextView cityTemperature;
    }


}
