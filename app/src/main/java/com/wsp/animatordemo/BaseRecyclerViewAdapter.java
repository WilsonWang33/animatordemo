package com.wsp.animatordemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected List<T> mDatas = new ArrayList<>();
    protected Context mContext;
    protected LayoutInflater inflater;
    AdapterView.OnItemClickListener mItemClickListener;

    public BaseRecyclerViewAdapter(List<T> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        T item = getItem(position);
        bindItemData(holder, item, position);
        setupOnItemClick(holder, position);
    }

    protected abstract void bindItemData(VH viewHolder, T data, int position);

    protected void setupOnItemClick(final VH viewHolder, final int position) {
        if (mItemClickListener != null) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemClickListener.onItemClick(null, viewHolder.itemView, position, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public T getItem(int position) {
        position = Math.max(0, position);
        return mDatas.get(position);
    }

    public List<T> getDataSource() {
        return mDatas;
    }

    public void addData(List<T> newItems) {
        if (newItems != null) {
            mDatas.addAll(newItems);
            notifyDataSetChanged();
        }
    }
    public void deleteItem(int position) {
        mDatas.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void updateListViewData(List<T> lists) {
        mDatas.clear();
        if (lists != null) {
            mDatas.addAll(lists);
            notifyDataSetChanged();
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.mItemClickListener = listener;
    }

}