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
    TextView worksUserName,worksUserGoodTotal,worksCreateTime,worksUserClass;
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
        worksUserName=view.findViewById(R.id.works_user_name);
        worksUserGoodTotal=view.findViewById(R.id.works_user_good_total);
        worksCreateTime=view.findViewById(R.id.works_create_time);
        worksUserClass=view.findViewById(R.id.works_user_class);
        worksUserPhoto=view.findViewById(R.id.works_user_photo);
        worksUserImage=view.findViewById(R.id.works_user_image);
        worksUserExtra=view.findViewById(R.id.works_user_extra);
        worksUserGood=view.findViewById(R.id.works_user_good);
        worksUserMsg=view.findViewById(R.id.works_user_msg);
        worksUserShare=view.findViewById(R.id.works_user_share);
        worksUserFollow=view.findViewById(R.id.works_user_follow);
    }
}
