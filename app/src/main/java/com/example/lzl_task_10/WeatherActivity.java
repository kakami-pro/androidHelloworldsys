package com.example.lzl_task_10;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.lzl_task_10.data.AirQualityData;
import com.example.lzl_task_10.data.WeatherForecast;
import com.example.lzl_task_10.data.WeatherNow;
import com.example.lzl_task_10.utility.WeatherApiUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class WeatherActivity extends AppCompatActivity {
    ImageView iv_cond;
    String weather_id="CN101210701";
    TextView tv_city,tv_update_time,tv_temp,tv_weather_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather);
        tv_city=findViewById(R.id.title_city_tv);
        tv_update_time=findViewById(R.id.title_pub_time_tv);
        tv_temp = findViewById(R.id.now_temp_tv);
        tv_weather_info = findViewById(R.id.now_cond_tv);
        iv_cond=findViewById(R.id.now_cond_iv);
        Button bt=findViewById(R.id.bt_update);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
updateWeatherNow();
            }
        });
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
}