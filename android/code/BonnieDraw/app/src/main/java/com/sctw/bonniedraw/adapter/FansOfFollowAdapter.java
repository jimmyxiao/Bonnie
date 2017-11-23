package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.FansOfFollowBean;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/11/13.
 */

public class FansOfFollowAdapter extends RecyclerView.Adapter<FansOfFollowAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FansOfFollowBean> data;
    private OnFansOfFollowClick listener;

    public interface OnFansOfFollowClick {
        void onFansOfFollowClick(int position, int fn, int uid);
    }

    public FansOfFollowAdapter(Context context, ArrayList<FansOfFollowBean> data, OnFansOfFollowClick listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public FansOfFollowAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fans_of_follow, parent, false);
        FansOfFollowAdapter.ViewHolder vh = new FansOfFollowAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final FansOfFollowAdapter.ViewHolder holder, int position) {
        holder.mTvUserName.setText(data.get(position).getUserName());
        if (data.get(position).isFollowing()) {
            holder.mBtnFollow.setText("追蹤中");
        } else {
            holder.mBtnFollow.setText("追蹤");
        }

        holder.mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uid = data.get(holder.getAdapterPosition()).getUserId();
                if (data.get(holder.getAdapterPosition()).isFollowing()) {
                    listener.onFansOfFollowClick(holder.getAdapterPosition(), 0, uid);
                } else {
                    listener.onFansOfFollowClick(holder.getAdapterPosition(), 1, uid);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCircleImg;
        TextView mTvUserName;
        Button mBtnFollow;

        ViewHolder(View v) {
            super(v);
            mCircleImg = v.findViewById(R.id.circle_fof_user_img);
            mTvUserName = v.findViewById(R.id.textView_fof_username);
            mBtnFollow = v.findViewById(R.id.btn_fof_follow);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setFollow(int position, boolean isFollowing) {
        data.get(position).setFollowing(isFollowing);
        notifyItemChanged(position);
    }
}