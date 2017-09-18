package com.sctw.bonniedraw.paintpicker;

import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctw.bonniedraw.R;


/**
 * Created by Gunaseelan on 18-12-2016.
 */

public class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ViewHolder> {
    int[] colors;
    ColorsSelectedListener listener;

    public ColorsAdapter(int[] colors, ColorsSelectedListener listener) {
        this.colors = colors;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        GradientDrawable bgShape = (GradientDrawable) holder.view.getBackground();
        bgShape.setColor(colors[position]);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onColorSelected(colors[holder.getAdapterPosition()]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.viewColor);
        }
    }
}
