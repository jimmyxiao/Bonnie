package com.sctw.bonniedraw.fragment;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sctw.bonniedraw.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {
    TextInputLayout phoneLayout, nameLayout, emailLayout, passwordLayout, repasswordLayout;
    TextInputEditText userPhone, userName, userEmail, userPassword, userRePassword;
    TextView signupButton;
    Drawable doneIcon;
    boolean userPhoneVaild, userNameVaild, userEmailVaild, userPwdVaild, userRePwdVaild = false;
    Pattern pattern;
    Matcher matcher;
    final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{6,}$";
    Button signInBtn;
    FragmentManager fragmentManager;

    public SignInFragment() {
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

        doneIcon = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_done_black_24dp, null);
        if (doneIcon != null) {
            doneIcon.setBounds(0, 0, doneIcon.getIntrinsicWidth(), doneIcon.getIntrinsicHeight());
        }

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
            String title;
            String message;
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

            if (userPhoneVaild && userNameVaild && userEmailVaild && userPwdVaild && userRePwdVaild) {
                title = "註冊成功";
                message = "您的帳號已註冊成功，請在24小時內至信件夾收取認證信並認證，否則帳號會失效。";
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "確認",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //寄送認證信，回到主畫面。
                            }
                        });
            } else {
                title = "請確認輸入資料";
                message = "您的輸入資料有誤或未填寫。";
                infoCheck("phone");
                infoCheck("name");
                infoCheck("email");
                infoCheck("pwd");
                infoCheck("rePwd");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "是",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
            }

            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.show();
        }
    };

    //初始點選
    public View.OnFocusChangeListener userPhoneOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userPhone.getText().toString().trim().isEmpty()) {
                phoneLayout.setError("請輸入手機號碼");
            }
        }
    };

    public View.OnFocusChangeListener userNameOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userName.getText().toString().trim().isEmpty()) {
                nameLayout.setError("請輸入名字");
            }
        }
    };

    public View.OnFocusChangeListener userEmailOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userEmail.getText().toString().equals("")) {
                emailLayout.setError("請輸入信箱");
            }
        }
    };

    public View.OnFocusChangeListener userPwdOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userPassword.getText().toString().equals("")) {
                passwordLayout.setError("請輸入密碼");
            }
        }
    };

    public View.OnFocusChangeListener userRePwdOnFocus = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (userRePassword.getText().toString().equals("")) {
                repasswordLayout.setError("請再次輸入密碼");
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
