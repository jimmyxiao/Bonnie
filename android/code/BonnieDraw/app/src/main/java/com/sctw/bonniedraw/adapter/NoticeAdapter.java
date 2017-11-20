package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.bean.NoticeInfoBean;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/11/13.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private List<NoticeInfoBean> data;
    private Context context;

    public NoticeAdapter(Context context, List<NoticeInfoBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false);
        NoticeAdapter.ViewHolder vh = new NoticeAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final NoticeAdapter.ViewHolder holder, int position) {
        String workImgUrl = "";
        String userImgUrl = "";
        if (!data.get(position).getImagePath().equals("null")) {
            workImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position).getImagePath();
        }
        if (!data.get(position).getProfilePicture().equals("null")) {
            userImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position).getProfilePicture();
        }
        holder.mTvName.setText(data.get(holder.getAdapterPosition()).getUserNameFollow());
        holder.mTvTime.setText(data.get(holder.getAdapterPosition()).getCreationDate());
        holder.mTvMsg.setText(selectType(data.get(holder.getAdapterPosition()).getWorkMsg(), data.get(holder.getAdapterPosition()).getNotiMsgType()));
        Glide.with(context).load(workImgUrl).into(holder.mIvWork);
        Glide.with(context).load(userImgUrl).apply(GlideAppModule.getUserOptions()).into(holder.circleImageView);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView mTvName, mTvTime, mTvMsg;
        ImageView mIvWork;

        ViewHolder(View v) {
            super(v);
            circleImageView = v.findViewById(R.id.circle_notice_user_img);
            mTvName = v.findViewById(R.id.textView_notice_name);
            mTvTime = v.findViewById(R.id.textView_notice_time);
            mTvMsg = v.findViewById(R.id.textView_notice_msg);
            mIvWork = v.findViewById(R.id.imgView_notice_work);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * notiMsgType
     * 1: 被追踨通知 ;    xxxx 開始追蹤你
     * 2: 朋友加入通知 ;  facebook 朋友 xxx 加入BonnieDraw
     * 3: 留言通知 :  xxx 在你的作品留言
     * 4:  追蹤人發表通知 : 你追蹤的 xxx 分享一則作品
     * 5: 作品按讚通知 : XXX 在你的作品按讚
     */

    private String selectType(String msg, int notiMsgType) {
        switch (notiMsgType) {
            case 1:
                return "開始追蹤你";
            case 2:
                return "你的好友，加入BonnieDraw";
            case 3:
                return "對你的作品留言：" + "\n「" + msg + "」";
            case 4:
                return "分享一則新作品";
            case 5:
                return "對你的作品按讚";
            default:
                return "";
        }
    }

    public interface OnNoticeClickListener {
        void onWorkImgClick(int wid);

        void onUserImgClick(int uid);
    }
}
