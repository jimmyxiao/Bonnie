package com.sctw.bonniedraw.works;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

import java.util.List;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class FollowOwnAdapterList extends RecyclerView.Adapter<FollowOwnAdapterList.ViewHolder> {
    List<String> data;
    FollowOwnListOnClickListener listener;

    public FollowOwnAdapterList(List<String> data, FollowOwnListOnClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public FollowOwnAdapterList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow, parent, false);
        FollowOwnAdapterList.ViewHolder vh = new FollowOwnAdapterList.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final FollowOwnAdapterList.ViewHolder holder, int position) {
        holder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFollowClick(holder.getAdapterPosition());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView followText;
        ImageView followImg;
        Button followBtn;

        ViewHolder(View v) {
            super(v);
            followBtn = (Button) v.findViewById(R.id.btn_follow_own);
            followText = (TextView) v.findViewById(R.id.textView_follow_own);
            followImg = (ImageView) v.findViewById(R.id.imgView_follow_own);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
