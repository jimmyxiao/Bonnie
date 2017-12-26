package com.sctw.bonniedraw.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.bean.SidebarBean;

import java.util.ArrayList;

/**
 * Created by Fatorin on 2017/11/23.
 */

public class SideBarAdapter extends RecyclerView.Adapter<SideBarAdapter.ViewHolder> {
    private Context context;
    private ArrayList<SidebarBean> data;
    private SideBarClickListener listener;

    public interface SideBarClickListener {
        void onClick(int position);
    }

    public SideBarAdapter(Context context, ArrayList<SidebarBean> data, SideBarClickListener listener) {
        this.context = context;
        this.data = data;
        this.listener = listener;
    }

    @Override
    public SideBarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sidebar, parent, false);
        SideBarAdapter.ViewHolder vh = new SideBarAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final SideBarAdapter.ViewHolder holder, int position) {
        holder.mTv.setText(data.get(holder.getAdapterPosition()).getTitle());
        if (data.get(holder.getAdapterPosition()).getTitle().isEmpty()) {
            holder.mLl.setVisibility(View.GONE);
        }
        holder.mIv.setImageDrawable(ContextCompat.getDrawable(context, data.get(holder.getAdapterPosition()).getDrawableId()));
        if (position % 3 == 2 && position != getItemCount() - 1) {
            holder.viewDiv.setVisibility(View.VISIBLE);
        }
        holder.mLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(holder.getAdapterPosition());
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLl;
        ImageView mIv;
        TextView mTv;
        View viewDiv;

        ViewHolder(View v) {
            super(v);
            mLl = v.findViewById(R.id.ll_item_siderbar);
            mIv = v.findViewById(R.id.imgView_item_siderbar);
            mTv = v.findViewById(R.id.textView_item_sidebar);
            viewDiv = v.findViewById(R.id.divier_sidrbar);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void chagneTitle(int position, String str) {
        data.get(position).setTitle(str);
        notifyItemChanged(position);
    }

    public String getTagName(int position) {
        return data.get(position).getTitle();
    }
}
