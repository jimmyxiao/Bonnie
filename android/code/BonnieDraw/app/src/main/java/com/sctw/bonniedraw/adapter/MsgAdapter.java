package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.DateFormatString;
import com.sctw.bonniedraw.utility.Msg;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Msg> data;
    private OnClickMsgPublish listener;

    public MsgAdapter(Context context, ArrayList<Msg> data, OnClickMsgPublish listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        MsgAdapter.ViewHolder vh = new MsgAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MsgAdapter.ViewHolder holder, int position) {
        holder.mTvUsername.setText(data.get(position).getUserName());
        holder.mTvBoard.setText(data.get(position).getMessage());
        String IMG_URL = "";
        Glide.with(context).load(IMG_URL).into(holder.mCircleUserImg).onLoadFailed(ContextCompat.getDrawable(context, R.drawable.photo_round));
        holder.mTvTime.setText(DateFormatString.getDate(Long.valueOf(data.get(position).getCreationDate())));


        holder.mBtnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickLike(holder.getAdapterPosition());
            }
        });
        holder.mTvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickPublish(holder.getAdapterPosition());
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUsername, mTvBoard, mTvTime, mTvLikeConut, mTvReply, mTvLookAll;
        CircleImageView mCircleUserImg;
        ImageButton mBtnLike;

        ViewHolder(View v) {
            super(v);
            mTvUsername = v.findViewById(R.id.textView_msg_username);
            mTvBoard = v.findViewById(R.id.textView_msg_board);
            mTvTime = v.findViewById(R.id.textView_msg_time);
            mTvLikeConut = v.findViewById(R.id.textView_msg_like_count);
            mTvReply = v.findViewById(R.id.textView_msg_reply);
            mTvLookAll = v.findViewById(R.id.textView_msg_look_all);
            mCircleUserImg = v.findViewById(R.id.circle_msg_user_img);
            mBtnLike = v.findViewById(R.id.imgBtn_msg_like);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnClickMsgPublish {
        void onClickLike(int wid);

        void onClickPublish(int wid);
    }
}
