package com.brotherjing.simpledanmakuview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2014/11/29 0029.
 */
public class DanmakuTextView extends TextView {

    int type;
    int level;
    int index;

    public DanmakuTextView(Context context,int l,int i,int t) {
        super(context);
        level = l;
        index = i;
        type = t;
    }

    public DanmakuTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DanmakuTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getIndex(){return index;}
    public int getLevel(){return level;}
    public int getType(){return type;}

}
