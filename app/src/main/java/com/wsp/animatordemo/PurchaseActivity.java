package com.wsp.animatordemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.divider.MaterialDividerItemDecoration;
import com.wsp.animatordemo.databinding.ActivityPurchaseBinding;
import com.wsp.animatordemo.utils.DensityUtil;
import com.wsp.animatordemo.view.MyItemDecoration;
import com.wsp.animatordemo.view.MyScrollView;

import java.util.ArrayList;
import java.util.List;

public class PurchaseActivity extends AppCompatActivity {
    ActivityPurchaseBinding layout;
    List<String> strings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layout = ActivityPurchaseBinding.inflate(getLayoutInflater());
        setContentView(layout.getRoot());
        initVp();
    }

    private void initVp() {
        strings.clear();
        for(int i = 0;i<20;i++){
            strings.add("content" + i);
        }
        layout.vp.setAdapter(new BaseRecyclerViewAdapter<String, SampleTVHolder2>(strings,this) {
            float screenH;

            @Override
            protected void bindItemData(SampleTVHolder2 viewHolder, String data, int position) {
                viewHolder.tv.setText(""+ mDatas.get(position));
                screenH = DensityUtil.getScreenHeight(PurchaseActivity.this);
            }

            @NonNull
            @Override
            public SampleTVHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LinearLayout linearLayout = new LinearLayout(PurchaseActivity.this);
                linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                TextView tv = new TextView(PurchaseActivity.this);
                tv.setBackgroundColor(Color.parseColor("#F7F7F7"));
                tv.setGravity(Gravity.CENTER);
                tv.setTextSize(30);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        DensityUtil.dip2px(PurchaseActivity.this,1000));
                tv.setLayoutParams(layoutParams);
                linearLayout.addView(tv);
                TextView tv1 = new TextView(PurchaseActivity.this);
                tv1.setBackgroundColor(Color.parseColor("#F7F7F7"));
                tv1.setGravity(Gravity.CENTER);
                tv1.setText("上拉查看下一个小号");
                tv1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DensityUtil.dip2px(PurchaseActivity.this,60)));
                linearLayout.addView(tv1);
                ScrollView scrollView = new MyScrollView(PurchaseActivity.this);
                scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                scrollView.addView(linearLayout);
                return new SampleTVHolder2(scrollView);
            }
        });


        float bottomDevider =  DensityUtil.dip2px(PurchaseActivity.this,50);
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
//        layout.vp.addItemDecoration(new MyItemDecoration(PurchaseActivity.this,LinearLayout.VERTICAL));
    }

    public static class SampleTVHolder2 extends RecyclerView.ViewHolder{
        ScrollView ss;
        LinearLayout ll;
        TextView tv;

        public SampleTVHolder2(@NonNull View itemView) {
            super(itemView);
            ss = (ScrollView) itemView;
            ll = (LinearLayout) ss.getChildAt(0);
            tv = (TextView) ll.getChildAt(0);
        }
    }

}