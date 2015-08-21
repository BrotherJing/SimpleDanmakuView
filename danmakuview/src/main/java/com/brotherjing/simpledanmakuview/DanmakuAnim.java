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
    private float width;
    private int progress;

    private boolean isFinished;
    //private OnAnimFinishListener listener;

    public DanmakuAnim(Danmaku d,float start,float end, float width){
        this.danmaku = d;
        this.start = start;
        this.end = end;
        this.x = start;
        this.width = width;
        progress = 0;
        isFinished = false;
    }

    public boolean update(){
        if(++progress==FRAME_COUNT){
            isFinished = true;
            return false;
        }
        x = start+(end-start)*1f*progress/FRAME_COUNT;
        return true;
    }

    public void setHeight(int height){
        y = height;
    }

    public String getText(){return danmaku.getText();}

    public float getX(){return x;}

    public float getY(){return y;}

    public boolean isFinished(){return isFinished;}

    public float getWidth(){
        return width;
    }

    public Danmaku getDanmaku() {
        return danmaku;
    }
}
