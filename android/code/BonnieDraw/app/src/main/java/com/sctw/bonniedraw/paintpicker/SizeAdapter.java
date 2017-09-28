package com.sctw.bonniedraw.paintpicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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
        holder.imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        listener.onSizeSelected(sizes[holder.getAdapterPosition()]);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.view_size_view);
        }
    }
}
