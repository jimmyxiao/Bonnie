package com.sctw.bonniedraw.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.paint.PaintActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaintFragment extends Fragment {

    private Button createPaint;
    private Button replaySetting;
    FragmentManager fragmentManager;

    public PaintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paint, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        createPaint= (Button) view.findViewById(R.id.create_new_paint);

        createPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent();
                it.setClass(getActivity(), PaintActivity.class);
                startActivity(it);
            }
        });
    }
}
