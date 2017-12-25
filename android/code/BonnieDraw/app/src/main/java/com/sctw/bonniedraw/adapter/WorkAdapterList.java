package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.utility.GlideApp;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterList extends RecyclerView.Adapter<WorkAdapterList.ViewHolder> {
    private List<WorkInfoBean> data = new ArrayList<>();
    private WorkListOnClickListener listener;
    private Context context;
    private int ownUid;
    private boolean mbShowFollow = false;

    public WorkAdapterList(Context context, List<WorkInfoBean> data, WorkListOnClickListener listener, boolean mbShowFollow) {
        this.context = context;
        this.data = data;
        this.listener = listener;
        this.mbShowFollow = mbShowFollow;
        ownUid = Integer.valueOf(context.getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE).getString(GlobalVariable.API_UID, ""));
    }

    @Override
    public WorkAdapterList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_work, parent, false);
        WorkAdapterList.ViewHolder vh = new WorkAdapterList.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public void onBindViewHolder(final WorkAdapterList.ViewHolder holder, int position, List payloads) {
        final int wid = Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId());
        final int uid = Integer.parseInt(data.get(holder.getAdapterPosition()).getUserId());
        if (payloads.isEmpty()) {
            holder.mTvUserName.setText(data.get(position).getUserName());
            holder.mTvWorkName.setText(data.get(position).getTitle());
            if (data.get(position).getLikeCount() == 0) {
                holder.mTvWorkGoodTotal.setVisibility(View.GONE);
            } else {
                holder.mTvWorkGoodTotal.setText("" + data.get(position).getLikeCount());
            }
            if (data.get(position).getMsgCount() == 0) {
                holder.mTvWorkMsgTotal.setVisibility(View.GONE);
            } else {
                holder.mTvWorkMsgTotal.setText("" + data.get(position).getMsgCount());
            }
            String workImgUrl = "";
            String userImgUrl = "";
            if (!data.get(position).getImagePath().equals("null")) {
                workImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position).getImagePath();
            }
            if (!data.get(position).getUserImgPath().equals("null")) {
                userImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position).getUserImgPath();
            }
            GlideApp.with(context)
                    .asBitmap()
                    .load(workImgUrl)
                    .apply(GlideAppModule.getWorkOptions())
                    .transition(withCrossFade())
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            int imageWidth = resource.getWidth();
                            int imageHeight = resource.getHeight();
                            int width = PxDpConvert.getScreenWidth(context);//固定寬度
                            // 寬度固定,然後根據原始寬高比得到此固定寬度需要的高度
                            int height = width * imageHeight / imageWidth;
                            ViewGroup.LayoutParams para = holder.mImgViewWrok.getLayoutParams();
                            para.height = height;
                            para.width = width;
                            holder.mImgViewWrok.setImageBitmap(resource);
                        }
                    });
            //作者圖
            GlideApp.with(context)
                    .load(userImgUrl)
                    .apply(GlideAppModule.getUserOptions())
                    .into(holder.mCircleImageView);

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

            if (data.get(holder.getAdapterPosition()).getIsFollowing() == 0) {
                holder.mTvFollow.setText(context.getString(R.string.follow_start));
            } else {
                holder.mTvFollow.setText(context.getString(R.string.following));
            }

            //設定追蹤  0=沒追蹤，傳出沒追蹤的值
            holder.mTvFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.get(holder.getAdapterPosition()).getIsFollowing() == 0) {
                        listener.onFollowClick(holder.getAdapterPosition(), 0, uid);
                    } else {
                        listener.onFollowClick(holder.getAdapterPosition(), 1, uid);
                    }
                }
            });

            //是自己的就沒有設定追蹤選項
            if (!mbShowFollow) {
                holder.mTvFollow.setVisibility(View.INVISIBLE);
            } else if (uid == ownUid) {
                //暫時隱藏追蹤選項
                holder.mTvFollow.setVisibility(View.INVISIBLE);
            } else {
                holder.mTvFollow.setVisibility(View.VISIBLE);
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

            final String parseURL = workImgUrl;
            holder.imgBtnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = GlobalVariable.API_LINK_SHARE_LINK + uid;
                    Uri uri = Uri.parse(parseURL);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    if (uri != null) {
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                        shareIntent.setType("image/*");
                        //當用戶選擇短信時使用sms_body取得文字
                        shareIntent.putExtra("sms_body", title);
                    } else {
                        shareIntent.setType("text/plain");
                    }
                    shareIntent.putExtra(Intent.EXTRA_TEXT, title);
                    //自定義選擇框的標題
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(Intent.createChooser(shareIntent, context.getString(R.string.share)));
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
        } else {
            //單獨刷新用
            int type = (int) payloads.get(0);
            switch (type) {
                case 0:
                    //type 0 =Like, 1 =Follow, 2 =Collection
                    if (data.get(position).isLike()) {
                        holder.imgBtnGood.setSelected(true);
                    } else {
                        holder.imgBtnGood.setSelected(false);
                    }
                    if (data.get(position).getLikeCount() == 0) {
                        holder.mTvWorkGoodTotal.setVisibility(View.GONE);
                    } else {
                        holder.mTvWorkGoodTotal.setText("" + data.get(position).getLikeCount());
                        holder.mTvWorkGoodTotal.setVisibility(View.VISIBLE);
                    }
                    break;
                case 1:
                    if (data.get(holder.getAdapterPosition()).getIsFollowing() == 0) {
                        holder.mTvFollow.setText(context.getString(R.string.follow_start));
                    } else {
                        holder.mTvFollow.setText(context.getString(R.string.following));
                    }

                    //設定追蹤  0=沒追蹤，傳出沒追蹤的值
                    holder.mTvFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data.get(holder.getAdapterPosition()).getIsFollowing() == 0) {
                                listener.onFollowClick(holder.getAdapterPosition(), 0, uid);
                            } else {
                                listener.onFollowClick(holder.getAdapterPosition(), 1, uid);
                            }
                        }
                    });
                    //是自己的就沒有設定追蹤選項
                    if (!mbShowFollow) {
                        holder.mTvFollow.setVisibility(View.INVISIBLE);
                    } else if (uid == ownUid) {
                        //暫時隱藏追蹤選項
                        holder.mTvFollow.setVisibility(View.INVISIBLE);
                    } else {
                        holder.mTvFollow.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    if (data.get(position).isCollection()) {
                        holder.imgBtnCollection.setSelected(true);
                    } else {
                        holder.imgBtnCollection.setSelected(false);
                    }
                    break;
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUserName, mTvWorkName, mTvWorkGoodTotal, mTvWorkMsgTotal, mTvFollow;
        ImageView mImgViewWrok;
        CircleImageView mCircleImageView;
        ImageButton imgBtnExtra, imgBtnGood, imgBtnMsg, imgBtnShare, imgBtnCollection;

        ViewHolder(View v) {
            super(v);
            mTvUserName = (TextView) v.findViewById(R.id.textView_works_username);
            mTvWorkName = (TextView) v.findViewById(R.id.textView_works_title);
            mTvWorkGoodTotal = (TextView) v.findViewById(R.id.textView_works_good_total);
            mTvWorkMsgTotal = (TextView) v.findViewById(R.id.textView_works_msg_total);
            mTvFollow = (TextView) v.findViewById(R.id.textView_works_follow);
            mImgViewWrok = (ImageView) v.findViewById(R.id.imgView_works_img);
            imgBtnExtra = (ImageButton) v.findViewById(R.id.imgBtn_works_extra);
            imgBtnGood = (ImageButton) v.findViewById(R.id.imgBtn_works_good);
            imgBtnMsg = (ImageButton) v.findViewById(R.id.imgBtn_works_msg);
            imgBtnShare = (ImageButton) v.findViewById(R.id.imgBtn_works_share);
            imgBtnCollection = (ImageButton) v.findViewById(R.id.imgBtn_works_collection);
            mCircleImageView = v.findViewById(R.id.circleImg_works_user_photo);
        }
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
        Glide.with(context).clear(holder.mImgViewWrok);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<WorkInfoBean> getData() {
        return data;
    }

    public void setData(List<WorkInfoBean> newData) {
        data = newData;
    }

    public void setLike(int position, boolean like) {
        int likeCount = data.get(position).getLikeCount();
        data.get(position).setLike(like);
        if (like) {
            data.get(position).setLikeCount(likeCount + 1);
        } else {
            data.get(position).setLikeCount(likeCount - 1);
        }
        notifyItemChanged(position, 0);
    }

    public void setFollow(int position, int isFollow) {
        data.get(position).setIsFollowing(isFollow);
        int uid = Integer.valueOf(data.get(position).getUserId());
        for (int x = 0; x < data.size(); x++) {
            int dataUid = Integer.valueOf(data.get(x).getUserId());
            if (dataUid == uid) {
                data.get(x).setIsFollowing(isFollow);
                notifyItemChanged(x, 1);
            }
        }
    }

    public void setCollection(int position, boolean isCollection) {
        data.get(position).setCollection(isCollection);
        notifyItemChanged(position, 2);
    }

    public interface WorkListOnClickListener {
        void onWorkImgClick(int wid);

        void onWorkExtraClick(int wid);

        void onWorkGoodClick(int position, boolean like, int wid);

        void onWorkMsgClick(int wid);

        void onUserClick(int uid);

        void onWorkCollectionClick(int position, boolean isCollection, int wid);

        // 0 = not follow , 1= following
        void onFollowClick(int position, int isFollow, int uid);
    }
}
