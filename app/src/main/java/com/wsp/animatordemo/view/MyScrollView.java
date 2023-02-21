package com.wsp.animatordemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyScrollView extends ScrollView {
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float mStartY;
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int actionMasked = ev.getActionMasked();
        switch (actionMasked){
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(getScrollY()>0&&getScrollY()<computeVerticalScrollRange());
                mStartY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endY = ev.getY();
                float dY = endY - mStartY;
                Log.i("MyScrollView", "  dY:"+dY);
                Log.i("MyScrollView", "  getScrollY:"+getScrollY());
                //scrollView在底部以及手指往上滑的时候   和   scrollView在顶部的时候以及手指往下滑
                getParent().requestDisallowInterceptTouchEvent((dY<0&&getScrollY()-dY>=computeVerticalScrollRange())||(dY>0&&getScrollY()+dY<=0));
                mStartY = endY;
                break;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
