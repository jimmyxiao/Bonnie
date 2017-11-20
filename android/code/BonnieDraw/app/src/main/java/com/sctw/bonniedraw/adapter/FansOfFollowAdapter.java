package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.FansOfFollowBean;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fatorin on 2017/11/13.
 */

public class FansOfFollowAdapter extends RecyclerView.Adapter<FansOfFollowAdapter.ViewHolder> {
    private Context context;
    private ArrayList<FansOfFollowBean> data;

    public FansOfFollowAdapter(Context context, ArrayList<FansOfFollowBean> data) {
        this.context = context;
        this.data = data;
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

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView mCircleImg;


        ViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
