package com.sctw.bonniedraw.paint;

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
 * Created by Fatorin on 2017/8/30.
 */

public class PaintControlAdapter extends RecyclerView.Adapter<PaintControlAdapter.PaintViewHolder>{
    private static final int TYPE_HEAD=0;
    private static final int TYPE_LIST=1;

    List<PaintControlItem> items;
    //設定預設建構子
    public PaintControlAdapter(List<PaintControlItem> items) {
        this.items=items;
    }

    //設定相對應的LAYOUT
    @Override
    public PaintControlAdapter.PaintViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        PaintViewHolder mPaintViewHolder;
        if(viewType==TYPE_LIST){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
            mPaintViewHolder = new PaintViewHolder(view,viewType);
            return mPaintViewHolder;
        }
        else if(viewType==TYPE_HEAD){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            mPaintViewHolder = new PaintViewHolder(view,viewType);
            return mPaintViewHolder;
        }
        return null;
    }

    //對每個物件進行綁定
    @Override
    public void onBindViewHolder(PaintViewHolder holder, int position) {
        PaintControlItem item;
        if(holder.view_type==TYPE_LIST){
            item=items.get(position-1);
            holder.paint_control_text.setText(item.getName());
            holder.imgId.setImageResource(item.getImgId());
        }
        else if(holder.view_type==TYPE_HEAD){
            holder.paint_control_header_text.setText("Tools");
        }

    }

    //取得項目數量(GET LIST SIZE)
    @Override
    public int getItemCount() {
        return items.size()+1;
    }

    //建立視窗
    public class PaintViewHolder extends RecyclerView.ViewHolder {
        int view_type;
        //欄位項
        TextView paint_control_text;
        ImageView imgId;
        //標題項
        TextView paint_control_header_text;
        ImageButton paint_back_btn;

        public PaintViewHolder(View itemView,int viewType) {
            super(itemView);
            if(viewType==TYPE_LIST){
                paint_control_text = (TextView)itemView.findViewById(R.id.paint_control_text);
                imgId = (ImageView)itemView.findViewById(R.id.paint_imgId);
                view_type=1;
            }
            else if(viewType==TYPE_HEAD){
                paint_control_header_text=(TextView) itemView.findViewById(R.id.paint_control_header_text);
                paint_back_btn=(ImageButton) itemView.findViewById(R.id.paint_back_btn);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0) return TYPE_HEAD;
        return TYPE_LIST;
    }
}
