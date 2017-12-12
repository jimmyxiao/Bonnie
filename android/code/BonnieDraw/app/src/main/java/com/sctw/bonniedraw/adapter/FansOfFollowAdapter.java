package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.FansOfFollowBean;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/11/13.
 */

public class FansOfFollowAdapter extends RecyclerView.Adapter<FansOfFollowAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<FansOfFollowBean> data, tempData;
    private OnFansOfFollowClick listener;
    private FofFliter fofFilter;

    @Override
    public Filter getFilter() {
        if (fofFilter == null) {
            fofFilter = new FofFliter();
        }
        return fofFilter;
    }

    public interface OnFansOfFollowClick {
        void onFansOfFollowOnClickFollow(int position, int fn, int uid);

        void onFansOfFollowOnClickUser(int uid);
    }

    public FansOfFollowAdapter(Context context, ArrayList<FansOfFollowBean> data, OnFansOfFollowClick listener) {
        this.context = context;
        this.data = data;
        this.tempData = data;
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
        String imgUrl = "";
        if (!data.get(holder.getAdapterPosition()).getProfilePicture().equals("null")) {
            imgUrl = GlobalVariable.API_LINK_GET_FILE + data.get(holder.getAdapterPosition()).getProfilePicture();
        }
        Glide.with(context).load(imgUrl).apply(GlideAppModule.getUserOptions()).into(holder.mCircleImg);

        holder.mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int uid = data.get(holder.getAdapterPosition()).getUserId();
                if (data.get(holder.getAdapterPosition()).isFollowing()) {
                    listener.onFansOfFollowOnClickFollow(holder.getAdapterPosition(), 0, uid);
                } else {
                    listener.onFansOfFollowOnClickFollow(holder.getAdapterPosition(), 1, uid);
                }
            }
        });

        holder.mTvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int uid = data.get(holder.getAdapterPosition()).getUserId();
                listener.onFansOfFollowOnClickUser(uid);
            }
        });

        holder.mCircleImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int uid = data.get(holder.getAdapterPosition()).getUserId();
                listener.onFansOfFollowOnClickUser(uid);
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


    class FofFliter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<FansOfFollowBean> newData = new ArrayList();
            if (charSequence != null && charSequence.toString().trim().length() > 0) {
                for (int i = 0; i < tempData.size(); i++) {
                    String content = (String) tempData.get(i).getUserName();
                    if (content.contains(charSequence)) {
                        newData.add(tempData.get(i));
                    }
                }
            } else {
                newData = tempData;
            }
            FilterResults filterResults = new FilterResults();
            filterResults.count = newData.size();
            filterResults.values = newData;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            data = (ArrayList<FansOfFollowBean>) filterResults.values;
            if (filterResults.count > 0) {
                notifyDataSetChanged();
            } else {
                //沒符合的選項
                data=new ArrayList<FansOfFollowBean>();
                notifyDataSetChanged();
            }
        }
    }
}
