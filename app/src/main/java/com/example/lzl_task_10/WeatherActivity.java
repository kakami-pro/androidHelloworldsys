package com.example.lzl_task_10;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lzl_task_10.data.AirQualityData;
import com.example.lzl_task_10.data.DailyForecast;
import com.example.lzl_task_10.data.WeatherForecast;
import com.example.lzl_task_10.data.WeatherNow;
import com.example.lzl_task_10.utility.HeFenUtil;
import com.example.lzl_task_10.utility.WeatherApiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeatherActivity extends AppCompatActivity {
    public static final int CITY_REQ_COQE=0;
    ImageView iv_cond;
    String weather_id="CN101210701";
    String city_name="温州";
    TextView tv_city,tv_update_time,tv_temp,tv_weather_info;
    LinearLayout forecastLayout;
    TextView tv_aqi,tv_pm25;
    SwipeRefreshLayout swipeRefreshLayout;
    AtomicInteger requestCount=new AtomicInteger(0);
    private static final String KEY_WEATHER_ID="weather_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        loadCityname();
        forecastLayout=findViewById(R.id.forecast_layout);
        tv_city=findViewById(R.id.title_city_tv);
        tv_update_time=findViewById(R.id.title_pub_time_tv);
        tv_temp = findViewById(R.id.now_temp_tv);
        tv_weather_info = findViewById(R.id.now_cond_tv);
        iv_cond=findViewById(R.id.now_cond_iv);
        tv_aqi=findViewById(R.id.aqi_text);
        tv_pm25=findViewById(R.id.pm25_text);
        swipeRefreshLayout=findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateData();
            }
        });
        Button bt=findViewById(R.id.bt_update);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });

        tv_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherActivity.this, SelectCityActivity.class);
                startActivityForResult(intent,CITY_REQ_COQE);
            }
        });
        updateData();
    }


    private void loadWeatherId()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.getString(KEY_WEATHER_ID,"CN101210701");
    }
    private void saveWeatherId(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_WEATHER_ID,weather_id);
        edit.apply();
    }
    private void loadCityname()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.getString(KEY_WEATHER_ID,"温州");
    }
    private void saveCityname(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(KEY_WEATHER_ID,city_name);
        edit.apply();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CITY_REQ_COQE&&resultCode== RESULT_OK){
            city_name=SelectCityActivity.getWeatherIdByIntent(data);
            updateData();
            saveCityname();
        }
    }

    private void updateRefreshState(){
        if (requestCount.incrementAndGet()==3){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void updateWeatherAqi()
    {
//        WeatherApiUtil.getAirQualityData(this, weather_id, new WeatherApiUtil.OnAirQualityFinished() {
//            @Override
//            public void onFinished(AirQualityData data) {
//                if (data!=null&&data.status.equalsIgnoreCase("ok"))
//                {
//                    tv_aqi.setText(data.airNowCity.aqi);
//                    tv_pm25.setText(data.airNowCity.pm25);
//                }
//                else {
//                    tv_aqi.setText("--");
//                    tv_pm25.setText("--");
//                }
//                updateRefreshState();
//            }
//        });
        HeFenUtil.getAirQualityData(this, city_name, new QWeather.OnResultAirNowListener() {
            @Override
            public void onError(Throwable throwable) {
                showToast(throwable.getMessage());

            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                if (airNowBean!=null){
                    AirNowBean.NowBean now = airNowBean.getNow();
                    tv_pm25.setText(now.getPm2p5());
                    tv_aqi.setText(now.getAqi());
                }
                else {
                    tv_aqi.setText("--");
                    tv_pm25.setText("--");
                }
                updateRefreshState();

            }
        });
    }
    private void updateData()
    {
        swipeRefreshLayout.setRefreshing(true);
        requestCount.set(0);
        updateWeatherNow();
        updateWeatherForecast();
        updateWeatherAqi();
    }

    private void updateWeatherNow()
    {
//        WeatherApiUtil.getWeatherNow(this, weather_id, new WeatherApiUtil.OnWeatherNowFinished() {
//            @Override
//            public void onFinished(WeatherNow data) {
//                if (data!=null)
//                {
//                    tv_city.setText(data.basic.location);
//                    tv_update_time.setText(data.update.loc);
//                    tv_temp.setText(data.now.tmp);
//                    tv_weather_info.setText(data.now.cond_txt);
//                    updateWeatherIcon(data.now.cond_code,iv_cond);
//                    updateRefreshState();
//                }
//            }
//        });
        HeFenUtil.getWeatherNow(this, city_name, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                showToast(throwable.getMessage());
            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                if (weatherNowBean!=null){
                    WeatherNowBean.NowBaseBean now = weatherNowBean.getNow();
                    tv_city.setText(city_name);
                    tv_update_time.setText(weatherNowBean.getBasic().getUpdateTime());
                    tv_temp.setText(now.getTemp()+"℃");
                    tv_weather_info.setText(now.getText());
                    updateWeatherIcon(now.getIcon(),iv_cond);
                    updateRefreshState();
                }
            }
        });
    }
    private void updateWeatherIcon(String cond_code,ImageView iv_cond)
    {
        String url = String.format("https://cdn.heweather.com/cond_icon/%s.png", cond_code);
        Glide.with(this).load(Uri.parse(url)).into(iv_cond);
    }
    private  void updateWeatherForecast()
    {
//        WeatherApiUtil.getWeatherForecast(this, weather_id, new WeatherApiUtil.OnWeatherForecastFinished() {
//            @Override
//            public void onFinished(WeatherForecast data) {
//                if (data!=null)
//                {
//                    forecastLayout.removeAllViews();
//                    List<DailyForecast> dailyForecastList = data.dailyForecastList;
//                    for (int i = 0; i < dailyForecastList.size(); i++) {
//                        DailyForecast dailyForecast = dailyForecastList.get(i);
//                        View v = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, null, false);
//                        TextView item_date_text=v.findViewById(R.id.item_date_text);
//                        TextView item_max_text=v.findViewById(R.id.item_max_text);
//                        TextView item_min_text=v.findViewById(R.id.item_min_text);
//                        ImageView item_iv_day_con=v.findViewById(R.id.item_iv_day_con);
//                        ImageView item_iv_night_con=v.findViewById(R.id.item_iv_night_con);
//                        item_date_text.setText(dailyForecast.date);
//                        item_max_text.setText(dailyForecast.tmp_max+"℃");
//                        item_min_text.setText(dailyForecast.tmp_min+"℃");
//                        updateWeatherIcon(dailyForecast.cond_code_d,item_iv_day_con);
//                        updateWeatherIcon(dailyForecast.cond_code_n,item_iv_night_con);
//                        forecastLayout.addView(v);
//                        updateRefreshState();
//                    }
//
//                }
//            }
//        });
        HeFenUtil.getWeatherForecast(this, city_name, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable throwable) {
                showToast(throwable.getMessage());
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                if (weatherDailyBean!=null){
                    forecastLayout.removeAllViews();
                    List<WeatherDailyBean.DailyBean> daily = weatherDailyBean.getDaily();
                    for (WeatherDailyBean.DailyBean dailyBean : daily) {
                        View v=LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item,null,false);
                        TextView item_date_text=v.findViewById(R.id.item_date_text);
                        TextView item_max_text=v.findViewById(R.id.item_max_text);
                        TextView item_min_text=v.findViewById(R.id.item_min_text);
                        ImageView item_iv_day_con=v.findViewById(R.id.item_iv_day_con);
                        ImageView item_iv_night_con=v.findViewById(R.id.item_iv_night_con);
                        item_date_text.setText(dailyBean.getFxDate());
                        item_max_text.setText(dailyBean.getTempMax()+"℃");
                        item_min_text.setText(dailyBean.getTempMin()+"℃");
                        updateWeatherIcon(dailyBean.getIconDay(),item_iv_day_con);
                        updateWeatherIcon(dailyBean.getIconNight(),item_iv_night_con);
                        forecastLayout.addView(v);
                        updateRefreshState();

                    }

                }
            }
        });
    }
    private void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }
}