package com.example.lzl_task_10.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.Marker;
import com.example.lzl_task_10.R;
import com.example.lzl_task_10.data.MarkerWeather;

public class MyInfoWindowAdapter implements AMap.InfoWindowAdapter {
    View infoWindow = null;
    Context context;

    public MyInfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if(infoWindow == null) {
            infoWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
        }
        render(marker, infoWindow);
        return infoWindow;
    }

    private void render(Marker marker, View infoWindow) {
        MarkerWeather object = (MarkerWeather) marker.getObject();
        TextView tem_tv=infoWindow.findViewById(R.id.map_temp);
        TextView qul_tv=infoWindow.findViewById(R.id.map_quality);
        tem_tv.setText(object.getTemperature()+"℃");
        qul_tv.setText(object.getQuality());
    }


    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
