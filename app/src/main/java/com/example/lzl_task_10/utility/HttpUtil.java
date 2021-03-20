package com.example.lzl_task_10.utility;

import android.app.Activity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    public interface SimpleAsyncCall{
         void onFailure(String e);
         void onResponse(String s);
    }
    public static String getOkhttpBlock(String url)
    {
        OkHttpClient client=new OkHttpClient();
        Request build = new Request.Builder().get().url(url).build();
        Call call = client.newCall(build);
        try {
            Response re = call.execute();
            String string = re.body().string();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getOkHttpAsync(final Activity activity,String url, SimpleAsyncCall l){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request build = new Request.Builder().url(url).get().build();
        Call call = okHttpClient.newCall(build);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        l.onFailure(e.toString());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String res;
                try {
                    res=response.body().string();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            l.onResponse(res);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
