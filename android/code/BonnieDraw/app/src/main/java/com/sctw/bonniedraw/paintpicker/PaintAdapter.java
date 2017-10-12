package com.sctw.bonniedraw.paintpicker;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sctw.bonniedraw.R;


/**
 * Created by Gunaseelan on 18-12-2016.
 */

public class PaintAdapter extends RecyclerView.Adapter<PaintAdapter.ViewHolder> {
    Drawable[] paints;
    PaintSelectedListener listener;

    public PaintAdapter(Drawable[] paints, PaintSelectedListener listener) {
        this.paints = paints;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paint_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(paints[holder.getAdapterPosition()]);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onPaintSelect(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return paints.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.view_paint_view);
        }
    }
}
