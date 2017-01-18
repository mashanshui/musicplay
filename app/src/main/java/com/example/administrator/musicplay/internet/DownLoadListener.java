package com.example.administrator.musicplay.internet;

/**
 * Created by Administrator on 2016/12/23.
 */

public interface DownLoadListener {
    void onProgress(int progress);

    void onSuccess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
