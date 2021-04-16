package com.example.lzl_task_10.utility;

import android.app.Activity;
import android.content.Context;
import android.text.method.QwertyKeyListener;
import android.widget.ArrayAdapter;

import com.example.lzl_task_10.data.AirQualityData;
import com.example.lzl_task_10.data.WeatherForecast;
import com.example.lzl_task_10.data.WeatherNow;
import com.hp.hpl.sparta.Document;
import com.qweather.sdk.bean.IndicesBean;
import com.qweather.sdk.bean.WarningBean;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.base.IndicesType;
import com.qweather.sdk.bean.base.Lang;
import com.qweather.sdk.bean.base.Range;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;

import static com.qweather.sdk.bean.base.IndicesType.COMF;

public class HeFenUtil {
    static {
        HeConfig.init("HE2104151849461823", "68e21d870d6c4c158a6e91ad36420ba4");
        HeConfig.switchToDevService();
    }

    public static void setGEO(Activity activity,String cityname,QWeather.OnResultGeoListener listener){
        QWeather.getGeoCityLookup(activity, cityname, Range.CN, 1, null, listener);
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
    public static void getWarningData(Activity activity,String cityname,QWeather.OnResultWarningListener listener)
    {
        QWeather.getGeoCityLookup(activity, cityname, Range.CN, 1, null, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                String id = locationBean.get(0).getId();
                QWeather.getWarning(activity, id, null, listener);
            }
        });
    }

    public static void getLifequality(Activity activity,String cityname,IndicesType type,QWeather.OnResultIndicesListener listener)
    {
        QWeather.getGeoCityLookup(activity, cityname, Range.CN, 1, null, new QWeather.OnResultGeoListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(GeoBean geoBean) {
                List<GeoBean.LocationBean> locationBean = geoBean.getLocationBean();
                String id = locationBean.get(0).getId();
                List<IndicesType> list=new ArrayList<>();
                list.add(type);
                QWeather.getIndices1D(activity, id, Lang.ZH_HANS, list,listener);
            }
        });

    }

}
