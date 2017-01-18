package com.example.administrator.musicplay.internet;


import com.example.administrator.musicplay.internet.HttpCallbackListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2016/12/21.
 */

public class HttpUtil {
    public static void sendHttpRequest(final String address,final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                HttpURLConnection conn=null;
                URL url ;
                try {
                    url = new URL(address);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);
                    conn.setReadTimeout(5000);
                    conn.setRequestMethod("GET");
                    InputStream in=conn.getInputStream();
                    BufferedReader br=new BufferedReader(new InputStreamReader(in));
                    String response="";
                    String line;
                    while((line=br.readLine())!=null){
                        response+=line;
                    }
                    if(listener!=null){
                        listener.onFinish(response.toString());
                    }
                } catch (IOException e) {
                    if(listener!=null){
                        listener.onError(e);
                    }
                }finally {
                    if(conn!=null){
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
