package com.sctw.bonniedraw.paintpicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sctw.bonniedraw.R;


/**
 * Created by Gunaseelan on 18-12-2016.
 */

public class SizeAdapter extends RecyclerView.Adapter<SizeAdapter.ViewHolder> {
    int[] sizes;
    SizeSelectedListener listener;

    public SizeAdapter(int[] sizes, SizeSelectedListener listener) {
        this.sizes = sizes;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_size_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imageView.setMaxHeight(sizes[position]);
        holder.imageView.setMaxWidth(sizes[position]);
        holder.mTextView.setText(sizes[position]+" 像素");
        holder.ln.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSizeSelected(sizes[holder.getAdapterPosition()]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout ln;
        TextView mTextView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView=itemView.findViewById(R.id.viewsize_text);
            imageView = itemView.findViewById(R.id.view_size_view);
            ln=itemView.findViewById(R.id.size_pick_layout);
        }
    }
}
