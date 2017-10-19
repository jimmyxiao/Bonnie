package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment {
    private TextView mTextViewEmail,mTextViewSignup;
    private Button mBtnGetPassword;
    private FragmentManager fragmentManager;

    public ForgetPasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forget_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        mTextViewEmail =(TextView) view.findViewById(R.id.editText_signup_email);
        mTextViewSignup = (TextView) view.findViewById(R.id.textView_singup_login);
        mTextViewSignup.setOnClickListener(signIn);
    }

    private View.OnClickListener getPwd=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("GET PASSWORD EMAL", mTextViewEmail.getText().toString());
        }
    };

    private View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.frameLayout_login, new SignUpFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
    };
}
