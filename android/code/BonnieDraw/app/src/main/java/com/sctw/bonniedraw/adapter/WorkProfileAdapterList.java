package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.UserInfoBean;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.fragment.EditProfileFragment;
import com.sctw.bonniedraw.utility.GlideApp;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;
import static android.content.Context.MODE_PRIVATE;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


public class WorkProfileAdapterList extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<WorkInfoBean> data = new ArrayList<>();
    private WorkListOnClickListener listener;
    private Context mContext;
    private int ownUid;
    private boolean mbShowFollow = false;
    private UserInfoBean mUserInfo;

    public WorkProfileAdapterList(Context context, List<WorkInfoBean> worksData , UserInfoBean userInfo, WorkListOnClickListener listener, boolean mbShowFollow) {
        this.mContext = context;

        this.data = worksData;
        this.mUserInfo = userInfo;
        //this.data.add(0, null); // for frofile
        this.listener = listener;
        this.mbShowFollow = mbShowFollow;
        ownUid = Integer.valueOf(mContext.getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE).getString(GlobalVariable.API_UID, ""));
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh = null;
        switch (viewType) {

            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_profile, parent, false);
                vh = new WorkProfileAdapterList.ViewHolder_profile(v);
                return vh;
            case 1:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_work, parent, false);
                vh = new WorkProfileAdapterList.ViewHolder_work(v);
                return vh;
        }
        return vh;
    }


    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(position>0)
            return 1;

        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, List payloads) {

        switch (holder.getItemViewType()) {
            case 0:
                // User Profile

                final ViewHolder_profile viewHolder_profile = (ViewHolder_profile) holder;
                viewHolder_profile.mTextViewUserName.setText(mUserInfo.getUserName());
                viewHolder_profile.mTextViewUserdescription.setText(mUserInfo.getDescription());
                viewHolder_profile.mTextViewWorks.setText(String.valueOf(mUserInfo.getWorksNum()));
                viewHolder_profile.mTextViewFans.setText(String.valueOf(mUserInfo.getFansNum()));
                viewHolder_profile.mTextViewFollows.setText(String.valueOf(mUserInfo.getFollowNum()));

                Glide.with(mContext)
                        .load(mUserInfo.getProfilePicture())
                        .apply(GlideAppModule.getUserOptions())
                        .into(viewHolder_profile.imgPhoto);



                viewHolder_profile.mBtnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onProfileEditClickListener();
                    }
                });

                viewHolder_profile.mTextViewFans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFansClickListener();
                    }
                });

                viewHolder_profile.mTextViewFollows.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onFansFollowsListener();
                    }
                });

                break;

            case 1:
                final ViewHolder_work viewHolder_work = (ViewHolder_work) holder;
                final int wid = Integer.parseInt(data.get(holder.getAdapterPosition()-1).getWorkId());
                final int uid = Integer.parseInt(data.get(holder.getAdapterPosition()-1).getUserId());

                if (payloads.isEmpty()) {
                    viewHolder_work.mTvUserName.setText(data.get(position-1).getUserName());
                    viewHolder_work.mTvWorkName.setText(data.get(position-1).getTitle());
                    if (data.get(position-1).getLikeCount() == 0) {
                        viewHolder_work.mTvWorkGoodTotal.setVisibility(View.GONE);
                    } else {
                        viewHolder_work.mTvWorkGoodTotal.setText("" + data.get(position-1).getLikeCount());
                    }
                    if (data.get(position-1).getMsgCount() == 0) {
                        viewHolder_work.mTvWorkMsgTotal.setVisibility(View.GONE);
                    } else {
                        viewHolder_work.mTvWorkMsgTotal.setText("" + data.get(position-1).getMsgCount());
                    }
                    String workImgUrl = "";
                    String userImgUrl = "";
                    if (!data.get(position-1).getImagePath().equals("null")) {
                        workImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position-1).getImagePath();
                    }

                    if (!data.get(position-1).getUserImgPath().equals("null")) {
                        userImgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(position-1).getUserImgPath();
                    }


                    GlideApp.with(mContext)
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
                                    int width = PxDpConvert.getScreenWidth(mContext);//固定寬度
                                    // 寬度固定,然後根據原始寬高比得到此固定寬度需要的高度
                                    int height = width * imageHeight / imageWidth;
                                    ViewGroup.LayoutParams para = viewHolder_work.mImgViewWrok.getLayoutParams();
                                    para.height = height;
                                    para.width = width;
                                    viewHolder_work.mImgViewWrok.setImageBitmap(resource);
                                }
                            });
                    //作者圖
                    /*
                    GlideApp.with(mContext)
                            .load(userImgUrl)
                            .apply(GlideAppModule.getUserOptions())
                            .into(viewHolder_work.mCircleImageView);
                    */
                    //設定讚
                    if (data.get(position-1).isLike()) {
                        viewHolder_work.imgBtnGood.setSelected(true);
                    } else {
                        viewHolder_work.imgBtnGood.setSelected(false);
                    }
                    //設定收藏
                    if (data.get(position-1).isCollection()) {
                        viewHolder_work.imgBtnCollection.setSelected(true);
                    } else {
                        viewHolder_work.imgBtnCollection.setSelected(false);
                    }

                    if (data.get(viewHolder_work.getAdapterPosition()-1).getIsFollowing() == 0) {
                        viewHolder_work.mTvFollow.setText(mContext.getString(R.string.m01_01_start_follow));
                    } else {
                        viewHolder_work.mTvFollow.setText(mContext.getString(R.string.uc_following));
                    }

                    //設定追蹤  0=沒追蹤，傳出沒追蹤的值
                    viewHolder_work.mTvFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (data.get(holder.getAdapterPosition()-1).getIsFollowing() == 0) {
                                listener.onFollowClick(holder.getAdapterPosition(), 0, uid);
                            } else {
                                listener.onFollowClick(holder.getAdapterPosition(), 1, uid);
                            }
                        }
                    });

                    //是自己的就沒有設定追蹤選項
                    if (!mbShowFollow) {
                        viewHolder_work.mTvFollow.setVisibility(View.INVISIBLE);
                    } else if (uid == ownUid) {
                        //暫時隱藏追蹤選項
                        viewHolder_work.mTvFollow.setVisibility(View.INVISIBLE);
                    } else {
                        viewHolder_work.mTvFollow.setVisibility(View.VISIBLE);
                    }

                    viewHolder_work.mTvUserName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onUserClick(uid);
                        }
                    });

                    viewHolder_work.mCircleImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            listener.onUserClick(uid);
                        }
                    });

                    viewHolder_work.mImgViewWrok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onWorkImgClick(wid);
                        }
                    });
                    viewHolder_work.imgBtnExtra.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onWorkExtraClick(uid, wid);
                        }
                    });
                    viewHolder_work.imgBtnGood.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!data.get(holder.getAdapterPosition()-1).isLike()) {
                                listener.onWorkGoodClick(holder.getAdapterPosition(), true, wid);
                            } else {
                                listener.onWorkGoodClick(holder.getAdapterPosition(), false, wid);
                            }
                        }
                    });
                    viewHolder_work.imgBtnMsg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            listener.onWorkMsgClick(wid);
                        }
                    });

                    viewHolder_work.imgBtnShare.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String title = GlobalVariable.API_LINK_SHARE_LINK + wid;
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BonnieDraw");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, title);
                            //自定義選擇框的標題
                            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(Intent.createChooser(shareIntent, mContext.getString(R.string.uc_share)));
                        }
                    });

                    viewHolder_work.imgBtnCollection.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!data.get(holder.getAdapterPosition()-1).isCollection()) {
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
                            if (data.get(position-1).isLike()) {
                                viewHolder_work.imgBtnGood.setSelected(true);
                            } else {
                                viewHolder_work.imgBtnGood.setSelected(false);
                            }
                            if (data.get(position-1).getLikeCount() == 0) {
                                viewHolder_work.mTvWorkGoodTotal.setVisibility(View.GONE);
                            } else {
                                viewHolder_work.mTvWorkGoodTotal.setText("" + data.get(position-1).getLikeCount());
                                viewHolder_work.mTvWorkGoodTotal.setVisibility(View.VISIBLE);
                            }
                            break;
                        case 1:
                            if (data.get(holder.getAdapterPosition()-1).getIsFollowing() == 0) {
                                viewHolder_work.mTvFollow.setText(mContext.getString(R.string.m01_01_start_follow));
                            } else {
                                viewHolder_work.mTvFollow.setText(mContext.getString(R.string.uc_following));
                            }

                            //設定追蹤  0=沒追蹤，傳出沒追蹤的值
                            viewHolder_work.mTvFollow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (data.get(holder.getAdapterPosition()-1).getIsFollowing() == 0) {
                                        listener.onFollowClick(holder.getAdapterPosition(), 0, uid);
                                    } else {
                                        listener.onFollowClick(holder.getAdapterPosition(), 1, uid);
                                    }
                                }
                            });
                            //是自己的就沒有設定追蹤選項
                            if (!mbShowFollow) {
                                viewHolder_work.mTvFollow.setVisibility(View.INVISIBLE);
                            } else if (uid == ownUid) {
                                //暫時隱藏追蹤選項
                                viewHolder_work.mTvFollow.setVisibility(View.INVISIBLE);
                            } else {
                                viewHolder_work.mTvFollow.setVisibility(View.VISIBLE);
                            }
                            break;
                        case 2:
                            if (data.get(position-1).isCollection()) {
                                viewHolder_work.imgBtnCollection.setSelected(true);
                            } else {
                                viewHolder_work.imgBtnCollection.setSelected(false);
                            }
                            break;
                    }
                }
                break;
        }
    }

    class ViewHolder_work extends RecyclerView.ViewHolder {
        TextView mTvUserName, mTvWorkName, mTvWorkGoodTotal, mTvWorkMsgTotal, mTvFollow;
        ImageView mImgViewWrok;
        CircleImageView mCircleImageView;
        ImageButton imgBtnExtra, imgBtnGood, imgBtnMsg, imgBtnShare, imgBtnCollection;
        LinearLayout mLinearLayout;

        ViewHolder_work(View v) {
            super(v);
            mTvUserName = (TextView) v.findViewById(R.id.textView_works_username);
            mTvUserName.setVisibility(View.GONE);
            mTvWorkName = (TextView) v.findViewById(R.id.textView_works_title);
            mTvWorkName.setVisibility(View.GONE);
            mTvWorkGoodTotal = (TextView) v.findViewById(R.id.textView_works_good_total);
            mTvWorkMsgTotal = (TextView) v.findViewById(R.id.textView_works_msg_total);
            mTvFollow = (TextView) v.findViewById(R.id.textView_works_follow);
            mImgViewWrok = (ImageView) v.findViewById(R.id.imgView_works_img);
            imgBtnExtra = (ImageButton) v.findViewById(R.id.imgBtn_works_extra);
            imgBtnExtra.setVisibility(View.GONE);
            imgBtnGood = (ImageButton) v.findViewById(R.id.imgBtn_works_good);
            imgBtnMsg = (ImageButton) v.findViewById(R.id.imgBtn_works_msg);
            imgBtnShare = (ImageButton) v.findViewById(R.id.imgBtn_works_share);
            imgBtnCollection = (ImageButton) v.findViewById(R.id.imgBtn_works_collection);
            mCircleImageView = v.findViewById(R.id.circleImg_works_user_photo);
            mCircleImageView.setVisibility(View.GONE);
            mLinearLayout = (LinearLayout) v.findViewById(R.id.linearLayout_work_item);
            mLinearLayout.setVisibility(View.GONE);
        }
    }

    class ViewHolder_profile extends RecyclerView.ViewHolder {

        CircleImageView imgPhoto;

        TextView mTextViewUserName, mTextViewUserdescription, mTextViewWorks, mTextViewFans, mTextViewFollows;
        private Button mBtnEdit;

        ViewHolder_profile(View view) {
            super(view);
            imgPhoto = (CircleImageView) view.findViewById(R.id.circleImg_profile_photo);
            mTextViewUserName = (TextView) view.findViewById(R.id.textView_profile_userName);
            mBtnEdit = view.findViewById(R.id.btn_profile_edit);
            mTextViewUserdescription = view.findViewById(R.id.textView_profile_user_description);
            mTextViewWorks = (TextView) view.findViewById(R.id.textView_profile_userworks);
            mTextViewFans = (TextView) view.findViewById(R.id.textView_profile_fans);
            mTextViewFollows = (TextView) view.findViewById(R.id.textView_profile_follows);
        }
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if(holder.getItemViewType() ==1){
            ViewHolder_work vh = (ViewHolder_work) holder;
            Glide.with(mContext).clear( vh.mImgViewWrok);
        }
    }

    @Override
    public int getItemCount() {
        return data.size()+1;
    }

    public List<WorkInfoBean> getData() {
        return data;
    }

    public void setData(List<WorkInfoBean> newData) {
        data = newData;
    }

    public void setLike(int position, boolean like) {
        int likeCount = data.get(position-1).getLikeCount();
        data.get(position-1).setLike(like);
        if (like) {
            data.get(position-1).setLikeCount(likeCount + 1);
        } else {
            data.get(position-1).setLikeCount(likeCount - 1);
        }
        notifyItemChanged(position, 0);
    }

    public void setFollow(int position, int isFollow) {
        data.get(position-1).setIsFollowing(isFollow);
        int uid = Integer.valueOf(data.get(position-1).getUserId());
        for (int x = 0; x < data.size(); x++) {
            int dataUid = Integer.valueOf(data.get(x).getUserId());
            if (dataUid == uid) {
                data.get(x).setIsFollowing(isFollow);
                notifyItemChanged(x, 1);
            }
        }
    }

    public void setCollection(int position, boolean isCollection) {
        data.get(position-1).setCollection(isCollection);
        notifyItemChanged(position, 2);
    }

    public interface WorkListOnClickListener {
        void onWorkImgClick(int wid);

        void onWorkExtraClick(int uid, int wid);

        void onWorkGoodClick(int position, boolean like, int wid);

        void onWorkMsgClick(int wid);

        void onUserClick(int uid);

        void onWorkCollectionClick(int position, boolean isCollection, int wid);

        // 0 = not follow , 1= following
        void onFollowClick(int position, int isFollow, int uid);

        void onProfileEditClickListener();

        void onFansClickListener();

        void onFansFollowsListener();
    }
}
