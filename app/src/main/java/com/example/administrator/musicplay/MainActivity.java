package com.example.administrator.musicplay;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.bumptech.glide.Glide;
import com.example.administrator.musicplay.adapter.MusicAdapter;
import com.example.administrator.musicplay.adapter.MusicItem;
import com.example.administrator.musicplay.fragment.CollectionFragment;
import com.example.administrator.musicplay.fragment.DownloadFragment;
import com.example.administrator.musicplay.fragment.LocalFragment;
import com.example.administrator.musicplay.fragment.MyFragmentAdapter;
import com.example.administrator.musicplay.internet.HttpUtil;
import com.example.administrator.musicplay.scanMusic.ScannerActivity;
import com.example.administrator.musicplay.scanMusic.ScannerMusic;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements OnClickListener{

    /**
     * 退出和扫描按钮
     */
    private Button exit,scan;

    /**
     * SlidingMenu菜单
     */
    private SlidingMenu menu;

    /**
     * 真正播放音乐的名字，定义成静态以便在服务中调用
     */
    public static TextView playname;

    /**
     * 播放音乐的开始时间和结束时间，定义成静态以便在服务中调用
     */
    public static TextView allTime,currentTime;

    /**
     * 音乐播放，暂停，下一首的按钮
     */
    private ImageButton play, stop,next;

    /**
     *标题栏上用于查找和显示菜单的按钮
     */
    private ImageButton searchButton,menuButton;

    /**
     * 整个程序的背景
     */
    private ImageView backGround;


    /**
     * 与MusicService绑定后获取的MusicService中的实例
     */
    private MusicService.PlayMusic playMusic;

    /**
     * 开启服务的intent
     */
    private Intent service;

    /**
     * 主布局中的viewPage
     */
    private ViewPager viewPager;

    /**
     * 存放viewPage的fragment的集合
     */
    public List<Fragment> fragmentList;

    /**
     * viewPage的标题
     */
    private PagerSlidingTabStrip tabs;

    /**
     * 屏幕的像素点
     */
    private DisplayMetrics dm;

    /**
     * 存放扫描出来的音乐信息的数组
     */
    private List<MusicItem> musicitem;

    /**
     *播放音乐的进度条，定义成静态以便在服务中使用
     */
    public static SeekBar playSeek;


    /**
     * @param requestCode  启动活动时传入的请求码
     * @param resultCode   返回数据时传入的处理结果
     * @param data  返回的数据
     * 在扫描完成之后调用LocalFragment的方法更新listview
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK) {
                    Boolean isScanner=data.getBooleanExtra("isScanner",false);
                    Log.i("info", "返回bool数据");
                    if(isScanner){
                        Log.i("info","返回数据加载" );
                        LocalFragment localFragment=(LocalFragment)fragmentList.get(0);
                        localFragment.loadScanMusic();
                    }
                }
                break;
            default:
        }
    }

    /**
     * 与MusicService建立连接
     */
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub

            //把服务里的binder实例取出来
            playMusic = (MusicService.PlayMusic) service;
        }
    };

    public static void sendSeekBar(final GetSeekBar seekBar){
        seekBar.getSeekBar(playSeek);
        seekBar.getAllTime(allTime);
        seekBar.getCurrentTime(currentTime);
        seekBar.getMusicName(playname);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info","maincreate" );
        setContentView(R.layout.activity_main);

        contentInit();
        slidingMenuInit();
        //开启服务，并绑定
        service = new Intent(MainActivity.this, MusicService.class);
        service.putExtra("path","");
        startService(service);
        bindService(service, conn, BIND_AUTO_CREATE);
        viewPageInit();
        //初始化背景的控件
        backGround = (ImageView) findViewById(R.id.backGround);
        //通过Url加载图片到控件中
        getBackGround("http://guolin.tech/api/bing_pic");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("info","main+onrresume" );
        jumpDownloadPage();
    }

    private void jumpDownloadPage() {
        Intent intent=getIntent();
        boolean isJump=intent.getBooleanExtra("isJump",false);
        Log.i("info","nojump" );
        if(isJump){
            Log.i("info","jump" );
            viewPager.setCurrentItem(2);
        }
    }

    /**
     * 设置ViewPage
     */
    private void viewPageInit() {
        viewPager = (ViewPager) findViewById(R.id.viewPage);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabStrip);
        dm = getResources().getDisplayMetrics();

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==1 || position==2){
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
                }else if(position==0){
                    menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //初始化fragmentList集合，并添加数据
        fragmentList = new ArrayList<>();
        fragmentList.add(new LocalFragment());
        fragmentList.add(new CollectionFragment());
        fragmentList.add(new DownloadFragment());

        //初始化适配器，并加载适配器和标题栏
        MyFragmentAdapter myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(), fragmentList);
        viewPager.setAdapter(myFragmentAdapter);
        //设置viewPage预加载fragment的数量
        viewPager.setOffscreenPageLimit(2);

        //手动跳转fragment
        //viewPager.setCurrentItem(2);

        tabs.setViewPager(viewPager);

        // 设置Tab的分割线是透明的
        tabs.setDividerColor(Color.TRANSPARENT);
        // 设置Tab底部线的高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 0, dm));
        // 设置Tab Indicator的高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 1, dm));
        // 设置Tab标题文字的大小
        tabs.setTextSize((int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // 设置Tab Indicator的颜色
        tabs.setIndicatorColor(Color.parseColor("#45c01a"));

        // 取消点击Tab时的背景色
        tabs.setTabBackground(0);
    }

    /**
     * 设置SlidingMenu
     */
    private void slidingMenuInit() {
        menu=new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        // 设置触摸屏幕的模式
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        //menu.setTouchModeBehind(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置滑动菜单视图的宽度
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        menu.setFadeDegree(0.35f);
        menu.setBehindScrollScale(0.3f);
        //menu.setSecondaryShadowDrawable(R.drawable.second_sliding);
        menu.setShadowDrawable(R.drawable.second_sliding);
        menu.attachToActivity(this,SlidingMenu.SLIDING_CONTENT);
        //menu.addIgnoredView(viewPager);
        menu.setMenu(R.layout.sliding_menu);
        scan= (Button) findViewById(R.id.scan);
        exit= (Button) findViewById(R.id.exit);
        scan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
                startActivityForResult(intent,1);
                menu.toggle();
            }
        });
        exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    /**
     * 初始化一些控件
     */
    private void contentInit() {
        playname = (TextView) findViewById(R.id.playname);
        allTime = (TextView) findViewById(R.id.alltime);
        currentTime = (TextView) findViewById(R.id.currenttime);
        searchButton = (ImageButton) findViewById(R.id.searchbutton);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
        menuButton = (ImageButton) findViewById(R.id.menubutton);
        menuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.toggle();
            }
        });
        play = (ImageButton) findViewById(R.id.play);
        stop = (ImageButton) findViewById(R.id.pause);
        next = (ImageButton) findViewById(R.id.next);
        playSeek = (SeekBar) findViewById(R.id.progressBar);
        play.setOnClickListener(this);
        stop.setOnClickListener(this);
        next.setOnClickListener(this);
        musicitem=new ArrayList<>();
    }


    /**
     * @param serviceName 判断是否存在的服务名
     * @return 返回true表示服务存在，反之不存在
     */
    private boolean isServiceStart(String serviceName){
        ActivityManager manager= (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for( ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceName.equals(serviceInfo.service.getClassName())) {
                return  true;
            }
        }
        return false;
    }


    /**
     * @param v
     * 控制播放的点击事件的监听
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.play:
                playMusic.play();
                break;
            case R.id.pause:
                playMusic.pause();
                break;
            case R.id.next:
                playMusic.next();
                break;
        }
    }

    /**
     * @param keyword 图片的地址链接
     * 通过url获取图片，并通过runOnUiThread和Glide（库）在主线程中加载图片
     */
    private void getBackGround(final String keyword) {

        HttpUtil.sendOkHttpRequest(keyword, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String url=response.body().string();

                //在主线程中加载图片
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println(Thread.currentThread());
                        backGround.setAlpha(0.5f);
                        //用Glide库加载图片到一个Imageview
                        Glide.with(MainActivity.this).load(url).into(backGround);
                    }
                });
            }
        });
    }

    /**
     * @param keyCode
     * @param event
     * @return
     * 拦截点击返回按钮事件，点击返回按钮返回主界面而不销毁程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //解绑和停止MusicService服务
        unbindService(conn);
        stopService(service);
    }

}
