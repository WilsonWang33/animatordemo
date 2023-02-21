package com.wsp.animatordemo.behavior;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.wsp.animatordemo.R;
import com.wsp.animatordemo.utils.DensityUtil;

public class HeaderViewBehavior2 extends CoordinatorLayout.Behavior {

    private int headerViewHeight;

    private int headerViewScaleHeight;
    private int headerViewWidth;
    private int titleHeight;

    private float oldTranslationY;

    private int mTouchSlop;

    public HeaderViewBehavior2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        parent.onLayoutChild(child,layoutDirection);
        mTouchSlop = ViewConfiguration.get(child.getContext()).getScaledTouchSlop() / 2;
        titleHeight = parent.findViewById(R.id.title).getMeasuredHeight();
        headerViewHeight = child.getMeasuredHeight();
        headerViewWidth = child.getMeasuredWidth();
        headerViewScaleHeight = headerViewHeight + 2*titleHeight;
        Log.i("headerView-","onLayoutChild" + "headerViewHeight:"+headerViewHeight);
        // 设置 top 从而排在 HeaderView的下面
        ViewCompat.offsetTopAndBottom(child, titleHeight);
        return true;

    }

    @Override
    public boolean layoutDependsOn(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        return dependency.getId()== R.id.recycler;
    }

    float oldScaleY = 1.0f;
    float oldScaleX = 1.0f;

    // 缩放
    final AnimatorSet animatorSet = new AnimatorSet();

    /**
        *         Log.i("headerView-","onDependentViewChanged" + "headerViewHeight:"+measuredHeight);
        *         Log.i("headerView-","onDependentViewChanged" + "translationY:"+translationY);
        *         Log.i("headerView-","onDependentViewChanged" + "top:"+top);
     */
    @Override
    public boolean onDependentViewChanged(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull View dependency) {
        float translationY = dependency.getTranslationY();
        int top = dependency.getTop();
        int measuredHeight = child.getMeasuredHeight();
        Log.i("headerView-","onDependentViewChanged" + " headerViewHeight:"+headerViewHeight);
        if(translationY<0){
            child.setTranslationY(translationY);
        }
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        return ((axes+1) & ViewCompat.SCROLL_AXIS_VERTICAL)!=0;

    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        //处理向上滑动
        float newTransY = child.getTranslationY()-dy;
        Log.i("HeaderView-","onNestedPreScroll:"+newTransY+"  dy:"+dy);
        if(dy>0){
            if (newTransY >= -headerViewHeight) {
                // 完全消耗滑动距离后没有完全贴顶或刚好贴顶
                // 那么就声明消耗所有滑动距离，并上移 RecyclerView
                consumed[1] = dy; // consumed[0/1] 分别用于声明消耗了x/y方向多少滑动距离
//                child.setTranslationY(newTransY);
                Log.i("HeaderView-","onNestedPreScroll:"+child.getClass().getName());
            } else {
                // 如果完全消耗那么会导致 RecyclerView 超出可视区域
                // 那么只消耗恰好让 RecyclerView 贴顶的距离
                consumed[1] = (int) (headerViewHeight + child.getTranslationY());
//                child.setTranslationY(-headerViewHeight);
            }
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        if(dyUnconsumed<0){
            float newTransY = child.getTranslationY() - dyUnconsumed;
            if (newTransY<=titleHeight) {
//                child.setScaleY(newTransY);
//                child.setTranslationY(newTransY);
            } else {
//                child.setTranslationY(titleHeight);
            }
        }
    }

    private float mStartX;
    private float mStartY;


    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                mStartY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = Math.abs(endX - mStartX);
                float distanceY = Math.abs(endY - mStartY);
                if(distanceX > mTouchSlop && distanceY > distanceX){
                    return onTouchEvent(parent,child,ev);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return true;
    }
}
