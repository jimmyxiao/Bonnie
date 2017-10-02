package com.sctw.bonniedraw.works;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

import java.util.List;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class WorkAdapterGrid extends RecyclerView.Adapter<WorkAdapterGrid.ViewHolder> {
    List<String> mData;
    WorkGridOnClickListener listner;

    public WorkAdapterGrid(List<String> mData, WorkGridOnClickListener listner) {
        this.mData = mData;
        this.listner = listner;
    }

    @Override
    public WorkAdapterGrid.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photocard_layout, parent, false);
        WorkAdapterGrid.ViewHolder vh = new WorkAdapterGrid.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final WorkAdapterGrid.ViewHolder holder, int position) {
        holder.mTextView.setText(mData.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listner.onWorkClick(holder.getAdapterPosition());
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.card_textview);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
