package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicFragment extends Fragment {
    TextView PublicTitle;
    ImageButton btnPublicBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PublicTitle=view.findViewById(R.id.public_fragment_title);
        btnPublicBack=view.findViewById(R.id.btn_public_back);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int item = bundle.getInt("type");
            String title="";
            switch (item){
                case 1:
                    title="隱私政策";
                    break;
                case 2:
                    title="使用條款";
                    break;
            }
            PublicTitle.setText(title);
        }

        btnPublicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
