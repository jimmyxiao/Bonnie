package com.sctw.bonniedraw.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
public class ProfileSettingFragment extends Fragment {
    ImageButton mImgBtnBack;
    Button mImgBtnEdit, mImgBtnUpdatePwd, mImgBtnLinkAccount, mImgDescription, mImgBtnPrivacyPolicy, mImgBtnTermsOfUse;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    SharedPreferences prefs;

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
        mImgBtnLinkAccount = view.findViewById(R.id.btn_profile_link_account);
        mImgDescription = view.findViewById(R.id.btn_profile_description);
        mImgBtnPrivacyPolicy = view.findViewById(R.id.btn_profile_privacy_policy);
        mImgBtnTermsOfUse = view.findViewById(R.id.btn_profile_terms_of_use);

        fragmentManager = getFragmentManager();

        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        mImgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                EditProfileFragment fragment = new EditProfileFragment();
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mImgBtnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                PublicFragment fragment = new PublicFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 1);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        mImgBtnTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentTransaction = fragmentManager.beginTransaction();
                PublicFragment fragment = new PublicFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        if (!prefs.getString(GlobalVariable.USER_THIRD_PLATFORM_STR, "null").equals("1")) {
            mImgBtnUpdatePwd.setVisibility(View.GONE);
        } else {
            mImgBtnUpdatePwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentTransaction = fragmentManager.beginTransaction();
                    UpdatePasswordFragment fragment = new UpdatePasswordFragment();
                    fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
    }
}
