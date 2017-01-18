package com.example.administrator.musicplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.administrator.musicplay.DBHelper.CollectionBook;
import com.example.administrator.musicplay.adapter.SearchAdapter;
import com.example.administrator.musicplay.adapter.ShowAdapter;
import com.example.administrator.musicplay.adapter.ShowItem;
import com.example.administrator.musicplay.internet.FastDownload;
import com.example.administrator.musicplay.internet.HttpCallbackListener;
import com.example.administrator.musicplay.internet.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.tablemanager.Connector;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;

/**
 * Created by Administrator on 2016/12/2.
 */

public class SearchActivity extends Activity{

    /**
     * 两个RecyclerView控件
     */
    private RecyclerView showSearch,showResult;

    /**
     * 网络加载时显示的进度条（环形）
     */
    private ProgressBar loadProgress;

    /**
     * 输入搜索歌名
     */
    private EditText editMusic;

    /**
     * 搜索歌曲按钮
     */
    private Button searchMusic;

    /**
     * 返回主界面按钮
     */
    private Button returnButton;

    /**
     * 搜索提示的集合
     */
    private List<String> keyWordList;

    /**
     * 搜索之后显示的集合
     */
    private List<ShowItem> showMusicList;

    /**
     * 音乐的链接，图片的链接，音乐名
     */
    private String musicUrl,imageUrl,musicName;

    /**
     * 开启服务的intent
     */
    private Intent serviceIntent;

    /**
     * 手势
     */
    private GestureDetector myGestureDetector;

    /**
     * 获取输入法管理
     */
    private InputMethodManager inputMethodManager;

    /**
     * 下载类
     */
    private FastDownload fastDownload;

    /**
     * 下载音乐的hsah
     */
    private String hash;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //当输入搜索关键字时显示加载的数据
            if(msg.what==1){
                //隐藏显示搜索结果的列表
                showSearch.setVisibility(View.VISIBLE);
                //显示搜索关键字的列表
                showResult.setVisibility(View.GONE);
                SearchAdapter searchAdapter = new SearchAdapter(keyWordList);

                //当点击列表项时加载点击的歌曲名
                searchAdapter.setOnItemClickListener(new SearchAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String name = keyWordList.get(position);
                        //在加载之前清空之前的数据
                        showMusicList.clear();
                        loadProgress.setVisibility(View.VISIBLE);
                        getShowMusic(name);
                    }

                });
                showSearch.setAdapter(searchAdapter);

             //当点击搜索时显示加载的数据
            }else if(msg.what==2){
                loadProgress.setVisibility(View.GONE);

                showSearch.setVisibility(View.GONE);
                showResult.setVisibility(View.VISIBLE);
                ShowAdapter showAdapter=new ShowAdapter(showMusicList);
                showAdapter.setOnItemClickListener(new ShowAdapter.OnItemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        String hash = showMusicList.get(position).getHash();
                        musicName = showMusicList.get(position).getSongname();
                        loadProgress.setVisibility(View.VISIBLE);
                        getplayMusic(hash);
                    }

                    @Override
                    public void downloadClick(View view, int position) {
                        hash=showMusicList.get(position).getHash();
                        getUrlMusic(hash);
                    }

                    @Override
                    public void collectionClick(View view, int position) {
                        CollectionBook book=new CollectionBook();
                        ShowItem item=showMusicList.get(position);
                        book.setSongName(item.getSongname());
                        book.setSingerName(item.getSingername());
                        book.setDuration(item.getDuration());
                        book.setHash(item.getHash());
                        book.save();
                        Toast.makeText(SearchActivity.this,"收藏成功",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent("android.collection.music.CollectionUPDATE");
                        sendBroadcast(intent);
                    }

                });
                showResult.setAdapter(showAdapter);

             //开启服务播放选择的音乐
            }else if(msg.what==3){
                loadProgress.setVisibility(View.GONE);
                serviceIntent.putExtra("path", musicUrl);
                serviceIntent.putExtra("name",musicName);
                startService(serviceIntent);

             //开始下载服务
            } else if (msg.what==4) {
                fastDownload.download(musicUrl,hash);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        loadProgress = (ProgressBar) findViewById(R.id.loadProgress);
        returnButton = (Button) findViewById(R.id.returnButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        keyWordList=new ArrayList<>();
        showMusicList = new ArrayList<>();
        InitRecyclerView();

        editMusic = (EditText) findViewById(R.id.editMusic);
        editMusic.addTextChangedListener(new EditChangedListener());
        searchMusic = (Button) findViewById(R.id.searchMusic);
        searchMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMusic.getText().toString()!=null){
                    showMusicList.clear();
                    loadProgress.setVisibility(View.VISIBLE);
                    getShowMusic(editMusic.getText().toString());
                }
            }
        });
        fastDownload = new FastDownload(this);
        serviceIntent = new Intent(SearchActivity.this, MusicService.class);
        slideInit();
        Connector.getDatabase();
    }

    /**
     * 滑动返回
     */
    private void slideInit() {
        inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        myGestureDetector=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(e2.getX()-e1.getX()>350){
                    finish();
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        showSearch.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                inputMethodManager.hideSoftInputFromWindow(editMusic.getWindowToken(), 0);
                myGestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        showResult.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                inputMethodManager.hideSoftInputFromWindow(editMusic.getWindowToken(), 0);
                myGestureDetector.onTouchEvent(e);
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    /**
     * 初始化RecyclerView
     */
    private void InitRecyclerView() {
        showSearch = (RecyclerView) findViewById(R.id.showSearch);
        showResult = (RecyclerView) findViewById(R.id.showResult);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        showResult.setLayoutManager(layoutManager1);
        showSearch.setLayoutManager(layoutManager2);
    }


    /**
     * 监控edittext中的输入
     */
    class EditChangedListener implements TextWatcher{

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Log.i("info","前"+s.toString());
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
           // Log.i("info","中"+s.toString());
            keyWordList.clear();
            getSearchMusic(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {
            //Log.i("info","后"+s.toString());
        }
    }


    /**
     * @param keyword
     * 当用户在搜索框上输入的时候，给予的提醒
     */
    private void getSearchMusic(final String keyword) {

        String u = null;
        try {
            u = new String(keyword.getBytes(), "utf-8");
            u = URLEncoder.encode(u, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url="http://mobilecdn.kugou.com/new/app/i/search.php?cmd=302&keyword="+u+"&with_res_tag=1";

        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                parseSearchJSON(response);
                Message m = new Message();
                m.what = 1;
                handler.sendMessage(m);
            }

            @Override
            public void onError(Exception e) {

            }
        });

    }

    private void parseSearchJSON(String response) {
        try {
            response=response.replace("<!--KG_TAG_RES_START-->","");
            response=response.replace("<!--KG_TAG_RES_END-->","");
//            Log.i("info", response);
            //将服务器返回的数据传入到一个JSONArray对象中
            JSONObject start = new JSONObject(response);
            JSONArray jsonArray=start.getJSONArray("data");
            //遍历这个数组
            for(int i=0;i<jsonArray.length();i++){
                //从JsonArray中取出JSONObject对象
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                //获取JSONObject对象中的数据
//                String song=jsonObject.getString("songcount");
//                String search=jsonObject.getString("searchcount");
                String key=jsonObject.getString("keyword");
                keyWordList.add(key);
//                Log.i("info", song);
//                Log.i("info", search);
//                Log.i("info", key);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * @param keyword
     * 当用户查询的时候加载显示的信息
     */
    private void getShowMusic(final String keyword) {

        String u = null;
        try {
            u = new String(keyword.getBytes(), "utf-8");
            u = URLEncoder.encode(u, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url="http://mobilecdn.kugou.com/api/v3/search/song?iscorrect=1&showtype=14&tag=1&version=8415&keyword="+u+"&highlight=em&plat=0&sver=5&correct=1&page=1&pagesize=20&with_res_tag=1";

        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                parseShowJSON(response);
                Message m=new Message();
                m.what=2;
                handler.sendMessage(m);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private void parseShowJSON(String response) {
        try {
            response=response.replace("<!--KG_TAG_RES_START-->","");
            response=response.replace("<!--KG_TAG_RES_END-->","");
            //Log.i("info", response);
            //将服务器返回的数据传入到一个JSONArray对象中
            JSONObject start1 = new JSONObject(response);
            JSONObject start2=start1.getJSONObject("data");
            JSONArray jsonArray=start2.getJSONArray("info");
            //遍历这个数组
            for(int i=0;i<jsonArray.length();i++){
                //从JsonArray中取出JSONObject对象
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                //获取JSONObject对象中的数据
                String songname=jsonObject.getString("songname");
                String singername=jsonObject.getString("singername");
                String  duration=jsonObject.getString("duration");
                String hash=jsonObject.getString("hash");
                songname=songname.replace("<em>","");
                songname=songname.replace("</em>","");
                ShowItem showItem=new ShowItem();
                showItem.setSongname(songname);
                showItem.setSingername(singername);
                showItem.setDuration(duration);
                showItem.setHash(hash);
                showMusicList.add(showItem);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * @param hash
     * 当用户点击歌曲的时候进行播放
     * 也就是获取歌曲的链接
     */
    private void getplayMusic(final String hash) {

        String url="http://m.kugou.com/app/i/getSongInfo.php?hash=" + hash + "&cmd=playInfo";
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                parsePlayJSON(response);
                Message m = new Message();
                m.what = 3;
                handler.sendMessage(m);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    /**
     * @param hash
     * 当用户点击下载的时候进行下载
     * 也就是获取歌曲的链接
     */
    private void getUrlMusic(final String hash) {

        String url="http://m.kugou.com/app/i/getSongInfo.php?hash=" + hash + "&cmd=playInfo";
        HttpUtil.sendHttpRequest(url, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                parsePlayJSON(response);
                Message m = new Message();
                m.what = 4;
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
            imageUrl=object.getString("imgUrl");
            imageUrl=imageUrl.replace("{size}/","");
            //Log.i("info", musicUrl+"   "+ImageUrl);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fastDownload.stopDownloadService();
    }
}
