package com.example.lzl_task_10.utility;

import com.example.lzl_task_10.data.City;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    public static List<City> getCityListFromJson(String json, int parentId,int level)
    {
        ArrayList<City> list=null;
        try {
            list=new ArrayList<>();
            JSONArray jsonArray = new JSONArray(json);
            for (int i = 0; i < jsonArray.length(); i++) {
                String s = jsonArray.get(i).toString();
                City city=new Gson().fromJson(s, City.class);
                String name = city.getName();
                String enName = PingyinUtil.toPingyin(name);
                String initalName = PingyinUtil.toPingyinFirstLetter(name);
                city.setEnName(enName);
                city.setLevel(level);
                city.setInitialName(initalName);
                city.setParentId(parentId);
                list.add(city);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static String getJsonfromCityList(List<City> list){
        Gson gson=new Gson();
        String s = gson.toJson(list);
        return s;
    }
}
