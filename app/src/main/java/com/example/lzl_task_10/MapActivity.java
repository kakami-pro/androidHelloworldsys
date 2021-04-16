package com.example.lzl_task_10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.TextOptions;
import com.example.lzl_task_10.data.City;
import com.example.lzl_task_10.data.LiteCity;
import com.example.lzl_task_10.data.MarkerWeather;
import com.example.lzl_task_10.db.CityDatabase;
import com.example.lzl_task_10.db.MarkerDatabase;
import com.example.lzl_task_10.view.MyInfoWindowAdapter;
import com.example.lzl_task_10.utility.HeFenUtil;
import com.qweather.sdk.bean.air.AirNowBean;
import com.qweather.sdk.bean.geo.GeoBean;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.QWeather;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity {
    AMap aMap=null;
    MapView mMapView = null;
    Marker markers=null;
    int zoom_level=0;
    CityDatabase database;
    MarkerDatabase markerDatabase;
    List<Marker> level0,level1,level2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.map_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        markerDatabase=new MarkerDatabase(this);
        markerDatabase.open();
        database=new CityDatabase(this);
        database.open();


        mMapView = (MapView) findViewById(R.id.mapWebView);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        aMap.setInfoWindowAdapter(new MyInfoWindowAdapter(this));
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (markers!=null){
                    markers.hideInfoWindow();
                    markers.destroy();
                }
                markers = aMap.addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));
                MarkerWeather markerWeather=new MarkerWeather();
                markerWeather.setLatitude(latLng.latitude);
                markerWeather.setLongitude(latLng.longitude);
                markers.setObject(markerWeather);
                markers=setMarker(markers);
                markers.showInfoWindow();
            }
        });
        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                setMarker(marker);
                marker.showInfoWindow();
                return false;
            }
        });

        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if (!cameraPosition.isAbroad){
//                    System.out.println(cameraPosition.zoom);
                    if (cameraPosition.zoom>=9){
                        if (zoom_level!=2){
                            zoom_level=2;
                            refreshCityMarker();
                        }
                    }
                    else if (cameraPosition.zoom>=6){
                        if (zoom_level!=1){
                            zoom_level=1;
                            refreshCityMarker();
                        }
                    }
                    else {
                        if (zoom_level!=0){
                            zoom_level=0;
                            refreshCityMarker();
                        }
                    }
                }
            }
        });

        updateAllMarker();
    }

    private void refreshCityMarker(){
            switch (zoom_level) {
                case 1:
                    for (Marker marker : level2) {
                        marker.setZIndex(5);
                        marker.setVisible(false);
                    }
                    for (Marker marker : level0) {
                        marker.setZIndex(5);
                        marker.setVisible(false);
                    }
                    for (Marker marker : level1) {
                        marker.setZIndex(15);
                        marker.setVisible(true);
                    }
                    break;
                case 2:
                    for (Marker marker : level1) {
                        marker.setZIndex(5);
                        marker.setVisible(false);
                    }
                    for (Marker marker : level0) {
                        marker.setZIndex(5);
                        marker.setVisible(false);
                    }
                    for (Marker marker : level2) {
                        marker.setZIndex(15);
                        marker.setVisible(true);
                    }
                    break;
                case 0:
                    for (Marker marker : level2) {
                        marker.setZIndex(5);
                        marker.setVisible(false);
                    }
                    for (Marker marker : level1) {
                        marker.setZIndex(5);
                        marker.setVisible(false);
                    }
                    for (Marker marker : level0) {
                        marker.setZIndex(15);
                        marker.setVisible(true);
                    }
                    break;
            }

    }

    private void updateCityinfoBytext() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<City> cityofLevel2 = database.getCityofLevel2(zoom_level);
                for (City city : cityofLevel2) {
                    HeFenUtil.setGEO(MapActivity.this, city.getName(), new QWeather.OnResultGeoListener() {
                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onSuccess(GeoBean geoBean) {
                            GeoBean.LocationBean locationBean = geoBean.getLocationBean().get(0);
                            LatLng latLng = new LatLng(Double.parseDouble(locationBean.getLat()),Double.parseDouble(locationBean.getLon()) );
                            TextOptions textOptions = new TextOptions();
                            textOptions.position(latLng);
                            setWeatherInfo(textOptions,latLng);

                        }
                    });
                }
            }
        }).start();
    }
    private void showToast(String s)
    {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
    }

    private void updateAllMarker(){
        level0=new ArrayList<>();
        level1=new ArrayList<>();
        level2=new ArrayList<>();
        updateCityinfo(0);
        updateCityinfo(1);
        updateCityinfo(2);

    }

    private void updateCityinfo(int level) {
        List<LiteCity> liteCities = markerDatabase.queryBylevel(level);
        if (liteCities.size()==0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<City> cityofLevel2 = database.getCityofLevel2(level);
                    for (City city : cityofLevel2) {

                        HeFenUtil.setGEO(MapActivity.this, city.getName(), new QWeather.OnResultGeoListener() {
                            @Override
                            public void onError(Throwable throwable) {
//                            Toast.makeText(MapActivity.this,throwable.getMessage(),Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onSuccess(GeoBean geoBean) {
                                GeoBean.LocationBean locationBean = geoBean.getLocationBean().get(0);
                                LatLng latLng = new LatLng(Double.parseDouble(locationBean.getLat()),Double.parseDouble(locationBean.getLon()) );
                                Marker defaultMarker = aMap.addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));

                                switch (level) {
                                    case 0:
                                        level0.add(defaultMarker);
                                        break;
                                    case 1:
                                        level1.add(defaultMarker);
                                        break;
                                    case 2:
                                        level2.add(defaultMarker);
                                        break;
                                }
                                markerDatabase.insertData(city.getId(),city.getName(),city.getLevel(),latLng.longitude,latLng.latitude);
                                MarkerWeather markerWeather=new MarkerWeather();
                                markerWeather.setLongitude(latLng.longitude);
                                markerWeather.setLatitude(latLng.latitude);
                                defaultMarker.setObject(markerWeather);

                            }
                        });
                    }
                }
            }).start();
        }
        else {
            List<LiteCity> liteCities1 = markerDatabase.queryBylevel(level);

            for (LiteCity city : liteCities1) {
                LatLng latLng = new LatLng(city.getLatitude(), city.getLongitude());
                Marker defaultMarker = aMap.addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));
                MarkerWeather markerWeather=new MarkerWeather();
                markerWeather.setLongitude(city.getLongitude());
                markerWeather.setLatitude(city.getLatitude());
                defaultMarker.setObject(markerWeather);
                switch (level) {
                    case 0:
                        level0.add(defaultMarker);
                        break;
                    case 1:
                        level1.add(defaultMarker);
                        break;
                    case 2:
                        level2.add(defaultMarker);
                        break;
                }
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public MarkerWeather setWeatherInfo(TextOptions textOptions,LatLng latLng)
    {
        String format = String.format("%f,%f", latLng.longitude, latLng.latitude);
        MarkerWeather markerWeather = new MarkerWeather();
        HeFenUtil.getWeatherNow(this, format, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {
                showToast(throwable.getMessage());

            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                String temp = weatherNowBean.getNow().getTemp();
                markerWeather.setTemperature(temp);
                HeFenUtil.getAirQualityData(MapActivity.this, format, new QWeather.OnResultAirNowListener() {
                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(AirNowBean airNowBean) {
                        String aqi = airNowBean.getNow().getAqi();
                        markerWeather.setQuality(aqi);
                        textOptions.text(String.format("温度：%s℃\n空气质量：%s",markerWeather.getTemperature(),markerWeather.getQuality()));
                        aMap.addText(textOptions);
                    }
                });
            }
        });

        return markerWeather;
    }

    public Marker setMarker(Marker marker){
        MarkerWeather markerWeather = (MarkerWeather) marker.getObject();
        String format = String.format("%f,%f", markerWeather.getLongitude(), markerWeather.getLatitude());
//        marker = aMap.addMarker(new MarkerOptions().position(latLng).snippet("DefaultMarker"));
        if (markerWeather.getQuality()!=null&&markerWeather.getTemperature()!=null)
        {
            marker.showInfoWindow();
            return marker;
        }
        HeFenUtil.getWeatherNow(this, format, new QWeather.OnResultWeatherNowListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(WeatherNowBean weatherNowBean) {
                String temp = weatherNowBean.getNow().getTemp();
                markerWeather.setTemperature(temp);
                marker.showInfoWindow();


            }
        });
        HeFenUtil.getAirQualityData(this, format, new QWeather.OnResultAirNowListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(AirNowBean airNowBean) {
                String aqi = airNowBean.getNow().getAqi();
                markerWeather.setQuality(aqi);
                marker.showInfoWindow();
            }
        });
        return marker;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}