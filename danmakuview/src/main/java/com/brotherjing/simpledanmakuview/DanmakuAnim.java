package com.brotherjing.simpledanmakuview;

/**
 * Created by Administrator on 2014/12/7 0007.
 */
public class DanmakuAnim {

    final int FRAME_COUNT = 400;

    private Danmaku danmaku;
    private float start;
    private float end;
    private float x;
    private float y;
    private int progress;

    private boolean isFinished;
    //private OnAnimFinishListener listener;

    public DanmakuAnim(Danmaku d,float start,float end){
        this.danmaku = d;
        this.start = start;
        this.end = end;
        this.x = start;
        progress = 0;
        isFinished = false;
    }

    public void update(){
        progress++;
        if(progress==FRAME_COUNT){
            isFinished = true;
            return;
        }
        x = start+(end-start)*1f*progress/FRAME_COUNT;
    }

    public void setHeight(int height){
        y = height;
    }

    public String getText(){return danmaku.getText();}
    public float getX(){return x;}
    public float getY(){return y;}

    public boolean isFinished(){return isFinished;}
}
