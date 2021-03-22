package com.example.lzl_task_10;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lzl_task_10.data.AirQualityData;
import com.example.lzl_task_10.data.DailyForecast;
import com.example.lzl_task_10.data.WeatherForecast;
import com.example.lzl_task_10.data.WeatherNow;
import com.example.lzl_task_10.utility.WeatherApiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class WeatherActivity extends AppCompatActivity {
    ImageView iv_cond;
    String weather_id="CN101210701";
    TextView tv_city,tv_update_time,tv_temp,tv_weather_info;
    LinearLayout forecastLayout;
    TextView tv_aqi,tv_pm25;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        forecastLayout=findViewById(R.id.forecast_layout);
        tv_city=findViewById(R.id.title_city_tv);
        tv_update_time=findViewById(R.id.title_pub_time_tv);
        tv_temp = findViewById(R.id.now_temp_tv);
        tv_weather_info = findViewById(R.id.now_cond_tv);
        iv_cond=findViewById(R.id.now_cond_iv);
        tv_aqi=findViewById(R.id.aqi_text);
        tv_pm25=findViewById(R.id.pm25_text);
        Button bt=findViewById(R.id.bt_update);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
            }
        });
    }

    private void updateWeatherAqi()
    {
        WeatherApiUtil.getAirQualityData(this, weather_id, new WeatherApiUtil.OnAirQualityFinished() {
            @Override
            public void onFinished(AirQualityData data) {
                if (data!=null)
                {
                    tv_aqi.setText(data.airNowCity.aqi);
                    tv_pm25.setText(data.airNowCity.pm25);
                }
                else {
                    tv_aqi.setText("--");
                    tv_pm25.setText("--");
                }
            }
        });
    }
    private void updateData()
    {
        updateWeatherNow();
        updateWeatherForecast();
        updateWeatherAqi();
    }

    private void updateWeatherNow()
    {
        WeatherApiUtil.getWeatherNow(this, weather_id, new WeatherApiUtil.OnWeatherNowFinished() {
            @Override
            public void onFinished(WeatherNow data) {
                if (data!=null)
                {
                    tv_city.setText(data.basic.location);
                    tv_update_time.setText(data.update.loc);
                    tv_temp.setText(data.now.tmp);
                    tv_weather_info.setText(data.now.cond_txt);
                    updateWeatherIcon(data.now.cond_code,iv_cond);
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
        WeatherApiUtil.getWeatherForecast(this, weather_id, new WeatherApiUtil.OnWeatherForecastFinished() {
            @Override
            public void onFinished(WeatherForecast data) {
                if (data!=null)
                {
                    forecastLayout.removeAllViews();
                    List<DailyForecast> dailyForecastList = data.dailyForecastList;
                    for (int i = 0; i < dailyForecastList.size(); i++) {
                        DailyForecast dailyForecast = dailyForecastList.get(i);
                        View v = LayoutInflater.from(WeatherActivity.this).inflate(R.layout.forecast_item, null, false);
                        TextView item_date_text=v.findViewById(R.id.item_date_text);
                        TextView item_max_text=v.findViewById(R.id.item_max_text);
                        TextView item_min_text=v.findViewById(R.id.item_min_text);
                        ImageView item_iv_day_con=v.findViewById(R.id.item_iv_day_con);
                        ImageView item_iv_night_con=v.findViewById(R.id.item_iv_night_con);
                        item_date_text.setText(dailyForecast.date);
                        item_max_text.setText(dailyForecast.tmp_max+"℃");
                        item_min_text.setText(dailyForecast.tmp_min+"℃");
                        updateWeatherIcon(dailyForecast.cond_code_d,item_iv_day_con);
                        updateWeatherIcon(dailyForecast.cond_code_n,item_iv_night_con);
                        forecastLayout.addView(v);

                    }

                }
            }
        });
    }
}