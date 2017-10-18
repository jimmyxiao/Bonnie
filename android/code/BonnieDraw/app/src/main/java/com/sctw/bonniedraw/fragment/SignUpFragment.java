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
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment {
    TextInputLayout phoneLayout, nameLayout, emailLayout, passwordLayout, repasswordLayout;
    TextInputEditText userPhone, userName, userEmail, userPassword, userRePassword;
    TextView signupButton;
    boolean userPhoneVaild, userNameVaild, userEmailVaild, userPwdVaild, userRePwdVaild = false;
    Pattern pattern;
    Matcher matcher;
    final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
    Button signInBtn;
    FragmentManager fragmentManager;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        pattern = Pattern.compile(PASSWORD_PATTERN);
        phoneLayout = (TextInputLayout) view.findViewById(R.id.phoneLayout);
        nameLayout = (TextInputLayout) view.findViewById(R.id.nameLayout);
        emailLayout = (TextInputLayout) view.findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) view.findViewById(R.id.passwordLayout);
        repasswordLayout = (TextInputLayout) view.findViewById(R.id.repasswordLayout);
        userPhone = (TextInputEditText) view.findViewById(R.id.inputUserPhone);
        userName = (TextInputEditText) view.findViewById(R.id.inputUserName);
        userEmail = (TextInputEditText) view.findViewById(R.id.inputUserEmail);
        userPassword = (TextInputEditText) view.findViewById(R.id.inputUserPassword);
        userRePassword = (TextInputEditText) view.findViewById(R.id.inputUserRePassword);
        userPhone.setOnFocusChangeListener(userPhoneOnFocus);
        userPhone.addTextChangedListener(userPhoneInvalid);
        userName.setOnFocusChangeListener(userNameOnFocus);
        userName.addTextChangedListener(userNameInvalid);
        userEmail.setOnFocusChangeListener(userEmailOnFocus);
        userEmail.addTextChangedListener(userEmailInvalid);
        userPassword.setOnFocusChangeListener(userPwdOnFocus);
        userPassword.addTextChangedListener(userPasswordInvalid);
        userRePassword.setOnFocusChangeListener(userRePwdOnFocus);
        userRePassword.addTextChangedListener(userRePasswordInvalid);
        signupButton = (TextView) view.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(signUp);
        fragmentManager = getFragmentManager();

        signInBtn = (Button) view.findViewById(R.id.signInBtn);
        signInBtn.setOnClickListener(signIn);
        super.onViewCreated(view, savedInstanceState);
    }

    //註冊按鈕
    public View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_login_layout, new LoginFragment());
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.commit();
        }
    };

    public View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (userPhoneVaild && userNameVaild && userEmailVaild && userPwdVaild && userRePwdVaild) {
                    signupAPI(3);
            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                infoCheck("phone");
                infoCheck("name");
                infoCheck("email");
                infoCheck("pwd");
                infoCheck("rePwd");

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
        signInBtn.setEnabled(false);
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = registerJSONFormat(style);
        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url("https://www.bonniedraw.com/bonniedraw_service/BDService/login/")
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "註冊失敗", Toast.LENGTH_SHORT).show();
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
                                if(style==3) signupAPI(2);
                                if(style==2) createLogSignup(1);
                            }else {
                                if(style==3) createLogSignup(2);
                                if(style==2) createLogSignup(3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void createLogSignup(int format){
        String title="";
        String message="";
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        switch (format){
            case 1:
                title=getString(R.string.signin_successful_title);
                message=getString(R.string.sigin_successful_msg);

                break;
            case 2:
                title=getString(R.string.signin_fail_title);
                message=getString(R.string.signin_fail_email_used);
                break;
            case 3:
                title=getString(R.string.signin_fail_title);
                message=getString(R.string.signin_fail_date_error);
        }
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        signInBtn.setEnabled(true);
                        dialog.dismiss();
                        //寄送認證信，回到主畫面。
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //初始點選
    public View.OnFocusChangeListener userPhoneOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userPhone.getText().toString().trim().isEmpty()) {
                phoneLayout.setError(getString(R.string.signin_check_phone));
            }
        }
    };

    public View.OnFocusChangeListener userNameOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userName.getText().toString().trim().isEmpty()) {
                nameLayout.setError(getString(R.string.signin_check_name));
            }
        }
    };

    public View.OnFocusChangeListener userEmailOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userEmail.getText().toString().equals("")) {
                emailLayout.setError(getString(R.string.signin_check_email));
            }
        }
    };

    public View.OnFocusChangeListener userPwdOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userPassword.getText().toString().equals("")) {
                passwordLayout.setError(getString(R.string.login_need_password));
            }
        }
    };

    public View.OnFocusChangeListener userRePwdOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userRePassword.getText().toString().equals("")) {
                repasswordLayout.setError(getString(R.string.signin_recheck_password));
            }
        }
    };

    public TextWatcher userPhoneInvalid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            infoCheck("phone");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public TextWatcher userNameInvalid = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            infoCheck("name");
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
            infoCheck("email");
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
            infoCheck("pwd");
        }

        @Override
        public void afterTextChanged(Editable editable) {
            infoCheck("rePwd");
        }
    };

    public TextWatcher userRePasswordInvalid = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            infoCheck("rePwd");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    //檢查輸入資料
    public void infoCheck(String str) {
        switch (str) {
            case "phone":
                if (userPhone.getText().toString().trim().isEmpty()) {
                    phoneLayout.setError("請輸入手機號碼");
                    userPhoneVaild = false;
                } else if (userPhone.getText().toString().length() < 10) {
                    phoneLayout.setError("請輸入正確的手機號碼");
                    userPhoneVaild = false;
                } else {
                    phoneLayout.setError(null);
                    userPhoneVaild = true;
                }
                break;
            case "name":
                if (userName.getText().toString().equals("")) {
                    nameLayout.setError("請輸入姓名");
                    userNameVaild = false;
                } else {
                    nameLayout.setError(null);
                    userNameVaild = true;
                }
                break;
            case "email":
                if (userEmail.getText().toString().trim().isEmpty()) {
                    emailLayout.setError("請輸入Email");
                    userEmailVaild = false;
                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail.getText().toString()).matches()) {
                    emailLayout.setError("請輸入正確的Email");
                    userEmailVaild = false;
                } else {
                    emailLayout.setError(null);
                    userEmailVaild = true;
                }
                break;
            case "pwd":
                if (userPassword.getText().toString().equals("")) {
                    passwordLayout.setError("請輸入密碼");
                    userPwdVaild = false;
                } else {
                    matcher = pattern.matcher(userPassword.getText().toString());
                    if (!matcher.matches()) {
                        userPwdVaild = false;
                        if (userPassword.getText().toString().length() < 6) {
                            passwordLayout.setError("密碼至少需要6個字元");
                        } else {
                            passwordLayout.setError("密碼需要一個特殊符號、數字、大小寫字母");
                        }
                    } else {
                        passwordLayout.setError(null);
                        userPwdVaild = true;
                    }
                }
                break;

            case "rePwd":
                if (userRePassword.getText().toString().equals("")) {
                    repasswordLayout.setError("請再次輸入密碼");
                    userRePwdVaild = false;
                } else {
                    String userPasswordString = userPassword.getText().toString();
                    String userRePasswordString = userRePassword.getText().toString();
                    if (userPasswordString.equals(userRePasswordString)) {
                        repasswordLayout.setError(null);
                        userRePwdVaild = true;
                    } else {
                        repasswordLayout.setError("密碼不一致");
                        userRePwdVaild = false;
                    }
                }
                break;
        }
    }
}
