package com.sctw.bonniedraw.colorpick;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.sctw.bonniedraw.R;
import java.util.ArrayList;

public class ColorTicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ColorBean> mColorsList;
    private int mSelectedPos = -1;
    private OnItemListener listener;

    private int itemWidth = 0;
    private int itemCount = 0;
    private int spanColumn = 8;  //每排的數量
    private int totalPage = 0;
    private int spanRow = 2;
    private int pageMargin = 0;

    public int getColorsSize(){
        if(mColorsList!=null)
            return mColorsList.size();

        return 0;
    }

    public ColorTicketAdapter(ArrayList<ColorBean> colors, OnItemListener listener) {
        this.mColorsList = colors;
        this.listener = listener;
        itemCount = mColorsList.size() + spanRow * spanColumn;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_view, parent, false);
        if (itemWidth <= 0) {
            itemWidth = (parent.getWidth() - pageMargin * 2) / spanColumn;
        }

        ColorTicketAdapter.ColorViewHolder colorHolder = new ColorTicketAdapter.ColorViewHolder(view);
        colorHolder.itemView.measure(0, 0);
        colorHolder.itemView.getLayoutParams().width = itemWidth;
        colorHolder.itemView.getLayoutParams().height = colorHolder.itemView.getMeasuredHeight();
        return colorHolder;
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        ColorTicketAdapter.ColorViewHolder colorHolder = (ColorTicketAdapter.ColorViewHolder) holder;
        GradientDrawable bgShape = (GradientDrawable) colorHolder.viewColor.getBackground();
        bgShape.setColor(mColorsList.get(position).getColor());

        //找出被選到值設到 mSelectedPos
        for (int i = 0; i < mColorsList.size(); i++) {
            if (mColorsList.get(i).isSelect()) {
                mSelectedPos = i;
            }
        }

        //選到設打勾
        if (mColorsList.get(position).isSelect()) {
            colorHolder.viewColorPress.setVisibility(View.VISIBLE);
        } else {
            colorHolder.viewColorPress.setVisibility(View.INVISIBLE);
        }

        //設定 onClick
        colorHolder.viewColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectedPos != holder.getAdapterPosition()) {
                    //先取消上個item的勾選狀態
                    if (mSelectedPos != -1) {
                        mColorsList.get(mSelectedPos).setSelect(false);
                        notifyItemChanged(mSelectedPos);
                    }
                    //設定新Item的勾選狀態
                    mSelectedPos = holder.getAdapterPosition();
                    mColorsList.get(mSelectedPos).setSelect(true);
                    notifyDataSetChanged();
                    listener.onItemClick(mColorsList.get(mSelectedPos).getColor());
                }
            }
        });

        //每個item 的width 設故定
        holder.itemView.getLayoutParams().width = 150 ;


        //做分頁計算用,暫先不處理
/*
        if (spanColumn == 1) {
            // 每個Item距離左右兩側各pageMargin
            holder.itemView.getLayoutParams().width = itemWidth + pageMargin * 2;
            holder.itemView.setPadding(pageMargin, 0, pageMargin, 0);
        } else {
            int m = position % (spanRow * spanColumn);
            if (m < spanRow) {
                // 每頁左側的Item距離左邊pageMargin
                holder.itemView.getLayoutParams().width = itemWidth + pageMargin;
                holder.itemView.setPadding(pageMargin, 0, 0, 0);
            } else if (m >= spanRow * spanColumn - spanRow) {
                // 每頁右側的Item距離右邊pageMargin
                holder.itemView.getLayoutParams().width = itemWidth + pageMargin;
                holder.itemView.setPadding(0, 0, pageMargin, 0);
            } else {
                //  中間的正常顯示
                holder.itemView.getLayoutParams().width = itemWidth;
                holder.itemView.setPadding(0, 0, 0, 0);
            }
        }


        holder.itemView.setTag(position);
        */
        /*
        if (position < mColorsList.size()) {
            holder.itemView.setVisibility(View.VISIBLE);
            mCallBack.onBindViewHolder(holder, position);
        } else {
            holder.itemView.setVisibility(View.INVISIBLE);
        }
        */
    }

    @Override
    public int getItemCount() {

        return mColorsList.size();

    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {
        ImageView viewColor;
        ImageView viewColorPress;

        public ColorViewHolder(View itemView) {
            super(itemView);
            viewColor = itemView.findViewById(R.id.viewColor);
            viewColorPress = itemView.findViewById(R.id.viewColorPress);
        }
    }

    public void set_mSelectedPos(int pos) {
        if (mSelectedPos != -1) {
            mColorsList.get(mSelectedPos).setSelect(false);
        }
        mSelectedPos = pos;
    }

    public int get_mSelectedPos() {
        return this.mSelectedPos;
    }

    public void addNewColor(ColorBean bean) {
        mColorsList.add(bean);
    }

    public void removeColor() {
        mColorsList.remove((mSelectedPos));
        notifyItemRemoved(mSelectedPos);
        notifyDataSetChanged();
        mSelectedPos = -1;
    }

    public void removeAllTrace() {
        for (int x = 0; x < getItemCount(); x++) {
            mColorsList.get(x).setSelect(false);
        }
    }

    public interface OnItemListener {
        void onItemClick(int color);
    }


}

