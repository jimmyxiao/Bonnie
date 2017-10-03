package com.sctw.bonniedraw.works;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

import java.util.List;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterList extends RecyclerView.Adapter<WorkAdapterList.ViewHolder> {
    List<String> data;
    WorkListOnClickListener listener;

    public WorkAdapterList(List<String> data, WorkListOnClickListener listener) {
        this.data = data;
        this.listener = listener;
    }

    @Override
    public WorkAdapterList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.work_card, parent, false);
        WorkAdapterList.ViewHolder vh = new WorkAdapterList.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final WorkAdapterList.ViewHolder holder, int position) {
        holder.mTextView.setText(data.get(position));
        holder.wroksImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkImgClick(holder.getAdapterPosition());
            }
        });
        holder.worksUserExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkExtraClick(holder.getAdapterPosition());
            }
        });
        holder.worksUserGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkGoodClick(holder.getAdapterPosition());
            }
        });
        holder.worksUserMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkMsgClick(holder.getAdapterPosition());
            }
        });
        holder.worksUserShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkShareClick(holder.getAdapterPosition());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        ImageView wroksImgView;
        ImageButton worksUserExtra, worksUserGood, worksUserMsg, worksUserShare;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.works_user_name);
            wroksImgView = (ImageView) v.findViewById(R.id.works_user_image);
            worksUserExtra = (ImageButton) v.findViewById(R.id.works_user_extra);
            worksUserGood = (ImageButton) v.findViewById(R.id.works_user_good);
            worksUserMsg = (ImageButton) v.findViewById(R.id.works_user_msg);
            worksUserShare = (ImageButton) v.findViewById(R.id.works_user_share);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}