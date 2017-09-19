package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    RecyclerView homeRecyclerView;
    private Toolbar toolbar;
    private ImageButton toolbarSearch;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbarSearch = (ImageButton) view.findViewById(R.id.toolbar_search);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        homeRecyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerview);
        ArrayList<String> myDataset = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            myDataset.add(Integer.toString(i));
        }
        HomeAdapter mAdapter = new HomeAdapter(myDataset);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homeRecyclerView.setLayoutManager(layoutManager);
        homeRecyclerView.setAdapter(mAdapter);

        toolbarSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "搜尋", Toast.LENGTH_SHORT).show();
            }
        });
    }

     public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
        List<String> data;

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;

            ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.home_number_text);
            }
        }

        public HomeAdapter(List<String> data) {
            this.data = data;
        }

        @Override
        public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card, parent, false);
            HomeAdapter.ViewHolder vh = new HomeAdapter.ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(final HomeAdapter.ViewHolder holder, int position) {
            holder.mTextView.setText(data.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Item " + holder.getAdapterPosition() + " is clicked.", Toast.LENGTH_SHORT).show();
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Toast.makeText(getActivity(), "Item " + holder.getAdapterPosition() + " is long clicked.", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }
}
