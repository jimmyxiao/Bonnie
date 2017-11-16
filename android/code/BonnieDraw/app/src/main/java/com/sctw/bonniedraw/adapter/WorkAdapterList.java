package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlideApp;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.WorkInfoBean;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterList extends RecyclerView.Adapter<WorkAdapterList.ViewHolder> {
    private List<WorkInfoBean> data = new ArrayList<>();
    private WorkListOnClickListener listener;
    private Context context;

    public WorkAdapterList(Context context, List<WorkInfoBean> data, WorkListOnClickListener listener) {
        this.context = context;
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
        String workImgUrl = "";
        String userImgUrl = "";
        if (!data.get(position).getImagePath().equals("null")) {
            workImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position).getImagePath();
        }
        if (!data.get(position).getUserImgPath().equals("null")) {
            userImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position).getUserImgPath();
        }

        GlideApp.with(context)
                .load(workImgUrl)
                .apply(GlideAppModule.getWorkOptions())
                .thumbnail(Glide.with(context).load(R.drawable.loading))
                .into(holder.mImgViewWrok);
        //作者圖
        GlideApp.with(context)
                .load(userImgUrl)
                .apply(GlideAppModule.getUserOptions())
                .into(holder.mCircleImageView);

        if (data.get(position).getMsgBeanList().isEmpty()) {
            holder.mLinearLayoutWorksMsgOutSide.setVisibility(View.GONE);
        }
        //設定讚
        if (data.get(position).isLike()) {
            holder.imgBtnGood.setSelected(true);
        } else {
            holder.imgBtnGood.setSelected(false);
        }
        //設定收藏
        if (data.get(position).isCollection()) {
            holder.imgBtnCollection.setSelected(true);
        } else {
            holder.imgBtnCollection.setSelected(false);
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
        holder.imgBtnExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkExtraClick(wid);
            }
        });
        holder.imgBtnGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!data.get(holder.getAdapterPosition()).isLike()) {
                    listener.onWorkGoodClick(holder.getAdapterPosition(), true, wid);
                } else {
                    listener.onWorkGoodClick(holder.getAdapterPosition(), false, wid);
                }
            }
        });
        holder.imgBtnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkMsgClick(wid);
            }
        });
        holder.imgBtnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkShareClick(wid);
            }
        });

        holder.imgBtnCollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.get(holder.getAdapterPosition()).isCollection()) {
                    listener.onWorkCollectionClick(holder.getAdapterPosition(), true, wid);
                } else {
                    listener.onWorkCollectionClick(holder.getAdapterPosition(), false, wid);
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUserName, mTvWorkName, mTvWorkGoodTotal, mTvFollow;
        ImageView mImgViewWrok;
        CircleImageView mCircleImageView;
        ImageButton imgBtnExtra, imgBtnGood, imgBtnMsg, imgBtnShare, imgBtnCollection;
        LinearLayout mLinearLayoutWorksMsgOutSide, mLinearLayoutWorksMsg1, mLinearLayoutWorksMsg2;

        ViewHolder(View v) {
            super(v);
            mTvUserName = (TextView) v.findViewById(R.id.textView_works_username);
            mTvWorkName = (TextView) v.findViewById(R.id.textView_works_title);
            mTvWorkGoodTotal = (TextView) v.findViewById(R.id.textView_works_good_total);
            mTvFollow = (TextView) v.findViewById(R.id.textView_works_follow);
            mImgViewWrok = (ImageView) v.findViewById(R.id.imgView_works_img);
            imgBtnExtra = (ImageButton) v.findViewById(R.id.imgBtn_works_extra);
            imgBtnGood = (ImageButton) v.findViewById(R.id.imgBtn_works_good);
            imgBtnMsg = (ImageButton) v.findViewById(R.id.imgBtn_works_msg);
            imgBtnShare = (ImageButton) v.findViewById(R.id.imgBtn_works_share);
            imgBtnCollection = (ImageButton) v.findViewById(R.id.imgBtn_works_collection);
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

    public void setFollow(int position, int isFollow) {
        data.get(position).setIsFollowing(isFollow);
        notifyItemChanged(position);
    }

    public void setCollection(int position, boolean isCollection) {
        data.get(position).setCollection(isCollection);
        notifyItemChanged(position);
    }

    public void refreshData(List<WorkInfoBean> newData) {
        data = newData;
        notifyDataSetChanged();
    }

    public interface WorkListOnClickListener {
        void onWorkImgClick(int wid);

        void onWorkExtraClick(int wid);

        void onWorkGoodClick(int position, boolean like, int wid);

        void onWorkMsgClick(int wid);

        void onWorkShareClick(int wid);

        void onUserClick(int uid);

        void onWorkCollectionClick(int position, boolean isCollection, int wid);

        // 0 = not follow , 1= following
        void onFollowClick(int position, int isFollow, int uid);
    }

}
