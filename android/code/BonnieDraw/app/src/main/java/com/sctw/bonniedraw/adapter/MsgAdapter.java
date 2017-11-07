package com.sctw.bonniedraw.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.Msg;

import java.util.ArrayList;

/**
 * Created by Fatorin on 2017/10/2.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {
    ArrayList<Msg> data;

    public MsgAdapter(ArrayList<Msg> data) {
        this.data=data;
    }

    @Override
    public MsgAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        MsgAdapter.ViewHolder vh = new MsgAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MsgAdapter.ViewHolder holder, int position) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View v) {
            super(v);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
