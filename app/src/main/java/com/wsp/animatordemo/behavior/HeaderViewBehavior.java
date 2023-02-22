package com.wsp.animatordemo.behavior;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.wsp.animatordemo.utils.DensityUtil;
import com.wsp.animatordemo.R;
import com.wsp.animatordemo.utils.StatusBarUtil;

public class HeaderViewBehavior extends CoordinatorLayout.Behavior {

    private int headerViewHeight;

    private int headerViewScaleHeight;
    private int headerViewWidth;
    private int titleHeight;
    private float oldTranslationY;
    private View scrollView;
    private int screenW;
    private float changeRate = 0;

    public HeaderViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        parent.onLayoutChild(child,layoutDirection);
        scrollView = parent.findViewById(R.id.recycler);
        titleHeight = parent.findViewById(R.id.title).getMeasuredHeight();
        headerViewHeight = child.getMeasuredHeight();
        oldH = headerViewHeight;
        headerViewWidth = child.getMeasuredWidth();
        headerViewScaleHeight = headerViewHeight + 2*titleHeight;
        screenW = DensityUtil.getScreenWidth(child.getContext());
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

    float oldH;
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
        Log.i("headerView-","onDependentViewChanged" + " translationY:"+translationY);
        ViewGroup relativeLayout = (RelativeLayout) child;
        setStatusBarAndTitleBar(parent,relativeLayout,translationY);
        if(translationY<0&&measuredHeight<=headerViewHeight){
            child.setTranslationY(translationY);
        }else if(translationY<=titleHeight){
            child.setTranslationY(0);
            //scale方式，图片变形
            float newH = (float) headerViewHeight+(float) translationY*2;
            float scaleY = (float) newH/(float) headerViewHeight;
            //换算x方向放大倍数
            float maxScaleY = (float) headerViewScaleHeight/(float) headerViewHeight;
            float widthMargin = screenW-headerViewWidth;
            changeRate = scaleY-(maxScaleY-1);
            float dx = widthMargin*(changeRate);
            float scaleX = (dx+headerViewWidth)/headerViewWidth;

            child.setScaleY(scaleY);
            child.setScaleX(scaleX);
            ((RelativeLayout) child).getChildAt(0).setScaleX(scaleY);
            Log.i("headerView-","onDependentViewChanged" + " newH:"+newH);
            Log.i("headerView-","onDependentViewChanged" + " scaleY:"+scaleY);
/*           if(animatorSet.isRunning()){
                animatorSet.end();
            }
            animatorSet.playTogether(
            ObjectAnimator.ofFloat(child,"scaleY", oldScaleY,scaleY).setDuration(100),
            ObjectAnimator.ofFloat(child,"scaleX", oldScaleX,scaleX).setDuration(100),
            ObjectAnimator.ofFloat(relativeLayout.getChildAt(0),"scaleX", oldScaleY,scaleY).setDuration(100));
            animatorSet.start();
            oldScaleY = scaleY;
            oldScaleX = scaleX;*/
/*
            if(valueAnimator!= null&&valueAnimator.isRunning()){
                valueAnimator.end();
            }
            valueAnimator = ValueAnimator.ofFloat(oldH,newH).setDuration(100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    float h = (Float) animation.getAnimatedValue();
                    setNewHeight(child,h);
                    oldH = h;
                }
            });
            valueAnimator.start();*/
        }
        return true;
    }

    private void setStatusBarAndTitleBar(CoordinatorLayout parent,ViewGroup child,float translationY) {
        if(parent.getContext() instanceof Activity){
            if(translationY>(float) titleHeight/2&&StatusBarUtil.statusBarStatus==1) {
                StatusBarUtil.transparencyBar((Activity) parent.getContext());
            }else if(translationY<=(float) titleHeight/2&&StatusBarUtil.statusBarStatus==2){
                StatusBarUtil.setStatusBarLightMode((Activity) parent.getContext());
            }
        }
        child.setBackgroundResource(translationY>=titleHeight?R.drawable.conner_9_top_bg_999999:R.drawable.conner_9_bg_999999);
        parent.findViewById(R.id.title).setAlpha(translationY>0?1-changeRate:1);
    }

    private void setNewHeight(View child, float h) {
        ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
        layoutParams.height = (int) h;
        child.setLayoutParams(layoutParams);
    }

    private float mStartX;
    private float mStartY;

    ValueAnimator valueAnimator;
    float orginY;

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = ev.getX();
                mStartY = ev.getY();
                orginY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float endX = ev.getX();
                float endY = ev.getY();
                float distanceX = Math.abs(endX - mStartX);
                float distanceY = Math.abs(endY - mStartY);
                if(distanceY > distanceX){
                    float dY = endY - mStartY;
                    float translationY = scrollView.getTranslationY();
                    float newTransY = translationY+dY;
//                    child.setY(ev.getRawY()-orginY);
                    Log.i("onInterceptTouchEvent","newTransT "+newTransY);
                    if(newTransY<=titleHeight) {
                        scrollView.setTranslationY(newTransY);
//                        if(valueAnimator!=null&&valueAnimator.isRunning()){
//                            valueAnimator.end();
//                            valueAnimator.removeAllListeners();
//                        }
//                        valueAnimator = ValueAnimator.ofFloat(translationY,newTransY).setDuration(80);
//                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                            @Override
//                            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
//                                scrollView.setTranslationY((Float) animation.getAnimatedValue());
//                            }
//                        });
//                        valueAnimator.start();
                    }else{
                        scrollView.setTranslationY(titleHeight);
                    }
                    mStartY = endY;
                    mStartX = endX;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    public void onStopNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int type) {
        super.onStopNestedScroll(coordinatorLayout, child, target, type);
        Log.i("HeaderView"," onStopNestedScroll");
    }


}
