package com.sctw.bonniedraw.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.MainActivity;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.ToastUtil;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;
    private TextView mTextViewSignup;
    private Button mBtnEmailLogin, mBtnFacebookLogin, mBtnTwitterLogin, mBtnGooglePlusLogin, mBtnForgetPassword;
    private TwitterLoginButton twitterLoginButton;
    private LoginButton facebookLoginButton;
    private CallbackManager callbackManager;
    private SharedPreferences prefs;
    private AccessToken accessToken;
    private FragmentManager fragmentManager;
    private URL profilePicUrl;
    private TextInputLayout mInputLayoutEmail, mInputLayoutPassword;
    private TextInputEditText mInputEditTextEmail, mInputEditTextPassword;
    boolean emailCheck = false;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        fragmentManager = getActivity().getSupportFragmentManager();
        init();
        mBtnEmailLogin = (Button) view.findViewById(R.id.btn_email_login);
        mBtnEmailLogin.setOnClickListener(clickListenerEmail);
        mTextViewSignup = (TextView) view.findViewById(R.id.textView_singup_login);
        mTextViewSignup.setOnClickListener(clickListenerSignup);
        //FB init
        mBtnFacebookLogin = (Button) view.findViewById(R.id.btn_fb_login);
        facebookLoginButton = (LoginButton) view.findViewById(R.id.btn_fb_login_hide);
        facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        facebookLoginButton.setFragment(this);
        callbackManager = CallbackManager.Factory.create();
        mBtnFacebookLogin.setOnClickListener(clickListenerFcaebookLogin);
        //Twitter init
        mBtnTwitterLogin = (Button) view.findViewById(R.id.btn_twitter_login);
        mBtnTwitterLogin.setOnClickListener(clickListenerTwitterLogin);
        twitterLoginButton = (TwitterLoginButton) view.findViewById(R.id.btn_twitter_login_hide);
        //Google init
        mBtnGooglePlusLogin = (Button) view.findViewById(R.id.btn_google_plus_login);
        mBtnGooglePlusLogin.setOnClickListener(clickListenerGooglePlusLogin);

        mBtnForgetPassword = (Button) view.findViewById(R.id.btn_forget_password);
        mBtnForgetPassword.setOnClickListener(clickListenerForgetPwd);
        mInputLayoutEmail = (TextInputLayout) view.findViewById(R.id.inputLayout_signup_email);
        mInputLayoutPassword = (TextInputLayout) view.findViewById(R.id.inputLayout_signup_password);
        mInputLayoutEmail.setErrorEnabled(true);
        mInputEditTextEmail = (TextInputEditText) view.findViewById(R.id.editText_signup_email);
        mInputEditTextPassword = (TextInputEditText) view.findViewById(R.id.editText_signup_password);
        mInputEditTextEmail.addTextChangedListener(checkEmail);
        facebookLinkAPI();
        twitterResult();
    }

    void init() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(), new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        System.out.println(connectionResult.toString());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    TextWatcher checkEmail = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mInputEditTextEmail.getText().toString().isEmpty()) {
                mInputLayoutEmail.setError(null);
                emailCheck = false;
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mInputEditTextEmail.getText().toString()).matches()) {
                mInputLayoutEmail.setError(getString(R.string.login_need_correct_email));
                emailCheck = false;
            } else {
                mInputLayoutEmail.setError(null);
                emailCheck = true;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private View.OnClickListener clickListenerEmail = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (emailCheck) {
                checkEmail();
            } else {
                mInputLayoutEmail.setError(getString(R.string.login_need_email));
            }
        }
    };

    private View.OnClickListener clickListenerFcaebookLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            facebookLoginButton.performClick();
        }
    };

    private View.OnClickListener clickListenerTwitterLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            twitterLoginButton.performClick();
        }
    };

    private void twitterResult() {
        twitterLoginButton.setCallback(new com.twitter.sdk.android.core.Callback<TwitterSession>() {
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
                        String id = userResult.data.idStr;
                        prefs.edit()
                                .putInt(GlobalVariable.USER_THIRD_PLATFORM_STR, GlobalVariable.THIRD_LOGIN_TWITTER)
                                .putString(GlobalVariable.USER_TOKEN_STR, token)
                                .putString(GlobalVariable.USER_NAME_STR, name)
                                .putString(GlobalVariable.USER_EMAIL_STR, email)
                                .putString(GlobalVariable.USER_IMG_URL_STR, photoUrl)
                                .putString(GlobalVariable.USER_FB_TWITTER_ID_STR, id)
                                .apply();
                        loginThird(GlobalVariable.API_LOGIN_CODE);
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

    private View.OnClickListener clickListenerGooglePlusLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };
    private View.OnClickListener clickListenerSignup = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.frameLayout_login, new SignUpFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
    };

    private View.OnClickListener clickListenerForgetPwd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.frameLayout_login, new ForgetPasswordFragment());
            ft.addToBackStack(null);
            ft.commit();
        }
    };

    public void facebookLinkAPI() {
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
                                            .putInt(GlobalVariable.USER_THIRD_PLATFORM_STR, GlobalVariable.THIRD_LOGIN_FACEBOOK)
                                            .putString(GlobalVariable.USER_TOKEN_STR, accessToken.toString())
                                            .putString(GlobalVariable.USER_NAME_STR, object.getString("name"))
                                            .putString(GlobalVariable.USER_EMAIL_STR, object.getString("email"))
                                            .putString(GlobalVariable.USER_IMG_URL_STR, profilePicUrl.toString())
                                            .putString(GlobalVariable.USER_FB_TWITTER_ID_STR, object.getString("id"))
                                            .apply();
                                    loginThird(GlobalVariable.API_LOGIN_CODE);
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                    ToastUtil.createToastWindow(getContext(), "發生錯誤", PxDpConvert.getSystemHight(getContext()) / 4);
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
                            .putInt(GlobalVariable.USER_THIRD_PLATFORM_STR, GlobalVariable.THIRD_LOGIN_GOOGLE)
                            .putString(GlobalVariable.USER_TOKEN_STR, acct.getIdToken())
                            .putString(GlobalVariable.USER_NAME_STR, acct.getDisplayName())
                            .putString(GlobalVariable.USER_EMAIL_STR, acct.getEmail())
                            .putString(GlobalVariable.USER_IMG_URL_STR, profilePicUrl.toString())
                            .apply();
                    loginThird(GlobalVariable.API_LOGIN_CODE);
                } catch (IOException e) {
                    ToastUtil.createToastWindow(getContext(), "發生錯誤", PxDpConvert.getSystemHight(getContext()) / 4);
                    e.printStackTrace();
                }
            }
        }
        //TWITTER
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void checkEmail() {
        mBtnEmailLogin.setEnabled(false);
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = new JSONObject();
        try {
            json.put("uc", mInputEditTextEmail.getText().toString());
            json.put("up", mInputEditTextPassword.getText().toString());
            json.put("ut", GlobalVariable.EMAIL_LOGIN);
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn", GlobalVariable.API_CHECK_EMAIL);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(json.toString());

        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LOGIN)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), R.string.login_fail_tittle, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseStr);
                            if (responseJSON.getInt("res") == 2) {
                                //Successful
                                loginEamil();
                            } else {
                                createLogSignin("此帳號尚未申請");
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

    private void loginEamil() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = new JSONObject();
        try {
            json.put("uc", mInputEditTextEmail.getText().toString());
            json.put("up", mInputEditTextPassword.getText().toString());
            json.put("ut", GlobalVariable.EMAIL_LOGIN);
            json.put("dt", GlobalVariable.LOGIN_PLATFORM);
            json.put("fn", GlobalVariable.API_LOGIN);
            json.put("token", prefs.getString(GlobalVariable.USER_FCM_TOKEN_STR, ""));
            json.put("deviceId", prefs.getString(GlobalVariable.USER_DEVICE_ID_STR, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println(json.toString());

        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LOGIN)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getActivity(), R.string.login_fail_tittle, Toast.LENGTH_SHORT).show();
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
                                prefs.edit()
                                        .putInt(GlobalVariable.USER_THIRD_PLATFORM_STR, GlobalVariable.EMAIL_LOGIN)
                                        .putString(GlobalVariable.USER_PWD_STR, mInputEditTextPassword.getText().toString())
                                        .putString(GlobalVariable.USER_TOKEN_STR, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.USER_NAME_STR, responseJSON.getJSONObject("userInfo").getString("userName"))
                                        .putString(GlobalVariable.USER_EMAIL_STR, responseJSON.getJSONObject("userInfo").getString("email"))
                                        .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.API_UID, responseJSON.getString("ui"))
                                        .putString(GlobalVariable.USER_IMG_URL_STR, responseJSON.getJSONObject("userInfo").getString("profilePicture"))
                                        .apply();
                                transferMainPage();
                            } else {
                                createLogSignin(getString(R.string.login_fail_msg));
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
    public void createLogSignin(String failString) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(getString(R.string.login_fail_tittle));
        alertDialog.setMessage(failString);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, (getString(R.string.commit)),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (!prefs.getString(GlobalVariable.USER_FB_TWITTER_ID_STR, "null").isEmpty()) {
                            LoginManager.getInstance().logOut();
                        }
                        prefs.edit().clear().apply();
                        mBtnEmailLogin.setEnabled(true);
                        dialog.dismiss();
                        //失敗
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    //登入失敗自己登出第三方平台
    public void logoutPlatform() {
        switch (prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0)) {
            case GlobalVariable.EMAIL_LOGIN:
                prefs.edit().clear().apply();
                break;
            case GlobalVariable.THIRD_LOGIN_FACEBOOK:
                LoginManager.getInstance().logOut();
                prefs.edit().clear().apply();
                break;
            case GlobalVariable.THIRD_LOGIN_GOOGLE:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        prefs.edit().clear().apply();
                    }
                });
                break;
            case GlobalVariable.THIRD_LOGIN_TWITTER:
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                prefs.edit().clear().apply();
                break;
            case 0:
                Toast.makeText(getActivity(), "has error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //第三方平台登入錯誤訊息
    public void logThirdSigninError() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.login_fail_tittle);
        alertDialog.setMessage("此平台所使用的電子信箱已註冊，請改用電子信箱或是別的帳戶來登入。");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.commit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutPlatform();
                        dialog.dismiss();
                        //失敗
                    }
                });
        alertDialog.show();
    }

    private void loginThird(int select) {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        JSONObject json = loginJSONFormat(select);
        System.out.println(json.toString());
        RequestBody body = RequestBody.create(ConnectJson.MEDIA_TYPE_JSON_UTF8, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LOGIN)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                ToastUtil.createToastWindow(getContext(), "連線失敗", PxDpConvert.getSystemHight(getContext()) / 4);
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
                                //要存TOKEN
                                prefs.edit()
                                        .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.API_UID, responseJSON.getString("ui"))
                                        .putString(GlobalVariable.USER_NAME_STR, responseJSON.getJSONObject("userInfo").getString("userName"))
                                        .putString(GlobalVariable.USER_IMG_URL_STR, responseJSON.getJSONObject("userInfo").getString("profilePicture"))
                                        .apply();
                                transferMainPage();

                            } else {
                                logThirdSigninError();
                            }
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
            int platform = prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0);
            if (platform == GlobalVariable.THIRD_LOGIN_FACEBOOK || platform == GlobalVariable.THIRD_LOGIN_TWITTER) {
                json.put("uc", prefs.getString(GlobalVariable.USER_FB_TWITTER_ID_STR, ""));
            } else {
                json.put("uc", prefs.getString(GlobalVariable.USER_EMAIL_STR, ""));
            }

            switch (style) {
                case GlobalVariable.API_LOGIN_CODE:
                    json.put("ut", prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0));
                    json.put("un", prefs.getString(GlobalVariable.USER_NAME_STR, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.USER_EMAIL_STR, ""));
                    json.put("thirdPictureUrl", prefs.getString(GlobalVariable.USER_IMG_URL_STR, ""));
                    json.put("token", prefs.getString(GlobalVariable.USER_FCM_TOKEN_STR, ""));
                    json.put("deviceId", prefs.getString(GlobalVariable.USER_DEVICE_ID_STR, ""));
                    break;
                case GlobalVariable.API_REGISTER_CODE:
                    json.put("ut", prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0));
                    json.put("un", prefs.getString(GlobalVariable.USER_NAME_STR, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_REGISTER);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.USER_EMAIL_STR, ""));
                    json.put("thirdPictureUrl", prefs.getString(GlobalVariable.USER_IMG_URL_STR, ""));
                    break;
                case GlobalVariable.API_CHECK_EMAIL_CODE:
                    json.put("ut", prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_CHECK_EMAIL);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.USER_EMAIL_STR, ""));
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void transferMainPage() {
        Intent it = new Intent();
        it.setClass(getActivity(), MainActivity.class);
        startActivity(it);
        getActivity().finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}
