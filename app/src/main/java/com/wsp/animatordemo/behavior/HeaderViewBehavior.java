package com.wsp.animatordemo.behavior;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.wsp.animatordemo.utils.DensityUtil;
import com.wsp.animatordemo.R;
import com.youth.banner.Banner;

public class HeaderViewBehavior extends CoordinatorLayout.Behavior {

    private int headerViewHeight;

    private int headerViewScaleHeight;
    private int headerViewWidth;
    private int titleHeight;

    private float oldTranslationY;


    public HeaderViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        parent.onLayoutChild(child,layoutDirection);
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
//        Log.i("headerView-","onDependentViewChanged" + " translationY:"+translationY);
//        Log.i("headerView-","onDependentViewChanged" + " title:"+titleHeight);
        Log.i("headerView-","onDependentViewChanged" + " headerViewHeight:"+headerViewHeight);
        if(translationY<0&&measuredHeight<=headerViewHeight){
            child.setTranslationY(translationY);
        }else if(translationY<=titleHeight){
            //scale方式，图片变形
            float newH = headerViewHeight+translationY*2;
            Log.i("headerView-","onDependentViewChanged" + " newH:"+newH);
            float scaleY = newH/headerViewHeight;
            Log.i("headerView-","onDependentViewChanged" + " scaleY:"+scaleY);

            //换算x方向放大倍数
            float maxScaleY = (headerViewHeight+2*titleHeight)/headerViewHeight;
            float widthMargin = DensityUtil.getScreenWidth(child.getContext())-headerViewWidth;
            float dx = widthMargin*(scaleY-1);
            float scaleX = (dx+headerViewWidth)/headerViewWidth;
            if(animatorSet.isRunning()){
                animatorSet.end();
                animatorSet.removeAllListeners();
            }
            animatorSet.playTogether(
            ObjectAnimator.ofFloat(child,"scaleY", oldScaleY,scaleY).setDuration(100),
            ObjectAnimator.ofFloat(child,"scaleX", oldScaleX,scaleX).setDuration(100));
            animatorSet.start();
            oldScaleY = scaleY;
            oldScaleX = scaleX;

            //修改layoutP方式

        }
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL)!=0;
    }

    @Override
    public void onNestedPreScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type);
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);
        if(dyUnconsumed<0){

        }
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return super.onTouchEvent(parent, child, ev);
    }
}