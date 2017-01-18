package com.example.administrator.musicplay;

import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/11/27.
 */

public interface GetSeekBar {
    void getSeekBar(SeekBar seekBar);

    void getAllTime(TextView allTime);

    void getCurrentTime(TextView currentTime);

    void getMusicName(TextView musicName);

}
