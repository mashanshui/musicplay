package com.example.administrator.musicplay.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.musicplay.DBHelper.CollectionBook;
import com.example.administrator.musicplay.MusicService;
import com.example.administrator.musicplay.R;
import com.example.administrator.musicplay.internet.HttpCallbackListener;
import com.example.administrator.musicplay.internet.HttpUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */

public class CollectionFragment extends Fragment{
    @Nullable
    /**
     * 用与显示音乐
     */
    private RecyclerView recyclerView;

    /**
     * 加载布局的View
     */
    private View view;

    /**
     * 用于开启服务的intent
     */
    private Intent service;

    private IntentFilter intentFilter;

    private UpdataReceiver receiver;

    private String musicUrl,name;

    private List<CollectionBook> musicList;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){}
            service = new Intent(getActivity(), MusicService.class);
            service.putExtra("path",musicUrl);
            service.putExtra("name",name);
            getActivity().startService(service);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("info","2oncreateview" );
        view=inflater.inflate(R.layout.collection_fragment,container,false);

        recyclerView = (RecyclerView) view.findViewById(R.id.collectionMusic);
        quaryDatabase();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        CollectionAdapter adapter = new CollectionAdapter(musicList);
        adapter.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                name=musicList.get(position).getSongName();
                getUrlMusic(musicList.get(position).getHash());
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void quaryDatabase() {
        musicList = DataSupport.findAll(CollectionBook.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info","2oncreate" );
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.collection.music.CollectionUPDATE");
        receiver=new UpdataReceiver();
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("info","2onActivityCreated" );
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("info", "2setUserVisibleHint");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    private void getUrlMusic(final String hash) {

        String url="http://m.kugou.com/app/i/getSongInfo.php?hash=" + hash + "&cmd=playInfo";
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
            response=response.replace("<!--KG_TAG_RES_START-->","");
            response=response.replace("<!--KG_TAG_RES_END-->","");
            //Log.i("info", response);
            //将服务器返回的数据传入到一个JSONArray对象中
            JSONObject object = new JSONObject(response);
            musicUrl=object.getString("url");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class UpdataReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            quaryDatabase();
            CollectionAdapter a=new CollectionAdapter(musicList);
            a.setOnItemClickListener(new CollectionAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    name=musicList.get(position).getSongName();
                    getUrlMusic(musicList.get(position).getHash());
                }
            });
            recyclerView.setAdapter(a);
        }
    }
}
