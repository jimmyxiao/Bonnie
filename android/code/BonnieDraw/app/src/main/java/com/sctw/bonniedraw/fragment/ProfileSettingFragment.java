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
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileSettingFragment extends Fragment {
    ImageButton btnProfileBack;
    LinearLayout btnProfileEdit,btnProfileUpdatePwd,btnProfileLinkAccount,btnProfileDescription,btnProfilePrivacyPolicy,btnProfileTermsOfUse;
    FragmentManager fm;
    FragmentTransaction ft;
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
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);

        btnProfileBack=view.findViewById(R.id.btn_profile_back);
        btnProfileEdit=view.findViewById(R.id.btn_profile_edit);
        btnProfileUpdatePwd=view.findViewById(R.id.btn_profile_update_pwd);
        btnProfileLinkAccount=view.findViewById(R.id.btn_profile_link_account);
        btnProfileDescription=view.findViewById(R.id.btn_profile_description);
        btnProfilePrivacyPolicy=view.findViewById(R.id.btn_profile_privacy_policy);
        btnProfileTermsOfUse=view.findViewById(R.id.btn_profile_terms_of_use);

        fm = getFragmentManager();

        btnProfileBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        btnProfileEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = fm.beginTransaction();
                EditProfileFragment fragment=new EditProfileFragment();
                ft.replace(R.id.main_actitivy_layout, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        btnProfilePrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = fm.beginTransaction();
                PublicFragment fragment=new PublicFragment();
                Bundle bundle=new Bundle();
                bundle.putInt("type",1);
                fragment.setArguments(bundle);
                ft.replace(R.id.main_actitivy_layout, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        btnProfileTermsOfUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ft = fm.beginTransaction();
                PublicFragment fragment=new PublicFragment();
                Bundle bundle=new Bundle();
                bundle.putInt("type",2);
                fragment.setArguments(bundle);
                ft.replace(R.id.main_actitivy_layout, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        if(!prefs.getString(GlobalVariable.userPlatformStr,"null").equals("1")){
            btnProfileUpdatePwd.setVisibility(View.GONE);
        }else {
            btnProfileUpdatePwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ft = fm.beginTransaction();
                    UpdatePasswordFragment fragment=new UpdatePasswordFragment();
                    ft.replace(R.id.main_actitivy_layout, fragment);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            });
        }
    }
}
