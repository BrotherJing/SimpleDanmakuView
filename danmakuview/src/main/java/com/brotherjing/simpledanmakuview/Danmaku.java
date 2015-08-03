package com.brotherjing.simpledanmakuview;

import android.graphics.Color;


/**
 * Created by Administrator on 2014/11/29 0029.
 */
public class Danmaku {

    final int WHITE = Color.WHITE;
    final int RED = Color.RED;
    final int GREEN = Color.GREEN;

    public enum DanmakuType{
        SHIFTING,
        FLOATING_TOP,
        FLOATING_BOTTOM
    }

    String text;
    int color;
    int velocity;
    boolean sentByUser;
    DanmakuType type;

    public Danmaku(String txt){
        this.text = txt;
        velocity = 10000;
        color = WHITE;
        type = DanmakuType.SHIFTING;
    }

    public Danmaku(String txt,boolean sentByUser){
        this(txt);
        this.sentByUser = sentByUser;
    }

    public Danmaku(String txt,int color,boolean sentByUser){
        this(txt,sentByUser);
        this.color = color;
    }

    public Danmaku(String txt,int color,boolean sentByUser,DanmakuType type){
        this(txt,color,sentByUser);
        this.type = type;
    }

    public void setText(String txt){this.text = txt;}
    public String getText(){return text;}

    public void setVelocity(int v){this.velocity = v;}
    public int getVelocity(){return velocity;}

    public void setColor(int color){this.color = color;}
    public int getColor(){return color;}

    public boolean isSentByUser(){return sentByUser;}
    public DanmakuType getType(){return type;}
}
