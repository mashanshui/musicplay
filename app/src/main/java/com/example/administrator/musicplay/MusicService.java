package com.example.administrator.musicplay;

/**
 * Created by Administrator on 2016/11/20.
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.administrator.musicplay.adapter.MusicItem;
import com.example.administrator.musicplay.scanMusic.ScannerMusic;

public class MusicService extends Service implements MediaPlayer.OnCompletionListener,MediaPlayer.OnBufferingUpdateListener,MediaPlayer.OnPreparedListener{

    /**
     * 音乐播放器控件
     */
    public MediaPlayer player = null;

    /**
     * 播放音乐的路径和名字
     */
    private String path,name;

    /**
     * 扫描出来的音乐集合
     */
    private List<MusicItem> list = new ArrayList<>();

    /**
     * 服务内部的binder实例
     */
    private PlayMusic mbinder = new PlayMusic();

    private SeekBar playSeek;
    private TextView SallTime,ScurrentTime,musicname;

    /**
     * 通知
     */
    private Notification.Builder builder;

    Handler handler = new Handler();
    Handler timeHandler=new Handler();

    /**
     * @param mp
     * 监听播放完成的事件
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        handler.removeCallbacks(runnable);
        timeHandler.removeCallbacks(timeRunable);
        //nextPlay();
        System.out.println("oncompletion");
    }

    /**
     * 播放下一首音乐
     */
    private void nextPlay() {
        list = ScannerMusic.scanner(this);
        int count=list.size();
        int random=(int)(Math.random()*count);
        name=list.get(random).getMusicName();
        path=list.get(random).getMusicPath();
        player.reset();
        try {
            player.setDataSource(path);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        playSeek.setMax(player.getDuration());
        playSeek.setSecondaryProgress(percent*player.getDuration());
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        handler.postDelayed(runnable, 600);
        timeHandler.postDelayed(timeRunable, 1000);
        musicname.setText(name);
        mp.start();
        builder.setContentText(name);
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    class PlayMusic extends Binder {

        /**
         * 控制播放音乐
         */
        public void play() {
            Log.i("info", "播放音乐");
            player.start();
        }

        /**
         * 控制暂停音乐
         */
        public void pause() {
            Log.i("info", "暂停音乐");
            player.pause();
        }

        /**
         * 控制播放下一首音乐
         */
        public void next() {
            Log.i("info", "下一首");
            nextPlay();
        }
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        System.out.println("onCreate");

        //建立前台服务
        builder = new Notification.Builder(this);
        Intent intent=new Intent(this, MainActivity.class);
        //设置当点击Notification时，跳转到的Activity页面时不掉用onCreate()
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,intent, 0);
        builder.setContentIntent(contentIntent);
        builder.setSmallIcon(R.drawable.logo);
        builder.setTicker("音乐播放器");
        builder.setContentTitle("Music play");
        builder.setContentText("正在播放");
        Notification notification = builder.build();
        startForeground(1, notification);

        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnBufferingUpdateListener(this);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        //回调mainactivity中的seekbar,alltime,currenttime
        MainActivity.sendSeekBar(new GetSeekBar() {
            @Override
            public void getSeekBar(SeekBar seekBar) {
                playSeek = seekBar;
            }

            @Override
            public void getAllTime(TextView allTime) {
                SallTime=allTime;
            }

            @Override
            public void getCurrentTime(TextView currentTime) {
                ScurrentTime=currentTime;
            }

            @Override
            public void getMusicName(TextView musicName) {
                musicname=musicName;
            }
        });
        SeekBarListen();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        System.out.println("onBind");
        return mbinder;
    }

    /**
     * @param intent 开启服务时使用的intent实例
     * @param flags
     * @param startId
     * @return
     * 每次开启服务都会重新调用这个方法
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("onStartCommand");
        path = intent.getStringExtra("path");
        name = intent.getStringExtra("name");
        System.out.println(path);
        if (path != "" && path != null) {
            player.reset();
            System.out.println("重置");
            try {
                player.setDataSource(path);
                player.prepareAsync();
                System.out.println("准备完成");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 进度条的监听事件
     */
    private void SeekBarListen() {
        playSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * @param seekBar 当前滑动的进度条
             * @param progress 滑动的进度
             * @param fromUser 是否是人为滑动（因为进度条播放是会自己前进）
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //判断是否是人为滑动，如果是则同步播放的进度
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    //进度条
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(player.getCurrentPosition()==player.getDuration()){
                handler.removeCallbacks(this);
            }else{
                playSeek.setMax(player.getDuration());
                playSeek.setProgress(player.getCurrentPosition());
                handler.postDelayed(this,600);
            }
        }
    };

    //时间
    private Runnable timeRunable=new Runnable() {
        @Override
        public void run() {
            int time= player.getDuration();
            int second=(time/1000)%60;
            int minute=(time/1000)/60;
            SallTime.setText(String.valueOf(minute)+":"+String.valueOf(second));
            int currentTime=player.getCurrentPosition();
            int CurrentSecond=(currentTime/1000)%60;
            int CurrentMinute=(currentTime/1000)/60;
            ScurrentTime.setText(String.valueOf(CurrentMinute)+":"+String.valueOf(CurrentSecond));
            if(time!=currentTime){
                timeHandler.postDelayed(this,1000);
            }
        }
    };
//    class SeekAsyncTask extends AsyncTask<Void, Integer, Void> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            playSeek.setMax(player.getDuration());
//            Log.i("info", "设置进度条最大值" + player.getDuration());
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            while (player.getCurrentPosition() < player.getDuration()) {
//                publishProgress(player.getCurrentPosition());
//                Log.i("info", String.valueOf(player.getCurrentPosition()));
//                try {
//                    Thread.sleep(600);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... values) {
//            super.onProgressUpdate(values);
//            playSeek.setProgress(values[0]);
//        }
//
//    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        handler.removeCallbacks(runnable);
        timeHandler.removeCallbacks(timeRunable);
        player.stop();
        player.release();
        stopForeground(true);
    }

}
