package com.sctw.bonniedraw.fragment;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.LoginActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdatePasswordFragment extends Fragment {
    Button updatePasswordCancel,updatePasswordDone;
    SharedPreferences prefs;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        updatePasswordCancel=view.findViewById(R.id.update_password_cancel);
        updatePasswordDone=view.findViewById(R.id.update_password_done);

        updatePasswordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        updatePasswordDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "更新密碼成功", Toast.LENGTH_SHORT).show();
                cleanValue();
            }
        });

    }

    public void cleanValue() {
        prefs.edit().clear().apply();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
