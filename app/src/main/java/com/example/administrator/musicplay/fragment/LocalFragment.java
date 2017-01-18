package com.example.administrator.musicplay.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.administrator.musicplay.MusicService;
import com.example.administrator.musicplay.R;
import com.example.administrator.musicplay.scanMusic.ScannerMusic;
import com.example.administrator.musicplay.adapter.MusicAdapter;
import com.example.administrator.musicplay.adapter.MusicItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/27.
 */

public class LocalFragment extends Fragment implements AdapterView.OnItemClickListener{
    @Nullable

    /**
     * 用与显示音乐的listview
     */
    private ListView localList;

    /**
     * 加载布局的View
     */
    private View view;

    /**
     * 存放扫描出来的音乐信息的数组
     */
    private List<MusicItem> musicItem = new ArrayList<>();

    /**
     * 用于开启服务的intent
     */
    private Intent service;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Log.i("info", "createview");
        view = inflater.inflate(R.layout.local_fragment, container, false);
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else {
            load();
        }

        return view;
    }

    private void load() {
        musicItem = ScannerMusic.scanner(getActivity());
        localList = (ListView) view.findViewById(R.id.localList);
        MusicAdapter adapter=new MusicAdapter(musicItem,getActivity());
        localList.setAdapter(adapter);
        localList.setOnItemClickListener(this);
        service = new Intent(getActivity(), MusicService.class);
        service.putExtra("path","");
        getActivity().startService(service);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    //Log.i("info","同意授权" );
                    load();
                }else {
                    Toast.makeText(getActivity(),"拒绝将不能使用",Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.i("info", "oncreate");
//        musicItem = ScannerMusic.scanner(getActivity());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // Log.i("info", "activitycreate");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String path=musicItem.get(position).getMusicPath();
        String name=musicItem.get(position).getMusicName();
        //Log.i("info","main"+path);
        service.putExtra("path",path);
        service.putExtra("name", name);
        getActivity().startService(service);
    }

    /**
     *  用于在activity中修改listview的数据
     */
    public void loadScanMusic(){
        musicItem = ScannerMusic.scanner(getActivity());
        MusicAdapter adapter=new MusicAdapter(musicItem,getActivity());
        localList.setAdapter(adapter);
        localList.setOnItemClickListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        //Log.i("info", "onstop");
    }

    @Override
    public void onDestroyView() {
        //Log.i("info", "ondestroyview");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Log.i("info", "ondestroy");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //Log.i("info", "1setUserVisibleHint");
    }
}
