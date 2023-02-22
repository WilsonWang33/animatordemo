package com.wsp.animatordemo.behavior;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wsp.animatordemo.utils.DensityUtil;
import com.wsp.animatordemo.R;
import com.wsp.animatordemo.view.MyHScrollView;

public class ContentViewBehavior extends CoordinatorLayout.Behavior {
    private int headerViewHeight;
    private int headerViewScaleHeight;
    private int titleHeight;
    private RecyclerView contentView;
    private OverScroller scroller;
    private boolean isFling;
    private boolean hScrollViewAtTop = true;
    MyHScrollView myHScrollView;

    private final Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            if(scroller!=null&&scroller.computeScrollOffset()){
                contentView.setTranslationY(scroller.getCurrY());
                ViewCompat.postOnAnimation(contentView,this);
            }
        }
    };

    ValueAnimator valueAnimator;
    private void setAnimator(int current, int target,int duration){
        valueAnimator = ValueAnimator.ofFloat(current, target).setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                contentView.setTranslationY((Float) animation.getAnimatedValue());
            }
        });

    }

    private void startAutoScroll(int current, int target,int duration) {
        Log.i("ContentViewBehavior-", "startAutoScroll current"+current + " target"+target );
        if (scroller == null) {
            scroller =new OverScroller(contentView.getContext());
        }
        if (scroller.isFinished()) {
            contentView.removeCallbacks(scrollRunnable);
            scroller.startScroll(0, current, 0, target - current, duration);
            ViewCompat.postOnAnimation(contentView, scrollRunnable);
        }
    }

    private void stopAutoScroll() {
        Log.i("ContentViewBehavior", "stopAutoScroll");
        if (scroller!=null&&scroller.isFinished()) {
            scroller.abortAnimation();
            contentView.removeCallbacks(scrollRunnable);
        }
    }

    public ContentViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        parent.onLayoutChild(child,layoutDirection);
        contentView = (RecyclerView) child;
        titleHeight = parent.findViewById(R.id.title).getMeasuredHeight();
        myHScrollView = parent.findViewById(R.id.banner);

        headerViewHeight = myHScrollView.getMeasuredHeight()+titleHeight;
        headerViewScaleHeight = headerViewHeight + 2*titleHeight;

        // 设置 top 从而排在 HeaderView的下面
        ViewCompat.offsetTopAndBottom(child, headerViewHeight);
        myHScrollView.addOnScrollToTopListener(atTop -> {hScrollViewAtTop = atTop;});
        return true;

    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL)!=0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        if(type == ViewCompat.TYPE_NON_TOUCH){
            isFling = true;
        }
        stopAutoScroll();
        float newTransY = child.getTranslationY()-dy;
        if(dy>0){//手指向上滑
            if (newTransY >= -headerViewHeight) {
                // 完全消耗滑动距离后RV没有完全贴顶或刚好贴顶
                // 那么就声明消耗所有滑动距离，并上移 RV
                consumed[1] = dy; // consumed[0/1] 分别用于声明消耗了x/y方向多少滑动距离
                child.setTranslationY(newTransY);
            } else {
                // 如果完全消耗那么会导致 RecyclerView 超出可视区域
                // 那么只消耗恰好让 RecyclerView 贴顶的距离
                consumed[1] = (int) (headerViewHeight + child.getTranslationY());
                child.setTranslationY(-headerViewHeight);
            }
        }
    }

    float bottomPoint;


    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        stopAutoScroll();
        Log.i("Content-", " dyConsumed "+dyConsumed);
        Log.i("Content-", " dyUnconsumed "+dyUnconsumed);
        Log.i("Content-", " getTranslationY "+child.getTranslationY());
        if (dyUnconsumed < 0) { // 只处理手指向下滑动的情况
            float newTransY = child.getTranslationY() - dyUnconsumed;
            if(hScrollViewAtTop){
                if (newTransY<=titleHeight) {
                    child.setTranslationY(newTransY);
                } else {
                    child.setTranslationY(titleHeight);
                }
            }else{
                if (newTransY<=0) {
                    child.setTranslationY(newTransY);
                } else {
                    child.setTranslationY(0);
                }
            }

        }
    }

    @Override
    public void onNestedScrollAccepted(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, axes, type);
    }

    @Override
    public boolean onNestedPreFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY) {
        /*//手指向下滑
        float newTransY = child.getTranslationY()-dy;
        Log.i("Content-","onNestedPreScroll:"+newTransY+"  dy:"+dy);
        if(dy>0){
            if (newTransY >= -headerViewHeight) {
                // 完全消耗滑动距离后没有完全贴顶或刚好贴顶
                // 那么就声明消耗所有滑动距离，并上移 RecyclerView
                consumed[1] = dy; // consumed[0/1] 分别用于声明消耗了x/y方向多少滑动距离
                child.setTranslationY(newTransY);
                Log.i("Content-","onNestedPreScroll:"+child.getClass().getName());
            } else {
                // 如果完全消耗那么会导致 RecyclerView 超出可视区域
                // 那么只消耗恰好让 RecyclerView 贴顶的距离
                consumed[1] = (int) (headerViewHeight + child.getTranslationY());
                child.setTranslationY(-headerViewHeight);
            }
        }*/
 /*       if(child.getTranslationY()<headerViewScaleHeight&&child.getTranslationY()>headerViewHeight){
            return true;
        }*/
        if(velocityY == 0){
            Log.i("startAutoScroll"," onStopNestedScroll==0");
        }

        return super.onNestedPreFling(coordinatorLayout,child,target,velocityX,velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, float velocityX, float velocityY, boolean consumed) {
        Log.i("Content-"," onNestedFling "+ consumed+ velocityY);
/*        if(child.getTranslationY()<headerViewScaleHeight&&child.getTranslationY()>headerViewHeight){
            return true;
        }*/
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        Log.i("startAutoScroll"," onStopNestedScroll " + contentView.getScrollState());
        if (child.getTranslationY() <= 0 || child.getTranslationY() >= titleHeight||contentView.getScrollState()!=RecyclerView.SCROLL_STATE_IDLE) {
            // RV 已经归位（完全折叠或完全展开）
            return;
        }
        if (child.getTranslationY() < titleHeight * 0.5f) {
            stopAutoScroll();
            startAutoScroll((int) child.getTranslationY(), 0, 500);
        } else {
            stopAutoScroll();
            startAutoScroll((int) child.getTranslationY(), titleHeight, 500);
        }

    }



}
