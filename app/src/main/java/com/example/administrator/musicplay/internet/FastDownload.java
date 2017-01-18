package com.example.administrator.musicplay.internet;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Administrator on 2016/12/24.
 */

public class FastDownload {

    private Context context;
    private Intent intent;

    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder= (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    public FastDownload(Context context) {
        this.context = context;
        intent=new Intent(context,DownloadService.class);
        context.startService(intent);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.i("info", "开启服务");
    }

    public void download(String downloadUrl,String hash) {
        if(downloadBinder==null){
            return;
        }
        intent.putExtra("hash", hash);
        context.startService(intent);
        downloadBinder.startDownload(downloadUrl);
    }

    public void stopDownloadService() {
        if(downloadBinder!=null){
            context.unbindService(connection);
        }
    }
}
