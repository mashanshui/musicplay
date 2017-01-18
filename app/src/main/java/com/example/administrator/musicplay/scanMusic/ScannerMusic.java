package com.example.administrator.musicplay.scanMusic;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.administrator.musicplay.adapter.MusicItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class ScannerMusic {

    public static List<MusicItem> scanner(Context context) {
        List<MusicItem> list=new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,MediaStore.Audio.Media.SIZE + ">80000", null,
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();
           MusicItem mp3Info=new MusicItem();
            long id = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));               //音乐id
            String title = cursor.getString((cursor
                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));            //音乐标题
            String artist = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));            //艺术家
            int duration = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DURATION));          //时长
            long size = cursor.getLong(cursor
                    .getColumnIndex(MediaStore.Audio.Media.SIZE));              //文件大小
            String url = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));              //文件路径
            int isMusic = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));          //是否为音乐
            if (isMusic != 0) {     //只把音乐添加到集合当中
                mp3Info.setMusicName(title);
                mp3Info.setMusicArtist(artist);
                mp3Info.setMusicduration(duration);
                mp3Info.setMusicPath(url);
                list.add(mp3Info);
            }
        }
        return list;
    }
}
