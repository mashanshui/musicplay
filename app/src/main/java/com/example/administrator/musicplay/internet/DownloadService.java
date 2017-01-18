package com.example.administrator.musicplay.internet;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.administrator.musicplay.MainActivity;
import com.example.administrator.musicplay.R;
import com.example.administrator.musicplay.setMusicMessage.UpdataDownloadService;

import java.io.File;

public class DownloadService extends Service {

    private DownloadTask downloadTask;
    private String downloadUrl,downloadHash;
    private DownloadBinder mybinder=new DownloadBinder();

    private DownLoadListener listener=new DownLoadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(2,getNotification("下载中...",progress));
        }

        @Override
        public void onSuccess() {
            downloadTask=null;
            //stopForeground(true);
            getNotificationManager().notify(2,getNotification("下载成功...",-1));
            sendBroadcastDownload();
            Toast.makeText(DownloadService.this,"下载成功",Toast.LENGTH_SHORT).show();
            updataDownloadMessage();
            stopSelf();
        }

        @Override
        public void onFailed() {
            downloadTask=null;
            //stopForeground(true);
            getNotificationManager().notify(2,getNotification("下载失败...",-1));
            Toast.makeText(DownloadService.this,"下载失败",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask=null;
            Toast.makeText(DownloadService.this,"暂停下载",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask=null;
            //stopForeground(true);
            Toast.makeText(DownloadService.this,"取消下载",Toast.LENGTH_SHORT).show();
        }
    };

    /**
     * 下载完成后修改音乐标签
     */
    private void updataDownloadMessage(){
        Intent intent = new Intent(DownloadService.this, UpdataDownloadService.class);
        intent.putExtra("hash", downloadHash);
        intent.putExtra("path", DownloadFilePath());
        startService(intent);
    }

    /**
     * 下载完成后发送广播
     */
    public void sendBroadcastDownload(){
        Intent intent = new Intent("com.example.broadcasttest.DOWNLOAD_SUCCESS");
        intent.putExtra("path",DownloadFilePath() );
        sendBroadcast(intent);

    }

    private String DownloadFilePath(){
        String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        File file = new File(directory + fileName);
        return file.getAbsolutePath();
    }

    public class DownloadBinder extends Binder{

        public void startDownload(String url){
            if(downloadTask==null){
                downloadUrl=url;
                downloadTask = new DownloadTask(listener,DownloadService.this);
                downloadTask.execute(downloadUrl);
                getNotificationManager().notify(2,getNotification("开始下载...",0));
                Toast.makeText(DownloadService.this,"开始下载",Toast.LENGTH_SHORT).show();
            }
        }

        public void pauseDownload(){
            if(downloadTask!=null){
                downloadTask.pauseDownload();
            }
        }

        public void cancelDownload(){
            if(downloadTask!=null){
                downloadTask.cancelDownload();
            }else {
                if(downloadUrl!=null){
                    String fileName=downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if(file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(2);
                    Toast.makeText(DownloadService.this,"已取消下载",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mybinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.hasExtra("hash")){
            downloadHash = intent.getStringExtra("hash");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title,int progress){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("isJump",true);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //将图片转换成bitmap格式
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentIntent(pendingIntent);
        builder.setContentTitle(title);
//        if(progress>0){
//            builder.setContentText(progress+"%");
//            builder.setProgress(100,progress,false);
//        }
        return builder.build();
    }

    /**
     * @return sd卡是否挂载
     */
    public boolean ExistSDCard(){
        if (android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }else {
            return false;
        }
    }
}
