package com.sctw.bonniedraw.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment implements TextWatcher {
    private TextView mTextViewEmail, mTextViewSignup;
    private Button mBtnGetPassword;
    private FragmentManager fragmentManager;
    private boolean mbCheckEmail = false;
    private TextInputLayout mTextInputLayoutEmail;

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
        mTextViewEmail = (TextView) view.findViewById(R.id.editText_forget_email);
        mTextViewSignup = (TextView) view.findViewById(R.id.textView_singup_login);
        mBtnGetPassword = (Button) view.findViewById(R.id.btn_get_password);
        mTextInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.textInputLayout_forget_email);
        mTextViewSignup.setOnClickListener(signIn);
        mBtnGetPassword.setOnClickListener(getPwd);
        mTextViewEmail.addTextChangedListener(this);
    }

    private View.OnClickListener getPwd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mbCheckEmail) {
                updateProfileInfo();
            } else {
                checkEmail();
            }
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

    void updateProfileInfo() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = ConnectJson.forgetPwd(mTextViewEmail.getText().toString(), "MASKTEST123456");
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        final Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_FORGET_PWD)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setMessage("連線失敗");
                        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONObject responseJSON = new JSONObject(responseStr);
                                if (responseJSON.getInt("res") == 1) {
                                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Fail", Toast.LENGTH_SHORT).show();
                                }
                                Log.d("GET RESPONE", responseJSON.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        checkEmail();
    }

    @Override
    public void afterTextChanged(Editable s) {
        checkEmail();
    }

    private void checkEmail() {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mTextViewEmail.getText().toString()).matches()) {
            mTextInputLayoutEmail.setError("請輸入正確的E-mail");
            mbCheckEmail = false;
        } else {
            mTextInputLayoutEmail.setError(null);
            mbCheckEmail = true;
        }
    }
}
