package com.example.administrator.musicplay.internet;

/**
 * Created by Administrator on 2016/12/21.
 */

public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
