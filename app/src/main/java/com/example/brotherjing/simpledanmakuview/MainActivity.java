package com.example.brotherjing.simpledanmakuview;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.brotherjing.simpledanmakuview.*;


public class MainActivity extends Activity{

    private final int HANDLER_SEND_MESSAGE = 1;
    private final int STRING_CNT = 3;

    private String[] strings = {"23333333333333333333333","wwwwwwwwwwwwwwwwwwwwwwwww","666666666666666666666666"};

    private Thread thread;
    private LinearLayout ll;
    private SeekBar sb1,sb2,sb3;
    private Button pause;
    private DanmakuView danmakuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ll = (LinearLayout)findViewById(R.id.ll);
        sb1 = (SeekBar)findViewById(R.id.sbHeight);
        sb2 = (SeekBar)findViewById(R.id.sbSpeed);
        sb3 = (SeekBar)findViewById(R.id.sbTextSize);
        pause = (Button)findViewById(R.id.btn_pause);
        danmakuView = (DanmakuView)findViewById(R.id.danmaku_view);
        danmakuView.setMode(DanmakuView.MODE_NO_OVERDRAW | DanmakuView.MODE_USE_DANMAKU_BUFFER);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                while(true){
                    try {
                        Thread.sleep(150);
                        handler.sendEmptyMessage(HANDLER_SEND_MESSAGE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        thread.start();
        sb1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ViewGroup.LayoutParams params = ll.getLayoutParams();
                params.height = progress + 100;
                ll.setLayoutParams(params);
                ll.requestLayout();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                danmakuView.setMSPF(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sb3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                danmakuView.setTextSize((int) (18 + (progress - 50) / 10));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (danmakuView.isRunning()) danmakuView.pause();
                else danmakuView.resume();
            }
        });

        danmakuView.setOnDanmakuClickListener(new DanmakuView.OnDanmakuClickListener() {
            @Override
            public void onDanmakuClick(Danmaku danmaku) {
                Toast.makeText(MainActivity.this,danmaku.getId()+"",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case HANDLER_SEND_MESSAGE:
                    Danmaku danmaku = new Danmaku(strings[(int)(Math.random()*3)],(int)System.currentTimeMillis());
                    danmaku.setSpeed(Danmaku.DanmakuSpeed.FAST);
                    danmakuView.addDanmaku(danmaku);
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
