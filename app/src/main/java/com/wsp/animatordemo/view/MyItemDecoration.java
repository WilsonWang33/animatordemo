package com.wsp.animatordemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MyItemDecoration extends RecyclerView.ItemDecoration {

    private final int orientation;
    private Drawable mDivider;

    /**
     * 在构造方法中加载系统自带的分割线（就是ListView用的那个分割线）
     */
    public MyItemDecoration(Context context, int orientation) {
        this.orientation = orientation;
        int[] attrs = new int[]{android.R.attr.listDivider};
        TypedArray a = context.obtainStyledAttributes(attrs);
        mDivider = a.getDrawable(0);
        paint.setTextSize(20);
        paint.setColor(Color.RED);
        a.recycle();
    }


    /**
     * 画线
     */
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (orientation == RecyclerView.HORIZONTAL) {
            drawVertical(c, parent, state);
        } else if (orientation == RecyclerView.VERTICAL) {
            drawHorizontal(c, parent, state);
        }
    }

    /**
     * 设置条目周边的偏移量
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (orientation == RecyclerView.HORIZONTAL) {
            //画垂直线
            outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
        } else if (orientation == RecyclerView.VERTICAL) {
            //画水平线
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight()+130);
        }
    }


    /**
     * 画竖直分割线
     */
    private void drawVertical(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int top = child.getTop() - params.topMargin;
            int right = left + mDivider.getIntrinsicWidth();
            int bottom = child.getBottom() + params.bottomMargin;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    /**
     * 画水平分割线
     */
    private void drawHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getLeft() - params.leftMargin;
            int top = child.getBottom() + params.bottomMargin;
            int right = child.getRight() + params.rightMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom+130);
            mDivider.draw(c);
            c.drawText("ABC",left,bottom,paint);
        }
    }
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

/*    private void drawText(String text, float width, float height, Canvas c, RecyclerView parent) {
        float fontLength = getFontLength(paint, text);
        float fontHeight = getFontHeight(paint);
        float tx = (width - fontLength) / 2;
        float ty = height - fontHeight / 2 + getFontLeading(paint);
        c.drawText(text, tx, ty, paint);
    }*/

}
