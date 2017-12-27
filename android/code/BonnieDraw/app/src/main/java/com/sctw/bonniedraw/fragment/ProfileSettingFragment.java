package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSettingFragment extends DialogFragment {
    ImageButton mImgBtnBack;
    Button mImgBtnEdit, mImgBtnUpdatePwd, mImgDescription, mImgBtnPrivacyPolicy, mImgBtnTermsOfUse;
    SharedPreferences prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);

        mImgBtnBack = view.findViewById(R.id.imgBtn_profile_back);
        mImgBtnEdit = view.findViewById(R.id.btn_profile_edit);
        mImgBtnUpdatePwd = view.findViewById(R.id.btn_profile_update_pwd);
        mImgDescription = view.findViewById(R.id.btn_profile_description);
        mImgBtnPrivacyPolicy = view.findViewById(R.id.btn_profile_privacy_policy);
        mImgBtnTermsOfUse = view.findViewById(R.id.btn_profile_terms_of_use);

        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileSettingFragment.this.dismiss();
            }
        });

        mImgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditProfileFragment fragment = new EditProfileFragment();
                fragment.show(getChildFragmentManager(), "TAG");
            }
        });

        mImgBtnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicFragment fragment = new PublicFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                fragment.setArguments(bundle);
                fragment.show(getChildFragmentManager(), "TAG");
            }
        });

        mImgBtnTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PublicFragment fragment = new PublicFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                fragment.setArguments(bundle);
                fragment.show(getChildFragmentManager(), "TAG");
            }
        });

        if (prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0) != 1) {
            mImgBtnUpdatePwd.setVisibility(View.GONE);
        } else {
            mImgBtnUpdatePwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UpdatePasswordFragment fragment = new UpdatePasswordFragment();
                    fragment.show(getChildFragmentManager(), "TAG");
                }
            });
        }
    }
}
