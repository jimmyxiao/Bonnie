package com.sctw.bonniedraw.works;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.WorkInfo;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterList extends RecyclerView.Adapter<WorkAdapterList.ViewHolder> {
    List<WorkInfo> data=new ArrayList<>();
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
        holder.mTvWorkGoodTotal.setText(String.format(holder.mTvWorkGoodTotal.getContext().getString(R.string.work_good_total), data.get(position).getIsFollowing()));
        if (!data.get(position).getImagePath().equals("null")) {
            try {
                URL url = new URL(GlobalVariable.API_LINK_GET_PHOTO + data.get(position).getImagePath());
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                holder.mImgViewWrok.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        holder.mImgViewWrok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkImgClick(Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId()));
            }
        });
        holder.imgBtnWorksUserExtra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkExtraClick(Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId()));
            }
        });
        holder.imgBtnWorksUserGood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkGoodClick(Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId()));
            }
        });
        holder.imgBtnWorksUserMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkMsgClick(Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId()));
            }
        });
        holder.imgBtnWorksUserShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onWorkShareClick(Integer.parseInt(data.get(holder.getAdapterPosition()).getWorkId()));
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTvUserName, mTvWorkName, mTvWorkGoodTotal;
        ImageView mImgViewWrok;
        ImageButton imgBtnWorksUserExtra, imgBtnWorksUserGood, imgBtnWorksUserMsg, imgBtnWorksUserShare;

        ViewHolder(View v) {
            super(v);
            mTvUserName = (TextView) v.findViewById(R.id.textView_works_user_name);
            mTvWorkName = (TextView) v.findViewById(R.id.textView_works_work_name);
            mTvWorkGoodTotal = (TextView) v.findViewById(R.id.textView_works_good_total);
            mImgViewWrok = (ImageView) v.findViewById(R.id.imgView_works_work_img);
            imgBtnWorksUserExtra = (ImageButton) v.findViewById(R.id.imgBtn_works_extra);
            imgBtnWorksUserGood = (ImageButton) v.findViewById(R.id.imgBtn_works_good);
            imgBtnWorksUserMsg = (ImageButton) v.findViewById(R.id.imgBtn_works_msg);
            imgBtnWorksUserShare = (ImageButton) v.findViewById(R.id.imgBtn_works_share);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
