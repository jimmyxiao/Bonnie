package com.sctw.bonniedraw.colorpick;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sctw.bonniedraw.R;

import java.util.ArrayList;

/**
 * Created by Fatorin on 2017/11/2.
 */

public class ColorTicket extends RecyclerView.Adapter<ColorTicket.ViewHolder> {
    private ArrayList<ColorBean> colors;
    private int mSelectedPos = -1;
    private OnItemListener listener;

    public ColorTicket(ArrayList<ColorBean> colors, OnItemListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @Override
    public ColorTicket.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_view, parent, false);
        return new ColorTicket.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ColorTicket.ViewHolder holder, int position) {
        GradientDrawable bgShape = (GradientDrawable) holder.view.getBackground();
        bgShape.setColor(colors.get(position).getColor());
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).isSelect()) {
                mSelectedPos = i;
            }
        }

        if (colors.get(position).isSelect()) {
            holder.viewColorPress.setVisibility(View.VISIBLE);
        } else {
            holder.viewColorPress.setVisibility(View.INVISIBLE);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedPos != holder.getAdapterPosition()) {
                    //先取消上個item的勾選狀態
                    if (mSelectedPos != -1) {
                        colors.get(mSelectedPos).setSelect(false);
                        notifyItemChanged(mSelectedPos);
                    }
                    //設定新Item的勾選狀態
                    mSelectedPos = holder.getAdapterPosition();
                    colors.get(mSelectedPos).setSelect(true);
                    notifyItemChanged(mSelectedPos);
                    listener.onItemClick(colors.get(mSelectedPos).getColor());
                }
                //(holder.getAdapterPosition(),colors.get(holder.getAdapterPosition()).getColor());
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView view;
        ImageView viewColorPress;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.viewColor);
            viewColorPress = itemView.findViewById(R.id.viewColorPress);
        }
    }

    public void set_mSelectedPos(int pos) {
        if (mSelectedPos != -1) {
            colors.get(mSelectedPos).setSelect(false);
        }
        mSelectedPos = pos;
    }

    public int get_mSelectedPos() {
        return this.mSelectedPos;
    }

    public void addNewColor(ColorBean bean) {
        colors.add(bean);
    }

    public void removeColor() {
        colors.remove((mSelectedPos));
        notifyItemRemoved(mSelectedPos);
        notifyDataSetChanged();
        mSelectedPos = -1;
    }

    public interface OnItemListener {
        void onItemClick(int color);
    }
}
