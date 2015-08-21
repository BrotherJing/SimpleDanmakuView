package com.brotherjing.simpledanmakuview;

import android.graphics.Color;


/**
 * Created by Administrator on 2014/11/29 0029.
 */
public class Danmaku {

    final static int WHITE = Color.WHITE;
    final static int RED = Color.RED;
    final static int GREEN = Color.GREEN;

    public enum DanmakuType{
        SHIFTING,
        FLOATING_TOP,
        FLOATING_BOTTOM
    }

    public static enum DanmakuSpeed{
        NORMAL,
        FAST,
        SLOW
    }

    String text;
    int color;
    DanmakuSpeed speed;
    boolean sentByUser;
    DanmakuType type;

    public Danmaku(String txt){
        this(txt,Danmaku.WHITE,false,DanmakuType.SHIFTING,DanmakuSpeed.NORMAL);
    }

    public Danmaku(String txt,boolean sentByUser){
        this(txt,Danmaku.WHITE,sentByUser,DanmakuType.SHIFTING,DanmakuSpeed.NORMAL);
    }

    public Danmaku(String txt,int color,boolean sentByUser){
        this(txt,color,sentByUser,DanmakuType.SHIFTING,DanmakuSpeed.NORMAL);
    }

    public Danmaku(String txt,int color,boolean sentByUser,DanmakuType type){
        this(txt,color,sentByUser,type,DanmakuSpeed.NORMAL);
    }

    public Danmaku(String txt,int color,boolean sentByUser,DanmakuType type,DanmakuSpeed speed){
        this.text = txt;
        this.color = color;
        this.sentByUser = sentByUser;
        this.type = type;
        this.speed = speed;
    }

    public void setText(String txt){this.text = txt;}
    public String getText(){return text;}

    public void setColor(int color){this.color = color;}
    public int getColor(){return color;}

    public boolean isSentByUser(){return sentByUser;}
    public DanmakuType getType(){return type;}
}
