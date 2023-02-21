package com.wsp.animatordemo.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.wsp.animatordemo.utils.DensityUtil;
import com.wsp.animatordemo.R;

public class ContentViewBehavior extends CoordinatorLayout.Behavior {
    private int headerViewHeight;
    private int headerViewScaleHeight;
    private int titleHeight;
    private View contentView;

    private OverScroller scroller;

    private Runnable scrollRunnable = new Runnable() {
        @Override
        public void run() {
            if(scroller.computeScrollOffset()){
                contentView.setTranslationY(scroller.getCurrY());
                ViewCompat.postOnAnimation(contentView,this);
            }
        }
    };

    private void startAutoScroll(int current, int target,int duration) {
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
        if (!scroller.isFinished()) {
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
        contentView = child;
        titleHeight = parent.findViewById(R.id.title).getMeasuredHeight();
        headerViewHeight = parent.findViewById(R.id.banner).getMeasuredHeight()+titleHeight;
        headerViewScaleHeight = headerViewHeight + 2*titleHeight;
        // 设置 top 从而排在 HeaderView的下面
        ViewCompat.offsetTopAndBottom(child, headerViewHeight);
        return true;

    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {

        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL)!=0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
        //处理向上滑动
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
        }
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        if (dyUnconsumed < 0) { // 只处理手指向下滑动的情况
            float newTransY = child.getTranslationY() - dyUnconsumed;
            if (newTransY<=titleHeight) {
//                child.setScaleY(newTransY);
                child.setTranslationY(newTransY);
            } else {
                child.setTranslationY(titleHeight);
            }
        }

    }
}
