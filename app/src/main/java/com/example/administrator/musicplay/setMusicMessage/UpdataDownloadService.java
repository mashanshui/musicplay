package com.example.administrator.musicplay.setMusicMessage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.administrator.musicplay.DBHelper.DownloadBook;
import com.example.administrator.musicplay.internet.HttpCallbackListener;
import com.example.administrator.musicplay.internet.HttpUtil;
import com.example.administrator.musicplay.scanMusic.ScannerActivity;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.NotSupportedException;
import com.mpatric.mp3agic.UnsupportedTagException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import static android.R.attr.path;

public class UpdataDownloadService extends Service {

    private String songName;
    private String singerName;
    private String fileName;
    private String directory;
    private String path;
    private int timeLength;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {
                File file = new File(path);
                File newFile=new File(directory + "/" + fileName + ".mp3");
                if(newFile.exists()){
                    newFile.delete();
                }else {
                    file.renameTo(newFile);
                }

                addMessageInDatabase();

                MediaScannerConnection.scanFile(UpdataDownloadService.this, new String[]{newFile.getPath()},
                        null, new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                stopSelf();
                            }
                        });
//                Mp3File mp3File= null;
//                try {
//                    mp3File = new Mp3File(path);
//                    ID3v2 id3v2Tag;
//                    if (mp3File.hasId3v2Tag()) {
//                        id3v2Tag = mp3File.getId3v2Tag();
//                    } else {
//                        // mp3 does not have an ID3v2 tag, let's create one..
//                        id3v2Tag = new ID3v24Tag();
//                        mp3File.setId3v2Tag(id3v2Tag);
//                    }
//                    id3v2Tag.setArtist(singerName);
//                    id3v2Tag.setTitle(songName);
//
//                    File file = new File(directory + "/" + fileName + ".mp3");
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                    mp3File.save(directory + "/" + fileName + ".mp3");
//
//                    File oldMusicFile = new File(path);
//                    oldMusicFile.delete();
//
//                    stopSelf();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (NotSupportedException e) {
//                    e.printStackTrace();
//                } catch (UnsupportedTagException e) {
//                    e.printStackTrace();
//                } catch (InvalidDataException e) {
//                    e.printStackTrace();
//                }
            }
        }
    };

    private void addMessageInDatabase(){
        DownloadBook book=new DownloadBook();
        book.setPath(directory + "/" + fileName + ".mp3");
        book.setSongName(songName);
        book.setSingerName(singerName);
        book.setDuration(timeLength);
        book.save();
        Intent intent=new Intent("android.collection.music.DownloadUPDATE");
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }



    @Override
    public void onCreate() {
        super.onCreate();
        directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String hash = intent.getStringExtra("hash");
        path = intent.getStringExtra("path");
        Log.i("up", path);

        getUrlMusic(hash);
        return super.onStartCommand(intent, flags, startId);
    }

    private void getUrlMusic(final String hash) {

        String url = "http://m.kugou.com/app/i/getSongInfo.php?hash=" + hash + "&cmd=playInfo";
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                parsePlayJSON(response);
                Message m = new Message();
                m.what = 1;
                handler.sendMessage(m);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void parsePlayJSON(String response) {
        try {
            response = response.replace("<!--KG_TAG_RES_START-->", "");
            response = response.replace("<!--KG_TAG_RES_END-->", "");
            //Log.i("info", response);
            //将服务器返回的数据传入到一个JSONArray对象中
            JSONObject object = new JSONObject(response);
            songName = object.getString("songName");
            singerName = object.getString("singerName");
            fileName = object.getString("fileName");
            timeLength=object.getInt("timeLength");
//            Log.i("up",String.valueOf(timeLength));
//            Log.i("up",songName);
//            Log.i("up",fileName);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
