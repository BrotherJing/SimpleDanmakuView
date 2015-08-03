package com.brotherjing.simpledanmakuview;

import android.animation.IntEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.*;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Administrator on 2014/11/29 0029.
 */
public class DanmakuView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    final String TAG = "DanmakuView";

    final int MSPF = 25;
    final int DANMAKU_TYPE_COUNT = 3;
    final int DEFAULT_TEXT_SIZE = 18;

    //int slots[][];//
    DanmakuAnim danmakuSlots[][];
    int alt_slot;//when all slots are full, choose an alternative one

    int screenWidth;
    int screenHeight;

    int textCount;//current visible danmaku count
    int defaultHeight;
    int MAX_SLOT_PORT;
    int MAX_SLOT_LAND;
    int MAX_SLOT;

    LinkedList<DanmakuAnim> animList;

    Context context;
    SurfaceHolder mHolder;
    Canvas mCanvas;

    TextPaint textPaint;
    Paint clearPaint;

    public DanmakuView(Context context) {
        super(context);
        this.context = context;
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    public DanmakuView(Context context, AttributeSet attrs){
        super(context,attrs);
        this.context = context;
        mHolder = getHolder();
        mHolder.addCallback(this);

        setZOrderOnTop(true);
        mHolder.setFormat(PixelFormat.TRANSLUCENT);

        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    public void init(int width,int height){
        screenHeight = height;
        screenWidth = width;
        int orientation = context.getResources().getConfiguration().orientation;
        defaultHeight = sp2px(DEFAULT_TEXT_SIZE);
        if(width>height){
            int tmp = width;
            width = height;
            height = tmp;
        }
        MAX_SLOT_PORT = (height-dp2px(120))/defaultHeight;
        MAX_SLOT_LAND = (width-dp2px(100))/defaultHeight;

        if(orientation==Configuration.ORIENTATION_PORTRAIT)MAX_SLOT = MAX_SLOT_PORT;
        else MAX_SLOT = MAX_SLOT_LAND;

        textCount = 0;
        alt_slot = 0;
        danmakuSlots = new DanmakuAnim[DANMAKU_TYPE_COUNT][MAX_SLOT_PORT];
        for(int i=0;i<MAX_SLOT_PORT;++i){
            danmakuSlots[0][i] = danmakuSlots[1][i] = danmakuSlots[2][i] = null;//刚开始轨道都空闲
        }

        //initialize paint
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(sp2px(DEFAULT_TEXT_SIZE));

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        animList = new LinkedList<DanmakuAnim>();
    }

    public void changeOrientation(int ori){
        if(ori==Configuration.ORIENTATION_PORTRAIT)MAX_SLOT = MAX_SLOT_PORT;
        else MAX_SLOT = MAX_SLOT_LAND;
    }

    public void addDanmaku(Danmaku d){

        //measure the width of text
        float width = textPaint.measureText(d.getText());

        DanmakuAnim anim = new DanmakuAnim(d,screenWidth,0-width);

        //select an available slot for this danmaku
        int slot = getNextAvailableSlot(d,anim);

        anim.setHeight(slot*defaultHeight);

        animList.add(anim);

        ++textCount;
    }

    public void pause(){
        isRunning = false;
        handler.removeCallbacks(this);
    }

    public void resume(){
        isRunning = true;
        handler.post(this);
    }

    private int getNextAvailableSlot(Danmaku d,DanmakuAnim anim){
        DanmakuAnim old_anim;
        int shifting = Danmaku.DanmakuType.SHIFTING.ordinal();
        int type = d.getType().ordinal();
        for(int i=0;i<MAX_SLOT;++i){
            if(danmakuSlots[type][i]!=null){
                if(type!=shifting)continue;
                old_anim = danmakuSlots[type][i];
                if(old_anim==null||old_anim.getX()<200){
                    danmakuSlots[type][i] = anim;
                    return i;
                }
            }
            else{
                danmakuSlots[type][i] = anim;
                return i;
            }
        }
        //choose a random slot
        alt_slot = (++alt_slot)%MAX_SLOT;
        danmakuSlots[type][alt_slot] = anim;
        return alt_slot;
    }

    Handler handler = new Handler();
    boolean isRunning;

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        handler.postDelayed(this,MSPF);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
        handler.removeCallbacks(this);
    }

    @Override
    public void run() {
        if(isRunning) {
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis();
            try {
                if (end - start < MSPF)
                    handler.postDelayed(this, MSPF - (end - start));
                else
                    handler.post(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            handler.removeCallbacks(this);
        }
    }

    void draw(){
        mCanvas = mHolder.lockCanvas();
        if(mCanvas!=null){
            mCanvas.drawPaint(clearPaint);

            Iterator<DanmakuAnim> iterator = animList.iterator();
            while(iterator.hasNext()){
                DanmakuAnim anim = iterator.next();
                if(anim.isFinished()){
                    iterator.remove();
                }else{
                    drawText(anim);
                }
            }
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    void drawText(DanmakuAnim anim){
        anim.update();
        mCanvas.drawText(anim.getText(),anim.getX(),anim.getY()+defaultHeight,textPaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init(w,h);
    }

    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private int sp2px(int sp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }
}
