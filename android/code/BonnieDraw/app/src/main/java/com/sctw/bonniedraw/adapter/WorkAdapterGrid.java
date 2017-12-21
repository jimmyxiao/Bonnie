package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterGrid extends RecyclerView.Adapter<WorkAdapterGrid.ViewHolder> {
    Context context;
    List<WorkInfoBean> data = new ArrayList<>();
    WorkGridOnClickListener listner;

    public WorkAdapterGrid(Context context, List<WorkInfoBean> data, WorkGridOnClickListener listner) {
        this.context = context;
        this.data = data;
        this.listner = listner;
    }

    @Override
    public WorkAdapterGrid.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work_grid_layout, parent, false);
        WorkAdapterGrid.ViewHolder vh = new WorkAdapterGrid.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final WorkAdapterGrid.ViewHolder holder, int position) {
        final int wid = Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId());
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onGridWorkClick(wid);
            }
        });
        Glide.with(context)
                .asBitmap()
                .load(GlobalVariable.API_LINK_GET_FILE + data.get(position).getImagePath())
                .apply(GlideAppModule.getWorkOptions())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        int imageWidth = resource.getWidth();
                        int imageHeight = resource.getHeight();
                        int width = PxDpConvert.getScreenWidth(context) / 3;//固定寬度
                        // 寬度固定,然後根據原始寬高比得到此固定寬度需要的高度
                        int height = width * imageHeight / imageWidth;
                        ViewGroup.LayoutParams para = holder.mImageView.getLayoutParams();
                        para.height = height;
                        para.width = width;
                        holder.mImageView.setImageBitmap(resource);
                    }
                });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;

        ViewHolder(View v) {
            super(v);
            mImageView = v.findViewById(R.id.imgView_work_grid);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface WorkGridOnClickListener {
        void onGridWorkClick(int wid);
    }

    public void setData(List<WorkInfoBean> data) {
        this.data = data;
    }

    public List<WorkInfoBean> getData() {
        return data;
    }
}
