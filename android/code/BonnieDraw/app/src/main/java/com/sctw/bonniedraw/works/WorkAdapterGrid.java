package com.sctw.bonniedraw.works;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.LoadImageApp;
import com.sctw.bonniedraw.utility.WorkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterGrid extends RecyclerView.Adapter<WorkAdapterGrid.ViewHolder> {
    List<WorkInfo> data = new ArrayList<>();
    WorkGridOnClickListener listner;

    public WorkAdapterGrid(List<WorkInfo> data, WorkGridOnClickListener listner) {
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
                listner.onWorkClick(wid);
            }
        });

        ImageLoader.getInstance()
                .displayImage(GlobalVariable.API_LINK_GET_FILE +data.get(position).getImagePath(), holder.mImageView, LoadImageApp.optionsWorkImg, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        Log.d("IMG LOAD FAIL", "FAIL");
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
}
