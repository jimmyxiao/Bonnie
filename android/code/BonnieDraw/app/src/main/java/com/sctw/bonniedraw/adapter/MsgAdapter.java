package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.MsgBean;
import com.sctw.bonniedraw.utility.DateFormatString;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    private Context context;
    private ArrayList<MsgBean> data;
    private OnClickMsgPublish listener;
    private int ownUid;

    public MsgAdapter(Context context, ArrayList<MsgBean> data, OnClickMsgPublish listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        SharedPreferences prefs = context.getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        ownUid = Integer.valueOf(prefs.getString(GlobalVariable.API_UID, ""));
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
        if (!data.get(position).getProfilePicture().equals("null")) {
            IMG_URL = GlobalVariable.API_LINK_GET_FILE + data.get(position).getProfilePicture();
        }
        Glide.with(context).load(IMG_URL).apply(GlideAppModule.getUserOptions()).into(holder.mCircleUserImg);
        holder.mTvTime.setText(DateFormatString.getDate(context,Long.valueOf(data.get(position).getCreationDate())));
        final int uid = data.get(holder.getAdapterPosition()).getUserId();
        final int wid = data.get(holder.getAdapterPosition()).getWorksId();
        final int worksMsgId = data.get(holder.getAdapterPosition()).getWorksMsgId();
        if (data.get(holder.getAdapterPosition()).getUserId() == ownUid) {
            holder.mBtnExtra.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClickExtra(holder.getAdapterPosition(), worksMsgId);
                }
            });
        } else {
            holder.mBtnExtra.setVisibility(View.INVISIBLE);
        }

        holder.mTvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserClick(uid);
            }
        });


        holder.mCircleUserImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserClick(uid);
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUsername, mTvBoard, mTvTime, mTvReply, mTvLookAll;
        CircleImageView mCircleUserImg;
        ImageButton mBtnExtra;

        ViewHolder(View v) {
            super(v);
            mTvUsername = v.findViewById(R.id.textView_msg_username);
            mTvBoard = v.findViewById(R.id.textView_msg_board);
            mTvTime = v.findViewById(R.id.textView_msg_time);
            mTvLookAll = v.findViewById(R.id.textView_msg_look_all);
            mBtnExtra = v.findViewById(R.id.imgBtn_msg_extra);
            mCircleUserImg = v.findViewById(R.id.circle_msg_user_img);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public interface OnClickMsgPublish {
        void onClickExtra(int position, int msgId);

        void onUserClick(int uid);
    }

    public void deleteMsg(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }


}
