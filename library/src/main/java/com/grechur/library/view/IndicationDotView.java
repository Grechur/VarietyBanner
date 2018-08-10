package com.grechur.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zz on 2018/8/10.
 */

public class IndicationDotView extends View{
    //图片
    private Drawable mDotDrawable;
    public IndicationDotView(Context context) {
        this(context,null);
    }

    public IndicationDotView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IndicationDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mDotDrawable!=null){
            mDotDrawable.setBounds(0,0,getMeasuredWidth(),getMeasuredHeight());
            mDotDrawable.draw(canvas);
        }
    }

    public void setDrawable(Drawable drawable) {
        this.mDotDrawable = drawable;
        invalidate();
    }
}
