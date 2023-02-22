package com.wsp.animatordemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;

import java.util.ArrayList;
import java.util.List;

public class MyHScrollView extends HorizontalScrollView  {
    public int mTouchSlop;

    private int iDownX;
    private float mStartX;
    private float mStartY;
    private boolean canScroll = true;
    private boolean verticalScrolling;

    public MyHScrollView(Context context) {
        super(context);
    }

    public MyHScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public MyHScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCanScroll(boolean canScroll) {
        this.canScroll = canScroll;
    }

    public void setVerticalScrolling(boolean b) {
        verticalScrolling = b;
    }

    public interface OnScrollToTopListener {
        void atTop(boolean atTop);
    }

    public OnScrollToTopListener onScrollToTopListener;

    private List<OnScrollToTopListener> listeners = new ArrayList<>();

    public void addOnScrollToTopListener(OnScrollToTopListener onScrollToTopListener) {
        listeners.add(onScrollToTopListener);
    }

    public void setOnScrollToTopListener(OnScrollToTopListener onScrollToTopListener) {
        this.onScrollToTopListener = onScrollToTopListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        Log.i("onInterceptTouchEvent","getL "+l);
        for(OnScrollToTopListener listener:listeners){
            listener.atTop(l==0);
        }
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int iAction=ev.getAction();
        switch(iAction){
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                mStartY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = Math.abs(endX - mStartX);
                float distanceY = Math.abs(endY - mStartY);
                if(( distanceX > distanceY&&!canScroll)||verticalScrolling){

                    return false;
                }
                mStartY = endY;
                mStartX = endX;
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

}
