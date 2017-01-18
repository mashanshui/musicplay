package com.example.administrator.musicplay.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.musicplay.DBHelper.CollectionBook;
import com.example.administrator.musicplay.DBHelper.DownloadBook;
import com.example.administrator.musicplay.MusicService;
import com.example.administrator.musicplay.R;

import org.litepal.crud.DataSupport;

import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */

public class DownloadFragment extends Fragment {
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


    private List<DownloadBook> musicList;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.download_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.downloadList);
        quaryDatabase();
        service = new Intent(getActivity(), MusicService.class);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        DownloadAdapter adapter=new DownloadAdapter(musicList);
        adapter.setOnItemClickListener(new DownloadAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                service.putExtra("path",musicList.get(position).getPath());
                service.putExtra("name",musicList.get(position).getSongName());
                getActivity().startService(service);
            }
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void quaryDatabase() {
        musicList = DataSupport.findAll(DownloadBook.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.collection.music.DownloadUPDATE");
        receiver=new UpdataReceiver();
        getActivity().registerReceiver(receiver, intentFilter);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.i("info", "3setUserVisibleHint");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }

    class UpdataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            quaryDatabase();
            DownloadAdapter adapter = new DownloadAdapter(musicList);
            adapter.setOnItemClickListener(new DownloadAdapter.OnItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    service.putExtra("path",musicList.get(position).getPath());
                    service.putExtra("name",musicList.get(position).getSongName());
                    getActivity().startService(service);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }
}
