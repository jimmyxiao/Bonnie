package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.UserInfoBean;
import com.sctw.bonniedraw.bean.WorkInfoBean;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.PxDpConvert;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


public class WorkProfileAdapterGrid extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    List<WorkInfoBean> data = new ArrayList<>();
    private WorkGridOnClickListener mListner;
    private UserInfoBean mUserInfo;

    public WorkProfileAdapterGrid(Context context, List<WorkInfoBean> worksData, UserInfoBean userInfo, WorkGridOnClickListener listner) {
        this.mContext = context;
        this.data = worksData;
        this.mListner = listner;
        this.mUserInfo = userInfo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        RecyclerView.ViewHolder vh = null;
        switch (viewType) {

            case 0:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_profile, parent, false);
                vh = new WorkProfileAdapterGrid.ViewHolder_profile(v);
                return vh;
            case 1:

                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_work_grid_layout, parent, false);
                vh = new WorkProfileAdapterGrid.ViewHolder_work(v);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 0:


                final WorkProfileAdapterGrid.ViewHolder_profile viewHolder_profile = (WorkProfileAdapterGrid.ViewHolder_profile) holder;
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
                        mListner.onProfileEditClickListener();
                    }
                });

//                viewHolder_profile.mTextViewFans.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mListner.onFansClickListener();
//                    }
//                });

                viewHolder_profile.layoutFans.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListner.onFansClickListener();
                    }
                });



//                viewHolder_profile.mTextViewFollows.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mListner.onFansFollowsListener();
//                    }
//                });

                viewHolder_profile.layoutFollows.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListner.onFansFollowsListener();
                    }
                });


                break;

            case 1:
                final WorkProfileAdapterGrid.ViewHolder_work viewHolder_work = (WorkProfileAdapterGrid.ViewHolder_work) holder;

                final int wid = Integer.parseInt(data.get(holder.getAdapterPosition()-1).getWorkId());
                viewHolder_work.mImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mListner.onGridWorkClick(wid);
                    }
                });
                Glide.with(mContext)
                        .asBitmap()
                        .load(GlobalVariable.API_LINK_GET_FILE + data.get(position-1).getImagePath())
                        .apply(GlideAppModule.getWorkOptions())
                        .transition(withCrossFade())
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                int imageWidth = resource.getWidth();
                                int imageHeight = resource.getHeight();
                                int width = PxDpConvert.getScreenWidth(mContext) / 3;//固定寬度
                                // 寬度固定,然後根據原始寬高比得到此固定寬度需要的高度
                                int height = width * imageHeight / imageWidth;
                                ViewGroup.LayoutParams para = viewHolder_work.mImageView.getLayoutParams();
                                para.height = height;
                                para.width = width;
                                viewHolder_work.mImageView.setImageBitmap(resource);
                            }
                        });

                break;
        }
    }


    class ViewHolder_work extends RecyclerView.ViewHolder {
        ImageView mImageView;

        ViewHolder_work(View v) {
            super(v);
            mImageView = v.findViewById(R.id.imgView_work_grid);
        }
    }

    class ViewHolder_profile extends RecyclerView.ViewHolder {

        CircleImageView imgPhoto;

        TextView mTextViewUserName, mTextViewUserdescription, mTextViewWorks, mTextViewFans, mTextViewFollows;
        LinearLayout layoutFans ,layoutFollows;

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
            layoutFans = (LinearLayout) view.findViewById(R.id.ll_profile_fans);
            layoutFollows = (LinearLayout) view.findViewById(R.id.ll_profile_follow);
        }
    }


    @Override
    public int getItemCount() {

        return data.size() + 1;
    }

    public interface WorkGridOnClickListener {
        void onGridWorkClick(int wid);

        void onProfileEditClickListener();

        void onFansClickListener();

        void onFansFollowsListener();
    }

    public void setData(List<WorkInfoBean> data) {
        this.data = data;
    }

    public List<WorkInfoBean> getData() {
        return data;
    }
}
