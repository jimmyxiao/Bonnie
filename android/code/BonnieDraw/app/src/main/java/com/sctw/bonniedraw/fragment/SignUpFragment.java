package com.sctw.bonniedraw.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.widget.ToastUtil;

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
public class SignUpFragment extends Fragment {
    TextInputLayout mTextInputLayoutName, mTextInputLayoutEmail, mTextInputLayoutPwd, mTextInputLayoutRePwd;
    TextInputEditText userName, userEmail, userPassword, userRePassword;
    TextView mTextViewSignin;
    Button mBtnSignup;
    boolean userNameVaild, userEmailVaild, userPwdVaild, userRePwdVaild = false;
    FragmentManager fragmentManager;
    final static int CHECK_PHONE = 0;
    final static int CHECK_EMAIL = 1;
    final static int CHECK_NAME = 2;
    final static int CHECK_PWD = 3;
    final static int CHECK_REPWD = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTextInputLayoutName = (TextInputLayout) view.findViewById(R.id.inputLayout_signup_name);
        mTextInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.inputLayout_signup_email);
        mTextInputLayoutPwd = (TextInputLayout) view.findViewById(R.id.inputLayout_signup_password);
        mTextInputLayoutRePwd = (TextInputLayout) view.findViewById(R.id.inputLayout_signup_repassword);
        userName = (TextInputEditText) view.findViewById(R.id.editText_signup_repassword);
        userEmail = (TextInputEditText) view.findViewById(R.id.editText_signup_email);
        userPassword = (TextInputEditText) view.findViewById(R.id.editText_signup_password);
        userRePassword = (TextInputEditText) view.findViewById(R.id.editText_signup_repassword);
        userName.setOnFocusChangeListener(userNameOnFocus);
        userName.addTextChangedListener(userNameInvalid);
        userEmail.setOnFocusChangeListener(userEmailOnFocus);
        userEmail.addTextChangedListener(userEmailInvalid);
        userPassword.setOnFocusChangeListener(userPwdOnFocus);
        userPassword.addTextChangedListener(userPasswordInvalid);
        userRePassword.setOnFocusChangeListener(userRePwdOnFocus);
        userRePassword.addTextChangedListener(userRePasswordInvalid);
        mTextViewSignin = (TextView) view.findViewById(R.id.textView_singup_login);
        mTextViewSignin.setOnClickListener(clickListenerSignin);
        fragmentManager = getFragmentManager();

        mBtnSignup = (Button) view.findViewById(R.id.btn_get_password);
        mBtnSignup.setOnClickListener(clickListenerSignup);
        super.onViewCreated(view, savedInstanceState);
    }

    //註冊按鈕
    public View.OnClickListener clickListenerSignin = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            transLogin();
        }
    };

    public View.OnClickListener clickListenerSignup = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (userNameVaild && userEmailVaild && userPwdVaild && userRePwdVaild) {
                signupAPI(3);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                infoCheck(CHECK_PHONE);
                infoCheck(CHECK_NAME);
                infoCheck(CHECK_EMAIL);
                infoCheck(CHECK_PWD);
                infoCheck(CHECK_REPWD);

                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.public_commit),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //寄送認證信，回到主畫面。
                            }
                        });
                alertDialog.setTitle(getString(R.string.signin_check_info));
                alertDialog.setMessage(getString(R.string.signin_check_msg));
                alertDialog.show();
            }
        }
    };

    private JSONObject registerJSONFormat(int style) {
        JSONObject json = new JSONObject();
        try {
            //  2註冊 3檢查EMAIL
            switch (style) {
                case 2:
                    json.put("uc", userEmail.getText().toString());
                    json.put("up", userPassword.getText().toString());
                    json.put("un", userName.getText().toString());
                    json.put("ut", GlobalVariable.EMAIL_LOGIN);
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_REGISTER);
                    Log.d("JSON DATA", json.toString());
                    break;
                case 3:
                    json.put("uc", userEmail.getText().toString());
                    json.put("ut", GlobalVariable.EMAIL_LOGIN);
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_CHECK_EMAIL);
                    Log.d("JSON DATA", json.toString());
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    private void signupAPI(final int style) {
        mBtnSignup.setEnabled(false);
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = registerJSONFormat(style);
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LOGIN)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.createToastWindow(getContext(),getString(R.string.signin_fail_title));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseStr);
                            if (responseJSON.getInt("res") == 1) {
                                Log.d("RESTFUL API : ", responseJSON.toString());
                                if (style == 3) signupAPI(2);
                                if (style == 2) createLogSignup(1);
                            } else {
                                if (style == 3) createLogSignup(2);
                                if (style == 2) createLogSignup(3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void createLogSignup(int format) {
        String title = "";
        String message = "";
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        switch (format) {
            case 1:
                title = getString(R.string.signin_successful_title);
                message = getString(R.string.sigin_successful_msg);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnSignup.setEnabled(true);
                                dialog.dismiss();
                                transLogin();
                                //寄送認證信，回到主畫面。
                            }
                        });
                break;
            case 2:
                title = getString(R.string.signin_fail_title);
                message = getString(R.string.signin_fail_email_used);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnSignup.setEnabled(true);
                                dialog.dismiss();
                                //寄送認證信，回到主畫面。
                            }
                        });
                break;
            case 3:
                title = getString(R.string.signin_fail_title);
                message = getString(R.string.signin_fail_date_error);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mBtnSignup.setEnabled(true);
                                dialog.dismiss();
                                //寄送認證信，回到主畫面。
                            }
                        });
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void transLogin() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.frameLayout_login, new LoginFragment());
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        ft.commit();
    }

    //初始點選
    public View.OnFocusChangeListener userNameOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userName.getText().toString().trim().isEmpty()) {
                mTextInputLayoutName.setError(getString(R.string.signin_check_name));
            }
        }
    };

    public View.OnFocusChangeListener userEmailOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userEmail.getText().toString().equals("")) {
                mTextInputLayoutEmail.setError(getString(R.string.signin_check_email));
            }
        }
    };

    public View.OnFocusChangeListener userPwdOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userPassword.getText().toString().equals("")) {
                mTextInputLayoutPwd.setError(getString(R.string.login_need_password));
            }
        }
    };

    public View.OnFocusChangeListener userRePwdOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userRePassword.getText().toString().equals("")) {
                mTextInputLayoutRePwd.setError(getString(R.string.signin_recheck_password));
            }
        }
    };

    public TextWatcher userNameInvalid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            infoCheck(CHECK_NAME);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };


    public TextWatcher userEmailInvalid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            infoCheck(CHECK_EMAIL);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public TextWatcher userPasswordInvalid = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            infoCheck(CHECK_PWD);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            infoCheck(CHECK_REPWD);
        }
    };

    public TextWatcher userRePasswordInvalid = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            infoCheck(CHECK_REPWD);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //檢查輸入資料
    public void infoCheck(int checkNum) {
        switch (checkNum) {
            case CHECK_NAME:
                if (userName.getText().toString().equals("")) {
                    mTextInputLayoutName.setError(getString(R.string.signin_check_name));
                    userNameVaild = false;
                } else {
                    mTextInputLayoutName.setError(null);
                    userNameVaild = true;
                }
                break;
            case CHECK_EMAIL:
                if (userEmail.getText().toString().trim().isEmpty()) {
                    mTextInputLayoutEmail.setError(getString(R.string.signin_check_email));
                    userEmailVaild = false;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.getText().toString()).matches()) {
                    mTextInputLayoutEmail.setError(getString(R.string.signin_need_correct_email));
                    userEmailVaild = false;
                } else {
                    mTextInputLayoutEmail.setError(null);
                    userEmailVaild = true;
                }
                break;
            case CHECK_PWD:
                if (userPassword.getText().toString().equals("")) {
                    mTextInputLayoutPwd.setError(getString(R.string.login_need_password));
                    userPwdVaild = false;
                } else {
                    if (userPassword.getText().toString().length() < 6) {
                        mTextInputLayoutPwd.setError(getString(R.string.signin_need_correct_password));
                        userPwdVaild = false;
                    } else {
                        mTextInputLayoutPwd.setError(null);
                        userPwdVaild = true;
                    }
                }
                break;

            case CHECK_REPWD:
                if (userRePassword.getText().toString().equals("")) {
                    mTextInputLayoutRePwd.setError(getString(R.string.signin_recheck_password));
                    userRePwdVaild = false;
                } else {
                    String userPasswordString = userPassword.getText().toString();
                    String userRePasswordString = userRePassword.getText().toString();
                    if (userPasswordString.equals(userRePasswordString)) {
                        mTextInputLayoutRePwd.setError(null);
                        userRePwdVaild = true;
                    } else {
                        mTextInputLayoutRePwd.setError(getString(R.string.signin_need_correct_repassword));
                        userRePwdVaild = false;
                    }
                }
                break;
        }
    }
}
