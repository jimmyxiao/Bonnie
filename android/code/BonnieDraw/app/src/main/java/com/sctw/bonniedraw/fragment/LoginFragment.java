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
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;

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
    private TextView signupButton;
    private Button emailLoginBtn;
    private Button fbLoginBtn;
    private Button twitterLoginBtn;
    private Button googlePlusLoginBtn;
    private TwitterLoginButton twitterLoginButtonGone;
    private LoginButton fbLoginBtnGone;
    private CallbackManager callbackManager;
    private SharedPreferences prefs;
    private AccessToken accessToken;
    private URL profilePicUrl;
    private FragmentManager fragmentManager;
    private Button forgetPasswordBtn;
    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText userEmailText;
    private TextInputEditText userPasswordText;
    boolean emailCheck = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        fragmentManager = getActivity().getSupportFragmentManager();

        emailLoginBtn = (Button) view.findViewById(R.id.emailLoginBtn);
        emailLoginBtn.setOnClickListener(emailLogin);
        signupButton = (TextView) view.findViewById(R.id.signupButton);
        signupButton.setOnClickListener(signUp);
        //FB init
        fbLoginBtn = (Button) view.findViewById(R.id.fbLoginBtn);
        fbLoginBtnGone = (LoginButton) view.findViewById(R.id.fbLoginBtnGone);
        fbLoginBtnGone.setReadPermissions(Arrays.asList("public_profile", "email"));
        fbLoginBtnGone.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        fbLoginBtn.setOnClickListener(facebookLogin);
        //Twitter init
        twitterLoginBtn = (Button) view.findViewById(R.id.twitterLoginBtn);
        twitterLoginBtn.setOnClickListener(twitterLogin);
        twitterLoginButtonGone = (TwitterLoginButton) view.findViewById(R.id.twitterLoginBtnGone);
        //Google init
        googlePlusLoginBtn = (Button) view.findViewById(R.id.googlePlusLoginBtn);
        googlePlusLoginBtn.setOnClickListener(googlePlusLogin);

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
        twitterResult();
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
            twitterLoginButtonGone.performClick();
        }
    };

    private void twitterResult() {
        twitterLoginButtonGone.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
            @Override
            //登入成功
            public void success(Result<TwitterSession> result) {
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                final String token = authToken.token;
                String secret = authToken.secret;
                //抓資料
                retrofit2.Call<User> user = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false, true);
                user.enqueue(new com.twitter.sdk.android.core.Callback<User>() {
                    @Override
                    public void success(Result<User> userResult) {
                        String name = userResult.data.name;
                        String email = userResult.data.email;
                        String photoUrl = userResult.data.profileImageUrl.replace("_normal", "_bigger");
                        prefs.edit()
                                .putString(GlobalVariable.userPlatformStr, "4")
                                .putString(GlobalVariable.userTokenStr, token)
                                .putString(GlobalVariable.userNameStr, name)
                                .putString(GlobalVariable.userEmailStr, email)
                                .putString(GlobalVariable.userImgUrlStr, photoUrl)
                                .apply();
                        registerOrLoginThird(GlobalVariable.API_CHECK_EMAIL_CODE);
                    }

                    @Override
                    public void failure(TwitterException exc) {
                        Log.d("TwitterKit", "Verify Credentials Failure", exc);
                    }
                });
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("Twitter Login", "Failed");
            }
        });
    }

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
                                    Log.d("Check FB", prefs.getString(GlobalVariable.userFbIdStr, "null"));
                                    Log.d("Check FB IMG URL", prefs.getString(GlobalVariable.userImgUrlStr, "null"));
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getActivity(), "發生錯誤", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,picture.type(large)");
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
        //FB
        callbackManager.onActivityResult(requestCode, resultCode, data);
        //GOOGLE PLUS
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
                } catch (IOException e) {
                    Toast.makeText(getActivity(), "發生錯誤", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
        //TWITTER
        twitterLoginButtonGone.onActivityResult(requestCode, resultCode, data);
    }

    private void loginEamil() {
        emailLoginBtn.setEnabled(false);
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
                                Log.d("RESTFUL API : ", responseJSON.toString());
                                prefs.edit()
                                        .putString(GlobalVariable.userPlatformStr, "1")
                                        .putString("emailLoginPwd", userPasswordText.getText().toString())
                                        .putString(GlobalVariable.userTokenStr, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.userNameStr, responseJSON.getJSONObject("userInfo").getString("userName"))
                                        .putString(GlobalVariable.userEmailStr, responseJSON.getJSONObject("userInfo").getString("email"))
                                        .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.API_UID,responseJSON.getString("ui"))
                                        .apply();
                                transferMainPage();
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
                        if (!prefs.getString(GlobalVariable.userFbIdStr, "null").isEmpty()) {
                            LoginManager.getInstance().logOut();
                        }
                        prefs.edit().clear().apply();
                        emailLoginBtn.setEnabled(true);
                        dialog.dismiss();
                        //失敗
                    }
                });
        alertDialog.setCancelable(false);
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
                                            .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                            .putString(GlobalVariable.API_UID,responseJSON.getString("ui"))
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
            if (prefs.getString(GlobalVariable.userPlatformStr, "").equals(GlobalVariable.THIRD_LOGIN_FACEBOOK)) {
                json.put("uc", prefs.getString(GlobalVariable.userFbIdStr, ""));
            } else {
                json.put("uc", prefs.getString(GlobalVariable.userEmailStr, ""));
            }

            switch (style) {
                case GlobalVariable.API_LOGIN_CODE:
                    json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.userEmailStr, ""));
                    json.put("thirdPictureUrl", prefs.getString(GlobalVariable.userImgUrlStr, ""));
                    break;
                case GlobalVariable.API_REGISTER_CODE:
                    json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                    json.put("un", prefs.getString(GlobalVariable.userNameStr, ""));
                    json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_REGISTER);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.userEmailStr, ""));
                    json.put("thirdPictureUrl", prefs.getString(GlobalVariable.userImgUrlStr, ""));
                    break;
                case GlobalVariable.API_CHECK_EMAIL_CODE:
                    json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_CHECK_EMAIL);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.userEmailStr, ""));
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
