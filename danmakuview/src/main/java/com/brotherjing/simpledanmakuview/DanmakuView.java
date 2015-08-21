package com.brotherjing.simpledanmakuview;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.*;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Administrator on 2014/11/29 0029.
 */
public class DanmakuView extends SurfaceView implements SurfaceHolder.Callback,Runnable{

    final String TAG = "DanmakuView";

    public static final int MODE_NO_OVERDRAW = 0x01;
    public static final int MODE_USE_DANMAKU_BUFFER = 0x02;

    final int MSPF_DEFAULT = 20;
    final int DANMAKU_TYPE_COUNT = 3;
    final int DEFAULT_TEXT_SIZE = 18;
    final int MAX_BUFFER_SIZE = 100;

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
    int MSPF;

    int mode_flag;//overdraw? use buffer?
    boolean first_measure;

    LinkedList<DanmakuAnim> animList;
    LinkedList<DanmakuAnim> animBuffer;

    Context context;
    SurfaceHolder mHolder;
    Canvas mCanvas;

    TextPaint textPaint;
    Paint clearPaint;

    public DanmakuView(Context context) {
        this(context,null);
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

        first_measure = true;
        //initSize(context.getResources().getDisplayMetrics().widthPixels, context.getResources().getDisplayMetrics().heightPixels);
        initData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.i(TAG,"measure "+getMeasuredHeight()+" "+getMeasuredWidth());
        initSize(getMeasuredWidth(), getMeasuredHeight());
    }

    private void initSize(int width, int height){
        screenHeight = height;
        screenWidth = width;
        defaultHeight = sp2px(DEFAULT_TEXT_SIZE);

        if(first_measure) {
            first_measure = false;
            MAX_SLOT = (screenHeight-dp2px(50))/defaultHeight;
            danmakuSlots = new DanmakuAnim[DANMAKU_TYPE_COUNT][MAX_SLOT];
            for (int i = 0; i < MAX_SLOT; ++i) {
                danmakuSlots[0][i] = danmakuSlots[1][i] = danmakuSlots[2][i] = null;//刚开始轨道都空闲
            }
        }else{
            int MAX_SLOT_NEW = (screenHeight-dp2px(50))/defaultHeight;
            int min = Math.min(MAX_SLOT_NEW,MAX_SLOT);
            DanmakuAnim[][] danmakuSlotsNew = new DanmakuAnim[DANMAKU_TYPE_COUNT][MAX_SLOT_NEW];
            for (int i = 0; i < min; ++i) {
                danmakuSlotsNew[0][i]=danmakuSlots[0][i];
                danmakuSlotsNew[1][i]=danmakuSlots[1][i];
                danmakuSlotsNew[2][i]=danmakuSlots[2][i];
            }
            for(int i=min;i<MAX_SLOT_NEW;++i){
                danmakuSlotsNew[0][i] = danmakuSlotsNew[1][i] = danmakuSlotsNew[2][i] = null;
            }
            MAX_SLOT = MAX_SLOT_NEW;
            danmakuSlots = danmakuSlotsNew;
        }
    }

    private void initData(){
        MSPF = MSPF_DEFAULT;
        mode_flag = 0;
        textCount = 0;
        alt_slot = 0;

        //initialize paint
        textPaint = new TextPaint();
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(sp2px(DEFAULT_TEXT_SIZE));

        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        animList = new LinkedList<DanmakuAnim>();
        animBuffer = new LinkedList<DanmakuAnim>();
    }

    public void changeOrientation(int ori){
        if(ori==Configuration.ORIENTATION_PORTRAIT)MAX_SLOT = MAX_SLOT_PORT;
        else MAX_SLOT = MAX_SLOT_LAND;
    }

    public boolean addDanmaku(Danmaku d){

        //measure the width of text
        float width = textPaint.measureText(d.getText());

        DanmakuAnim anim = new DanmakuAnim(d,screenWidth,-width,width);

        return addDanmakuAnim(anim,false);
    }

    private boolean addDanmakuAnim(DanmakuAnim anim, boolean fromBuffer){
        int slot = getNextAvailableSlot(anim);

        //if no slot is available
        if(slot<0){
            if((mode_flag&MODE_USE_DANMAKU_BUFFER)==0||fromBuffer)return false;
            if(isBufferFull())return false;
            animBuffer.add(anim);
            return true;
        }
        anim.setHeight(slot * defaultHeight);
        animList.add(anim);
        ++textCount;
        return true;
    }

    public void pause(){
        isRunning = false;
        handler.removeCallbacks(this);
    }

    public void resume(){
        isRunning = true;
        handler.post(this);
    }

    private int getNextAvailableSlot(DanmakuAnim anim){
        Danmaku d = anim.getDanmaku();
        DanmakuAnim old_anim;
        int shifting = Danmaku.DanmakuType.SHIFTING.ordinal();
        int type = d.getType().ordinal();
        for(int i=0;i<MAX_SLOT;++i){
            if(danmakuSlots[type][i]!=null){
                if(type!=shifting)continue;
                old_anim = danmakuSlots[type][i];
                if(old_anim.getX()<screenWidth-old_anim.getWidth()-100){
                    danmakuSlots[type][i] = anim;
                    return i;
                }
            }
            else{
                danmakuSlots[type][i] = anim;
                return i;
            }
        }
        //in this mode, overdraw is not allowed.
        if((mode_flag&MODE_NO_OVERDRAW)!=0)return -1;
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
        if(!isRunning) handler.removeCallbacks(this);
        if((mode_flag&MODE_USE_DANMAKU_BUFFER)!=0&&!animBuffer.isEmpty()){
            DanmakuAnim anim = animBuffer.getFirst();
            if(addDanmakuAnim(anim,true)){
                animBuffer.remove(anim);
            }
        }
        try{
            handler.postDelayed(this,MSPF);
        }catch (Exception e){
            e.printStackTrace();
        }
        draw();
    }

    void draw(){
        mCanvas = mHolder.lockCanvas();
        if(mCanvas!=null){
            mCanvas.drawPaint(clearPaint);

            Iterator<DanmakuAnim> iterator = animList.iterator();
            while(iterator.hasNext()){
                DanmakuAnim anim = iterator.next();
                /*if(anim.isFinished()){
                    iterator.remove();
                }else{
                    drawText(anim);
                }*/
                if(!anim.update()){
                    iterator.remove();
                }else{
                    mCanvas.drawText(anim.getText(),anim.getX(),anim.getY()+defaultHeight,textPaint);
                }
            }
            mHolder.unlockCanvasAndPost(mCanvas);
        }
    }

    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    private int sp2px(int sp){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp,context.getResources().getDisplayMetrics());
    }

    public void setMode(int mode){
        this.mode_flag = mode;
    }

    private boolean isBufferFull(){
        return animBuffer.size()>=MAX_BUFFER_SIZE;
    }

    public void setMSPF(int mspf){
        if(mspf<0)return;
        this.MSPF = mspf;
    }
}
