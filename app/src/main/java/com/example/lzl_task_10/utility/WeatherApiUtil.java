package com.example.lzl_task_10.utility;

import android.app.Activity;
import android.text.TextUtils;
import android.widget.Toast;

import com.example.lzl_task_10.data.AirQualityData;
import com.example.lzl_task_10.data.WeatherForecast;
import com.example.lzl_task_10.data.WeatherNow;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherApiUtil {
    public static final String API_KEY="f64bd6e4f5e94b339d8670af605f87b4";
    public interface OnWeatherNowFinished{
        void onFinished(WeatherNow data);
    }
    public interface OnWeatherForecastFinished{
        void onFinished(WeatherForecast data);

    }
    public interface OnAirQualityFinished{
        void onFinished(AirQualityData data);
    }
    public static void getAirQualityData(final Activity activity, String weather_id, final OnAirQualityFinished listener){
        String url=String.format("https://free-api.heweather.net/s6/air/now?location=%s&key=%s&lang=en",weather_id,API_KEY);
        HttpUtil.getOkHttpAsync(activity, url, new HttpUtil.SimpleAsyncCall() {
            @Override
            public void onFailure(String e) {
                showToast(activity,e);
                listener.onFinished(null);
            }
            @Override
            public void onResponse(String response) {
                if(!TextUtils.isEmpty(response)){
                    try {
                        JSONArray heWeather6 = new JSONObject(response).getJSONArray("HeWeather6");
                        String s= heWeather6.get(0).toString();
                        AirQualityData data = new Gson().fromJson(s, AirQualityData.class);
                        if(data!=null&&data.status.equalsIgnoreCase("ok")){
                            listener.onFinished(data);
                            return;
                        }
                    } catch (JSONException e) {e.printStackTrace();}
                }
                listener.onFinished(null);
            }
        });
    }

    public static void getWeatherForecast(Activity activity,String weather_id,OnWeatherForecastFinished listener){
        String url = String.format("https://free-api.heweather.net/s6/weather/forecast?location=%s&key=%s&lang=en", weather_id, API_KEY);
        HttpUtil.getOkHttpAsync(activity, url, new HttpUtil.SimpleAsyncCall() {
            @Override
            public void onFailure(String e) {
                showToast(activity,e);
                listener.onFinished(null);
            }

            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)){
                    try {
                        JSONArray heWeather6 = new JSONObject(s).getJSONArray("HeWeather6");
                        String s1 = heWeather6.get(0).toString();
                        WeatherForecast data = new Gson().fromJson(s1, WeatherForecast.class);
                        if (data!=null&&data.status.equalsIgnoreCase("ok")){
                            listener.onFinished(data);
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                listener.onFinished(null);

            }
        });
    }

    public static void getWeatherNow(Activity activity,String weather_id,OnWeatherNowFinished listener)
    {
        String url = String.format("https://free-api.heweather.net/s6/weather/now?location=%s&key=%s&lang=en", weather_id.trim(), API_KEY);
        HttpUtil.getOkHttpAsync(activity, url, new HttpUtil.SimpleAsyncCall() {
            @Override
            public void onFailure(String e) {
                showToast(activity,e);
                listener.onFinished(null);
            }

            @Override
            public void onResponse(String s) {
                if (!TextUtils.isEmpty(s)){
                    try {
                        JSONArray heWeather6 = new JSONObject(s).getJSONArray("HeWeather6");
                        String s1 = heWeather6.get(0).toString();
                        WeatherNow weatherNow = new Gson().fromJson(s1, WeatherNow.class);
                        listener.onFinished(weatherNow);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFinished(null);
                    }
                }
                else {
                    listener.onFinished(null);
                }
            }
        });
    }
    private static void showToast(Activity activity,String s){
        Toast.makeText(activity,s,Toast.LENGTH_SHORT).show();
    }
}
