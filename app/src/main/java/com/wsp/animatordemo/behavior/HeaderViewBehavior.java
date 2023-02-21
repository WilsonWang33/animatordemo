package com.wsp.animatordemo.behavior;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

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

    private View scrollView;

    private int mTouchSlop;

    public HeaderViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull View child, int layoutDirection) {
        parent.onLayoutChild(child,layoutDirection);
        scrollView = parent.findViewById(R.id.recycler);
        mTouchSlop = ViewConfiguration.get(child.getContext()).getScaledTouchSlop() / 2;
        titleHeight = parent.findViewById(R.id.title).getMeasuredHeight();
        headerViewHeight = child.getMeasuredHeight();
        oldH = headerViewHeight;
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
//        Log.i("headerView-","onDependentViewChanged" + " translationY:"+translationY);
//        Log.i("headerView-","onDependentViewChanged" + " title:"+titleHeight);
        Log.i("headerView-","onDependentViewChanged" + " headerViewHeight:"+headerViewHeight);
        if(translationY<0&&measuredHeight<=headerViewHeight){
            child.setTranslationY(translationY);
        }else if(translationY<=titleHeight){
            //scale方式，图片变形
            float newH = headerViewHeight+translationY*2;

            /*Log.i("headerView-","onDependentViewChanged" + " newH:"+newH);
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
*/
            //修改layoutP方式

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
            valueAnimator.start();
        }
        return true;
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
                    }
                    mStartY = endY;
                    mStartX = endX;
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
