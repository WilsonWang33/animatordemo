package com.wsp.animatordemo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wsp.animatordemo.databinding.ActivityGameDetailBinding;
import com.wsp.animatordemo.utils.DensityUtil;
import com.wsp.animatordemo.utils.StatusBarUtil;
import com.youth.banner.adapter.BannerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * created by wyp at 2023/02/17
 */
public class GameDetailActivity extends AppCompatActivity {
    ActivityGameDetailBinding layout;
    List<Integer> imgDatas = new ArrayList<>();
    List<String> strings = new ArrayList<>();

    int scrollY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.transparencyBar(this);
        layout = ActivityGameDetailBinding.inflate(getLayoutInflater());
        setContentView(layout.getRoot());
        initBanner();
        initRecycler();
        initAnimator();
    }

    private void initAnimator() {
        /*layout.nested.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                Log.i("nestedScroll"," : "+scrollY);
                GameDetailActivity.this.scrollY = scrollY;
            }
        });*/
    }

    private void initRecycler() {
        strings.clear();
        for(int i = 0;i<20;i++){
            strings.add("content" + i);
        }
        layout.recycler.setLayoutManager(new LinearLayoutManager(this));
        layout.recycler.setAdapter(new BaseRecyclerViewAdapter<String, SampleTVHolder>(strings,this) {
            @Override
            protected void bindItemData(SampleTVHolder viewHolder, String data, int position) {
                viewHolder.tv.setText(""+ mDatas.get(position));
            }

            @NonNull
            @Override
            public SampleTVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                TextView tv = new TextView(GameDetailActivity.this);
                tv.setBackgroundColor(Color.parseColor("#F7F7F7"));
                tv.setGravity(Gravity.CENTER);
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        DensityUtil.dip2px(mContext,100));
                tv.setLayoutParams(layoutParams);
                return new SampleTVHolder(tv);
            }
        });
    }

    class SampleTVHolder extends RecyclerView.ViewHolder{
        TextView tv;

        public SampleTVHolder(@NonNull View itemView) {
            super(itemView);
            this.tv = (TextView) itemView;
        }
    }

    private void initBanner() {
        imgDatas.add(R.mipmap.living);
        imgDatas.add(R.mipmap.living);
        layout.banner.setAdapter(new NewBannerAdapter(imgDatas));
    }

    class NewBannerAdapter<String> extends BannerAdapter<String,NewBannerHolder>{

        public NewBannerAdapter(List<String> datas) {
            super(datas);
        }

        @Override
        public NewBannerHolder onCreateHolder(ViewGroup parent, int viewType) {
            RelativeLayout relativeLayout = new RelativeLayout(GameDetailActivity.this);
            ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(layoutParams1);
            relativeLayout.setBackground(getDrawable(R.drawable.conner_9_bg_999999));
            ImageView imageView = new ImageView(GameDetailActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(R.mipmap.living);
            relativeLayout.addView(imageView);
//            View itemView = LayoutInflater.from(GameDetailActivity.this).inflate(R.layout.item_banner, null,false);
            return new NewBannerHolder(relativeLayout,imageView);
        }

        @Override
        public void onBindView(NewBannerHolder holder, String data, int position, int size) {
        }
    }

    class NewBannerHolder extends RecyclerView.ViewHolder{
        View itemView;
        ImageView imageView;
        public NewBannerHolder(@NonNull View itemView,ImageView imageView) {
            super(itemView);
            this.itemView = itemView;
            this.imageView = imageView;
        }
    }
}