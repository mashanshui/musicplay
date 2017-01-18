package com.example.administrator.musicplay.scanMusic;

import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.administrator.musicplay.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ScannerActivity extends AppCompatActivity {

    /**
     * 扫描音乐的对象
     */
    private SingleMediaScanner scanner;

    /**
     * 已经扫描的音乐的数量
     */
    private TextView scannerCount;

    /**
     * 扫描结束按钮
     */
    private Button scannerOver;

    private ProgressBar scannerProgressBar;

    /**
     * 扫描出来的路径
     */
    private List<String> scannerList = new ArrayList<>();

    private  int songCount=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_activity);

        scannerOver = (Button) findViewById(R.id.ScannerOver);
        scannerCount= (TextView) findViewById(R.id.scannerCount);
        scannerProgressBar= (ProgressBar) findViewById(R.id.scannerProgressBar);
        scannerOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("isScanner", true);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        scanner = new SingleMediaScanner(this);

        String []scannerPath={Environment
                .getExternalStorageDirectory().getAbsolutePath()};
        new MyAsyncTask().execute(scannerPath);
    }


    class MyAsyncTask extends AsyncTask<String,Integer,Integer>{

        @Override
        protected Integer doInBackground(String... params) {

            for (int i=0;i<params.length;i++) {
                folderScan(params[i]);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            for(int i=0;i<scannerList.size();i++){
                scanner.scan(scannerList.get(i));
                Log.i("info",scannerList.get(i) );
            }
            scanner.stop();
            scannerOver.setVisibility(View.VISIBLE);
        }
    }


    public void folderScan(String path) {
        File file = new File(path);

        if (file.isDirectory()) {
            File[] array = file.listFiles();

            for (int i = 0; i < array.length; i++) {
                File f = array[i];

                if (f.isFile()) {//FILE TYPE
                    String name = f.getName();

                    if (name.contains(".mp3")) {
                        //Log.i("info",f.getPath() );
                        scannerList.add(f.getAbsolutePath());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scannerCount.setText("已经扫描  "+(songCount++)+"  首音乐!");
                            }
                        });
                    }
                } else {//FOLDER TYPE
                    folderScan(f.getAbsolutePath());
                }
            }
        }
    }

    class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {

        private MediaScannerConnection mMs;

        public SingleMediaScanner(Context context) {
            mMs = new MediaScannerConnection(context, this);
            mMs.connect();
        }

        @Override
        public void onMediaScannerConnected() {

        }
        public void scan(String path){
            mMs.scanFile(path, null);
        }

        public void stop(){
            mMs.disconnect();
        }
        @Override
        public void onScanCompleted(String path, Uri uri) {

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("isScanner", true);
        setResult(RESULT_OK,intent);
        Log.i("info","点击back键" );
        finish();
    }
}
