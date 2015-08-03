package com.example.brotherjing.simpledanmakuview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.brotherjing.simpledanmakuview.*;


public class MainActivity extends Activity{

    private final int HANDLER_SEND_MESSAGE = 1;
    private final int STRING_CNT = 3;

    private String[] strings = {"2333","wwwwwwwwwwww","666666"};

    private Thread thread;
    private DanmakuView danmakuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        danmakuView = (DanmakuView)findViewById(R.id.danmaku_view);
        danmakuView.init(getResources().getDisplayMetrics().widthPixels,getResources().getDisplayMetrics().heightPixels);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        handler.sendEmptyMessage(HANDLER_SEND_MESSAGE);
                        Thread.sleep(200);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_SEND_MESSAGE:
                    danmakuView.addDanmaku(new Danmaku(strings[(int)(Math.random()*3)]));
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler = null;
    }
}
