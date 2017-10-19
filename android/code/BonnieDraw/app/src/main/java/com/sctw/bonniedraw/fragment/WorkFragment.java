package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkFragment extends Fragment {
    TextView mTextViewName,worksUserGoodTotal,worksCreateTime,worksUserClass;
    ImageView worksUserPhoto,worksUserImage;
    ImageButton worksUserExtra,worksUserGood,worksUserMsg,worksUserShare,worksUserFollow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_work, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextViewName =view.findViewById(R.id.textView_works_user_name);
        worksUserGoodTotal=view.findViewById(R.id.textView_works_good_total);
        worksCreateTime=view.findViewById(R.id.textView_works_create_time);
        worksUserClass=view.findViewById(R.id.textView_works_user_class);
        worksUserPhoto=view.findViewById(R.id.imgView_works_user_photo);
        worksUserImage=view.findViewById(R.id.imgView_works_work_img);
        worksUserExtra=view.findViewById(R.id.imgBtn_works_extra);
        worksUserGood=view.findViewById(R.id.imgBtn_works_good);
        worksUserMsg=view.findViewById(R.id.imgBtn_works_msg);
        worksUserShare=view.findViewById(R.id.imgBtn_works_share);
        worksUserFollow=view.findViewById(R.id.imgBtn_works_follow);
    }
}
