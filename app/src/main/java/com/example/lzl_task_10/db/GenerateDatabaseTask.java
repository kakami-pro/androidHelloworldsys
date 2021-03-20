package com.example.lzl_task_10.db;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.widget.Toast;

import com.example.lzl_task_10.data.City;
import com.example.lzl_task_10.utility.HttpUtil;
import com.example.lzl_task_10.utility.JsonUtil;

import java.util.List;

public class GenerateDatabaseTask extends AsyncTask<Void,Integer,Integer> {
    private Activity activity;
    private ProgressDialog progressDialog;
    private String baseurl="http://guolin.tech/api/china";
    private CityDatabase cityDatabase;

    public GenerateDatabaseTask(Activity activity,CityDatabase cityDatabase) {
        this.cityDatabase=cityDatabase;
        this.activity = activity;
    }

    @Override
    protected Integer doInBackground(Void... voids) {
        int count=0;
        String ack = HttpUtil.getOkhttpBlock(baseurl);
        List<City> provinceList = JsonUtil.getCityListFromJson(ack, -1, 0);
        if (provinceList!=null&&provinceList.size()>0)
        {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initProgressDialog(provinceList.size());
                }
            });
            cityDatabase.clearDatabase();
            count=cityDatabase.insertList(provinceList);
            for (int i = 0; i < provinceList.size(); i++) {
                City province = provinceList.get(i);
                int id = province.getId();
                String urlprovince = String.format("%s/%d", baseurl, id);
                String s = HttpUtil.getOkhttpBlock(urlprovince);
                List<City> citylist = JsonUtil.getCityListFromJson(s, id, 1);
                count=count+cityDatabase.insertList(citylist);
                for (int j = 0; j < citylist.size(); j++) {
                    City city = citylist.get(j);
                    int id1 = city.getId();
                    String urlcity = String.format("%s/%d", urlprovince, id1);
                    String s1 = HttpUtil.getOkhttpBlock(urlcity);
                    List<City> countrylist = JsonUtil.getCityListFromJson(s1, id1, 2);
                    count+=cityDatabase.insertList(countrylist);
                    publishProgress(i,count);
                }
                publishProgress(i+1,count);
            }
        }
        return count;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        progressDialog.dismiss();
        showToast(String.format("Insert %d data to database",integer));
    }

    private void initProgressDialog(int max)
    {
        progressDialog=new ProgressDialog(activity);
        progressDialog.setTitle("Generating database");
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Inserted data:");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(max);
        progressDialog.setProgress(0);
        progressDialog.show();
    }
    private void showToast(String format) {
        Toast.makeText(activity,format,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        progressDialog.setProgress(values[0]);
        progressDialog.setMessage(String.format("Insert data:%d",values[1]));
    }
}
