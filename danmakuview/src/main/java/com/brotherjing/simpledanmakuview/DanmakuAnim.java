package com.brotherjing.simpledanmakuview;

import com.brotherjing.simpledanmakuview.Danmaku.DanmakuSpeed;

/**
 * Created by Administrator on 2014/12/7 0007.
 */
public class DanmakuAnim {

    final int FRAME_COUNT_NORMAL = 400;
    final int FRAME_COUNT_FAST = 200;
    final int FRAME_COUNT_SLOW = 800;

    private Danmaku danmaku;
    private float start;
    private float end;
    private float x;
    private float y;
    private float width;
    private int progress;
    private float step;
    private int frameCount;

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
        switch (d.speed){
            case NORMAL:
                frameCount = FRAME_COUNT_NORMAL;
                break;
            case FAST:
                frameCount = FRAME_COUNT_FAST;
                break;
            case SLOW:
                frameCount = FRAME_COUNT_SLOW;
                break;
        }
        step = (end-start)*1f/ frameCount;
    }

    public boolean update(){
        if(++progress== frameCount){
            isFinished = true;
            return false;
        }
        //x = start+(end-start)*1f*progress/frameCount;
        x += step;
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
