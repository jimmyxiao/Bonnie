package com.sctw.bonniedraw.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.NoticeInfo;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/11/13.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    List<NoticeInfo> data = new ArrayList<>();

    public NoticeAdapter(List<NoticeInfo> data) {
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

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView circleImageView;
        TextView mTvName,mTvTime,mTvMsg;

        ViewHolder(View v) {
            super(v);
            circleImageView=v.findViewById(R.id.circle_notice_user_img);
            mTvName=v.findViewById(R.id.textView_notice_name);
            mTvTime=v.findViewById(R.id.textView_notice_time);
            mTvMsg=v.findViewById(R.id.textView_notice_msg);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
