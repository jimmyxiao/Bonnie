package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;

import java.io.IOException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private ImageView profilePhoto;
    private TextView profileName;
    SharedPreferences prefs;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        profilePhoto=(ImageView)view.findViewById(R.id.profile_photo);
        profileName=(TextView)view.findViewById(R.id.profile_name);

        String userName=prefs.getString(GlobalVariable.userNameStr,"Null");
        String userEmail=prefs.getString(GlobalVariable.userEmailStr,"Null");
        URL profilePicUrl = null;
        try {
            profilePicUrl = new URL(prefs.getString("userImgUrl", "FailLoad"));
            Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
            profilePhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        profileName.setText("姓名="+userName+" 信箱="+userEmail);
    }
}
