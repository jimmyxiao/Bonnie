package com.sctw.bonniedraw.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForgetPasswordFragment extends Fragment implements TextWatcher, View.OnClickListener {
    private static final String SITE_KEY = "6LfZIDkUAAAAABgHI0HkKRCzaqdqCMTabI5vXWgs";
    private static final String ST_KEY = "6LfZIDkUAAAAACK0iX4foZSovZG5ZpStkGSxIKdc";
    private TextView mTextViewEmail, mTextViewSignup;
    private Button mBtnGetPassword;
    private FragmentManager fragmentManager;
    private boolean mbCheckEmail = false;
    private TextInputLayout mTextInputLayoutEmail;
    private boolean mbFirstCheck=true;

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
        mTextViewSignup.setOnClickListener(this);
        mBtnGetPassword.setOnClickListener(this);
        mTextViewEmail.addTextChangedListener(this);
    }

    void getPassword() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.forgetPwd(mTextViewEmail.getText().toString());
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showErrorDialog(getString(R.string.login_fail_server));
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
                                    showErrorDialog(getString(R.string.go_to_email_get_password));
                                } else {
                                    showErrorDialog(getString(R.string.not_found_account));
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

    private void showErrorDialog(String msg){
        final FullScreenDialog dialog=new FullScreenDialog(getContext(),R.layout.dialog_base);
        FrameLayout layout= dialog.findViewById(R.id.frameLayout_dialog_base);
        Button btnOk=dialog.findViewById(R.id.btn_paint_dialog_base_yes);
        TextView tvTitle=dialog.findViewById(R.id.textView_dialog_base_title);
        TextView tvMsg=dialog.findViewById(R.id.textView_dialog_base_msg);
        tvTitle.setText(R.string.fail);
        tvMsg.setText(msg);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
            mTextInputLayoutEmail.setError(getString(R.string.need_correct_email));
            mbCheckEmail = false;
        } else {
            mTextInputLayoutEmail.setError(null);
            mbCheckEmail = true;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_password:
                if(mbCheckEmail){
                    if(mbFirstCheck){
                        mbFirstCheck=false;
                        getPassword();
                    }else {
                        recaptcha(v);
                    }
                }
                break;
            case R.id.textView_singup_login:
                FragmentTransaction ft = fragmentManager.beginTransaction();
                ft.replace(R.id.frameLayout_login, new SignUpFragment());
                ft.addToBackStack(null);
                ft.commit();
                break;
        }
    }

    private void recaptcha(View view) {
        SafetyNet.getClient(this.getActivity()).verifyWithRecaptcha(SITE_KEY)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        // Indicates communication with reCAPTCHA service was
                        // successful.
                        String userResponseToken = response.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                                getPassword();
                            // Validate the user response token using the
                            // reCAPTCHA siteverify API.
                        }
                    }
                }).addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ApiException) {
                    // An error occurred when communicating with the
                    // reCAPTCHA service. Refer to the status code to
                    // handle the error appropriately.
                    ApiException apiException = (ApiException) e;
                    int statusCode = apiException.getStatusCode();
                    Log.d("recaptcha", "Error: " + CommonStatusCodes
                            .getStatusCodeString(statusCode));
                } else {
                    // A different, unknown type of error occurred.
                    Log.d("recaptcha", "Error: " + e.getMessage());
                }
            }
        });
    }
}
