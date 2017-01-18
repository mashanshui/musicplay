package com.example.administrator.musicplay.DBHelper;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/1/10.
 */

public class DownloadBook extends DataSupport{
    private int id;
    private String songName;
    private String singerName;
    private String path;
    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSingerName() {
        return singerName;
    }

    public void setSingerName(String singerName) {
        this.singerName = singerName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
