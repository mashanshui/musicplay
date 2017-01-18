package com.example.administrator.musicplay.adapter;

/**
 * Created by Administrator on 2016/11/21.
 */

public class MusicItem {
    private int musicduration;
    private String musicName;
    private String musicArtist;
    private String musicPath;

    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    public int getMusicduration() {
        return musicduration;
    }

    public void setMusicduration(int musicduration) {
        this.musicduration = musicduration;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getMusicArtist() {
        return musicArtist;
    }

    public void setMusicArtist(String musicArtist) {
        this.musicArtist = musicArtist;
    }
}
