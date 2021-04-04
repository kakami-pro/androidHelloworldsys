package com.example.lzl_task_10.utility;

import android.app.Activity;
import android.content.Context;
import android.text.method.QwertyKeyListener;

import com.example.lzl_task_10.data.AirQualityData;
import com.example.lzl_task_10.data.WeatherForecast;
import com.example.lzl_task_10.data.WeatherNow;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Range;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.util.List;

public class HeFenUtil {
    static {
        HeConfig.init("HE2104032015181982", "6a0600142ba34ebead94874e679f5f03");
        HeConfig.switchToDevService();
    }

    public static void getWeatherNow(Activity activity, String cityname,QWeather.OnResultWeatherNowListener listener){
        QWeather.getGeoCityLookup(activity, cityname, Range.CN, 1, null, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                String id = locationBean.get(0).getId();
                QWeather.getWeatherNow(activity,id, listener);
            }
        });

    }
    public static void getWeatherForecast(Activity activity, String city_name, QWeather.OnResultWeatherDailyListener listener){
        QWeather.getGeoCityLookup(activity, city_name, Range.CN, 1, null, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                String id = locationBean.get(0).getId();
                QWeather.getWeather3D(activity, id, null, null, listener);
            }
        });
    }
    public static void getAirQualityData(final Activity activity, String cityname,  QWeather.OnResultAirNowListener listener){
        QWeather.getGeoCityLookup(activity, cityname, Range.CN, 1, null, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                String id = locationBean.get(0).getId();

                QWeather.getAirNow(activity,id, null, listener);
            }
        });

    }

}
