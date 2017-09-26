package com.sctw.bonniedraw.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.MainActivity;
import com.sctw.bonniedraw.utility.GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.sctw.bonniedraw.activity.LoginActivity.mGoogleApiClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;
    private static final int THIRD_LOGIN = 1;
    private static final int EMAIL_LOGIN = 4;
    private TextView signupButton;
    private Button emailLoginBtn;
    private Button fbLoginBtn;
    private Button twitterLoginBtn;
    private LoginButton fbLoginBtnGone;
    private Button googlePlusLoginBtn;
    private CallbackManager callbackManager;
    private SharedPreferences prefs;
    private AccessToken accessToken;
    private URL profilePicUrl;
    private FragmentManager fragmentManager;
    private Button forgetPasswordBtn;
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText userEmailText;
    private TextInputEditText userPasswordText;
    boolean checkSystem = false, accountResult = false, emailCheck = false, pwdCheck = false;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailLoginBtn = (Button) view.findViewById(R.id.emailLoginBtn);
        emailLoginBtn.setOnClickListener(emailLogin);
        signupButton = (TextView) view.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(signUp);
        fbLoginBtn = (Button) view.findViewById(R.id.fbLoginBtn);
        fbLoginBtnGone = (LoginButton) view.findViewById(R.id.fbLoginBtnGone);
        fbLoginBtnGone.setReadPermissions(Arrays.asList("public_profile", "email"));
        fbLoginBtnGone.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        fbLoginBtn.setOnClickListener(facebookLogin);
        twitterLoginBtn = (Button) view.findViewById(R.id.twitterLoginBtn);
        twitterLoginBtn.setOnClickListener(twitterLogin);
        googlePlusLoginBtn = (Button) view.findViewById(R.id.googlePlusLoginBtn);
        googlePlusLoginBtn.setOnClickListener(googlePlusLogin);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        fragmentManager = getActivity().getSupportFragmentManager();
        forgetPasswordBtn = (Button) view.findViewById(R.id.forgetPasswordBtn);
        forgetPasswordBtn.setOnClickListener(forgetPwd);

        emailLayout = (TextInputLayout) view.findViewById(R.id.emailLayout);
        passwordLayout = (TextInputLayout) view.findViewById(R.id.passwordLayout);
        emailLayout.setErrorEnabled(true);
        userEmailText = (TextInputEditText) view.findViewById(R.id.inputUserEmail);
        userPasswordText = (TextInputEditText) view.findViewById(R.id.inputUserPassword);
        userPasswordText.addTextChangedListener(checkPassword);
        userEmailText.addTextChangedListener(checkEmail);

        facebookResult();

        view.findViewById(R.id.extraBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transferMainPage();
            }
        });
    }

    TextWatcher checkPassword = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (userPasswordText.getText().toString().isEmpty()) {
                passwordLayout.setError("請輸入密碼");
            } else {
                passwordLayout.setError(null);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    TextWatcher checkEmail = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (userEmailText.getText().toString().isEmpty()) {
                emailLayout.setError(null);
                emailCheck = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmailText.getText().toString()).matches()) {
                emailLayout.setError("請輸入正確的信箱");
                emailCheck = false;
            } else {
                emailLayout.setError(null);
                emailCheck = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener emailLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (emailCheck && !userPasswordText.getText().toString().isEmpty() && userPasswordText.getText().toString().length() >= 6) {
                loginEamil();
            } else {
                emailLayout.setError("請輸入帳號");
                passwordLayout.setError("請輸入密碼");
            }
        }
    };

    private View.OnClickListener facebookLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fbLoginBtnGone.performClick();
        }
    };

    private View.OnClickListener twitterLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(getActivity(), "還不能用", Toast.LENGTH_SHORT).show();
        }
    };

    private View.OnClickListener googlePlusLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };
    private View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_login_layout, new SignUpFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
    };

    private View.OnClickListener forgetPwd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.main_login_layout, new ForgetPasswordFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
    };

    public void facebookResult() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.d("Token", accessToken.getToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    profilePicUrl = new URL(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    prefs.edit()
                                            .putString(GlobalVariable.userPlatformStr, "2")
                                            .putString(GlobalVariable.userTokenStr, accessToken.toString())
                                            .putString(GlobalVariable.userNameStr, object.getString("name"))
                                            .putString(GlobalVariable.userEmailStr, object.getString("email"))
                                            .putString(GlobalVariable.userImgUrlStr, profilePicUrl.toString())
                                            .putString(GlobalVariable.userFbIdStr, object.getString("id"))
                                            .apply();
                                    registerOrLoginThird(GlobalVariable.API_CHECK_EMAIL_CODE);
//                                    if (registerOrLoginThird(3)) {
//                                        if (registerOrLoginThird(2)) checkSystem = registerOrLoginThird(1);
//                                    } else {
//                                        checkSystem = registerOrLoginThird(1);
//                                    }
                                    Log.d("Check FB", prefs.getString(GlobalVariable.userFbIdStr, "null"));
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "發生錯誤", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                System.out.println("onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.v("LoginActivity", exception.getCause().toString());
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("requestCode", String.valueOf(requestCode));
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                try {
                    profilePicUrl = new URL(acct.getPhotoUrl().toString());
                    prefs.edit()
                            .putString(GlobalVariable.userPlatformStr, "3")
                            .putString(GlobalVariable.userTokenStr, acct.getIdToken())
                            .putString(GlobalVariable.userNameStr, acct.getDisplayName())
                            .putString(GlobalVariable.userEmailStr, acct.getEmail())
                            .putString(GlobalVariable.userImgUrlStr, profilePicUrl.toString())
                            .apply();
                    registerOrLoginThird(GlobalVariable.API_CHECK_EMAIL_CODE);
//                    if (registerOrLoginThird(3)) {
//                        if (registerOrLoginThird(2)) checkSystem = registerOrLoginThird(1);
//                    } else {
//                        checkSystem = registerOrLoginThird(1);
//                    }
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "發生錯誤", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void loginEamil() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("uc", userEmailText.getText().toString());
            json.put("up", userPasswordText.getText().toString());
            json.put("ut", GlobalVariable.EMAIL_LOGIN);
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn", GlobalVariable.API_LOGIN);
            Log.d("LOGIN JSON: ", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url("https://www.bonniedraw.com/bonniedraw_service/BDService/login/")
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "登入錯誤", Toast.LENGTH_SHORT).show();
                accountResult = false;
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
                                //Successful
                            } else {
                                createLogSignin();
                            }
                            Log.d("RESTFUL API : ", responseJSON.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //EMAIL登入失敗
    public void createLogSignin() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("登入失敗");
        alertDialog.setMessage("帳號或密碼錯誤，請重新輸入或點選忘記密碼");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //失敗
                    }
                });
        alertDialog.show();
    }

    //第三方平台登入
    public void logThirdSigninError() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("登入失敗");
        alertDialog.setMessage("此平台所使用的電子信箱已註冊，請改用電子信箱或是別的帳戶來登入。");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //失敗
                    }
                });
        alertDialog.show();
    }

    private void registerOrLoginThird(final int select) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = loginJSONFormat(select);
        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url("https://www.bonniedraw.com/bonniedraw_service/BDService/login/")
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), "連線失敗", Toast.LENGTH_SHORT).show();
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
                                //檢測通過丟去註冊，失敗丟去登入
                                if (select == 3) registerOrLoginThird(2);
                                if (select == 2) registerOrLoginThird(1);
                                if (select == 1) {
                                    //要存TOKEN
                                    prefs.edit()
                                            .putString(GlobalVariable.API_TOKEN, "")
                                            .apply();
                                    transferMainPage();
                                }

                            } else {
                                if (select == 3) {
                                    registerOrLoginThird(1);
                                } else if
                                        (select == 2 || select == 1) {
                                    logThirdSigninError();
                                }
                            }
                            Log.d("RESTFUL API : ", responseJSON.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                });
            }
        });
    }

    private JSONObject loginJSONFormat(int style) {
        JSONObject json = new JSONObject();
        try {
            // 1登入 2註冊 3檢查EMAIL
            switch (style) {
                case GlobalVariable.API_LOGIN_CODE:
                    if (prefs.getString(GlobalVariable.userPlatformStr, "").equals(GlobalVariable.THIRD_LOGIN_FACEBOOK)) {
                        json.put("uc", prefs.getString(GlobalVariable.userFbIdStr, ""));
                        json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                        json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                        json.put("fn", GlobalVariable.API_LOGIN);
                    } else {
                        json.put("uc", prefs.getString(GlobalVariable.userEmailStr, ""));
                        json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                        json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                        json.put("fn", GlobalVariable.API_LOGIN);
                    }
                    break;
                case GlobalVariable.API_REGISTER_CODE:
                    if (prefs.getString(GlobalVariable.userPlatformStr, "").equals(GlobalVariable.THIRD_LOGIN_FACEBOOK)) {
                        json.put("uc", prefs.getString(GlobalVariable.userFbIdStr, ""));
                        json.put("un", prefs.getString(GlobalVariable.userNameStr, ""));
                        json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                        json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                        json.put("fn", GlobalVariable.API_REGISTER);
                        json.put("thirdEmail", prefs.getString(GlobalVariable.userEmailStr, ""));
                        json.put("thirdPictureUrl", prefs.getString(GlobalVariable.userImgUrlStr, ""));
                    } else {
                        json.put("uc", prefs.getString(GlobalVariable.userEmailStr, ""));
                        json.put("un", prefs.getString(GlobalVariable.userNameStr, ""));
                        json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                        json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                        json.put("fn", GlobalVariable.API_REGISTER);
                        json.put("thirdEmail", prefs.getString(GlobalVariable.userEmailStr, ""));
                        json.put("thirdPictureUrl", prefs.getString(GlobalVariable.userImgUrlStr, ""));
                    }
                    break;
                case GlobalVariable.API_CHECK_EMAIL_CODE:
                    json.put("uc", prefs.getString(GlobalVariable.userEmailStr, ""));
                    json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_CHECK_EMAIL);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON DATA", json.toString());
        return json;
    }

    public void transferMainPage() {
        Intent it = new Intent();
        it.setClass(getActivity(), MainActivity.class);
        startActivity(it);
        getActivity().finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
