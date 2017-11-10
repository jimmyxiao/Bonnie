package com.sctw.bonniedraw.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.LoadImageApp;
import com.sctw.bonniedraw.utility.WorkInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterList extends RecyclerView.Adapter<WorkAdapterList.ViewHolder> {
    List<WorkInfo> data = new ArrayList<>();
    WorkListOnClickListener listener;

    public WorkAdapterList(List<WorkInfo> data, WorkListOnClickListener listener) {
        this.data = data;
        this.listener = listener;

    }

    @Override
    public WorkAdapterList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work, parent, false);
        WorkAdapterList.ViewHolder vh = new WorkAdapterList.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final WorkAdapterList.ViewHolder holder, int position) {
        holder.mTvUserName.setText(data.get(position).getUserName());
        holder.mTvWorkName.setText(data.get(position).getTitle());
        holder.mTvWorkGoodTotal.setText(String.format(holder.mTvWorkGoodTotal.getContext().getString(R.string.work_good_total), data.get(position).getLikeCount()));
        final int wid = Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId());
        final int uid = Integer.parseInt(data.get(holder.getAdapterPosition()).getUserId());
        //作品圖
        ImageLoader.getInstance()
                .displayImage(GlobalVariable.API_LINK_GET_FILE + data.get(position).getImagePath(), holder.mImgViewWrok, LoadImageApp.optionsWorkImg);
        //作者圖
        String userImgUrl = "";
        if (data.get(position).getUserImgPath().equals("null")) {
            userImgUrl = "";
        } else {
            userImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position).getUserImgPath();
        }
        ImageLoader.getInstance()
                .displayImage(userImgUrl, holder.mCircleImageView, LoadImageApp.optionsUserImg);

        if (data.get(position).getMsgList().isEmpty()) {
            holder.mLinearLayoutWorksMsgOutSide.setVisibility(View.GONE);
        }

        if (data.get(position).isLike()) {
            holder.imgBtnWorksUserGood.setSelected(true);
        } else {
            holder.imgBtnWorksUserGood.setSelected(false);
        }

        holder.mTvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserClick(uid);
            }
        });

        holder.mCircleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserClick(uid);
            }
        });

        holder.mImgViewWrok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkImgClick(wid);
            }
        });
        holder.imgBtnWorksUserExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkExtraClick(wid);
            }
        });
        holder.imgBtnWorksUserGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!data.get(holder.getAdapterPosition()).isLike()) {
                    listener.onWorkGoodClick(holder.getAdapterPosition(), true, wid);
                } else {
                    listener.onWorkGoodClick(holder.getAdapterPosition(), false, wid);
                }
            }
        });
        holder.imgBtnWorksUserMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkMsgClick(wid);
            }
        });
        holder.imgBtnWorksUserShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkShareClick(wid);
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUserName, mTvWorkName, mTvWorkGoodTotal;
        ImageView mImgViewWrok;
        CircleImageView mCircleImageView;
        ImageButton imgBtnWorksUserExtra, imgBtnWorksUserGood, imgBtnWorksUserMsg, imgBtnWorksUserShare;
        LinearLayout mLinearLayoutWorksMsgOutSide, mLinearLayoutWorksMsg1, mLinearLayoutWorksMsg2;

        ViewHolder(View v) {
            super(v);
            mTvUserName = (TextView) v.findViewById(R.id.textView_works_username);
            mTvWorkName = (TextView) v.findViewById(R.id.textView_works_title);
            mTvWorkGoodTotal = (TextView) v.findViewById(R.id.textView_works_good_total);
            mImgViewWrok = (ImageView) v.findViewById(R.id.imgView_works_img);
            imgBtnWorksUserExtra = (ImageButton) v.findViewById(R.id.imgBtn_works_extra);
            imgBtnWorksUserGood = (ImageButton) v.findViewById(R.id.imgBtn_works_good);
            imgBtnWorksUserMsg = (ImageButton) v.findViewById(R.id.imgBtn_works_msg);
            imgBtnWorksUserShare = (ImageButton) v.findViewById(R.id.imgBtn_works_share);
            mLinearLayoutWorksMsg1 = (LinearLayout) v.findViewById(R.id.linearLayout_works_msg_1);
            mLinearLayoutWorksMsg2 = (LinearLayout) v.findViewById(R.id.linearLayout_works_msg_2);
            mLinearLayoutWorksMsgOutSide = (LinearLayout) v.findViewById(R.id.linearLayout_works_msg_outside);
            mCircleImageView = v.findViewById(R.id.circleImg_works_user_photo);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setLike(int position, boolean like) {
        int likeCount = data.get(position).getLikeCount();
        data.get(position).setLike(like);
        if (like) {
            data.get(position).setLikeCount(likeCount + 1);
        } else {
            data.get(position).setLikeCount(likeCount - 1);
        }
        notifyItemChanged(position);
    }

    public interface WorkListOnClickListener {
        void onWorkImgClick(int wid);

        void onWorkExtraClick(int wid);

        void onWorkGoodClick(int position, boolean like, int wid);

        void onWorkMsgClick(int wid);

        void onWorkShareClick(int wid);

        void onUserClick(int uid);
    }

}
