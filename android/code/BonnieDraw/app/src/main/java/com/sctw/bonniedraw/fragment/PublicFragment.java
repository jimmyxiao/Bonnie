package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
public class PublicFragment extends DialogFragment {
    // 1.關於BONNIEDRAW  2.隱私權條款  3.使用條款
    TextView mTextViewTitle;
    ImageButton mImgBtnBack;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_public, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextViewTitle =view.findViewById(R.id.textView_public_fragment_title);
        mImgBtnBack =view.findViewById(R.id.imgBtn_public_back);

        Bundle bundle = getArguments();
        if (bundle != null) {
            int item = bundle.getInt("type");
            String title="";
            switch (item){
                case 0:
                    title=getString(R.string.public_title_about_bonnidraw);
                case 1:
                    title=getString(R.string.u06_04_privacy_policy);
                    break;
                case 2:
                    title=getString(R.string.u06_04_terms_of_service);
                    break;
            }
            mTextViewTitle.setText(title);
        }

        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicFragment.this.dismiss();
            }
        });
    }
}
