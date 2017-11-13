package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sctw.bonniedraw.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFollowFragment extends Fragment {


    public BaseFollowFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base_follow, container, false);
    }

}
