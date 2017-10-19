package com.sctw.bonniedraw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.fragment.LoginFragment;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences prefs;
    Boolean result = false;
    FragmentManager fragmentManager;
    LinearLayout mLinearLayout;
    public static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        ;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        System.out.println(connectionResult.toString());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        AlphaAnimation mAlphaAnimation = new AlphaAnimation(0, 1);
        mAlphaAnimation.setDuration(2000);
        mLinearLayout = findViewById(R.id.frameLayout_login_frist);
        mLinearLayout.setAnimation(mAlphaAnimation);
        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (prefs != null && !prefs.getString(GlobalVariable.userTokenStr, "").isEmpty()) {
                    checkLoginInfo(prefs);
                } else {
                    fragmentManager = getSupportFragmentManager();
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    LoginFragment loginFragment = new LoginFragment();
                    ft.add(R.id.frameLayout_login, loginFragment, "LoginFragment");
                    ft.commit();
                    mLinearLayout.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void checkLoginInfo(SharedPreferences prefs) {
        switch (prefs.getString(GlobalVariable.userPlatformStr, "null")) {
            case GlobalVariable.EMAIL_LOGIN:
                loginEamil();
                break;
            case "null":
                Toast.makeText(this, "登入資料異常", Toast.LENGTH_SHORT).show();
                break;
            default:
                loginThird();
                break;

        }
    }

    private void loginEamil() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = loginJSONFormat(1);

        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LOGIN)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "連線失敗，請檢查網路狀態", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseStr);
                            if (responseJSON.getInt("res") == 1) {
                                //Successful
                                prefs.edit()
                                        .putString(GlobalVariable.userPlatformStr, "1")
                                        .putString(GlobalVariable.userTokenStr, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.userNameStr, responseJSON.getJSONObject("userInfo").getString("userName"))
                                        .putString(GlobalVariable.userEmailStr, responseJSON.getJSONObject("userInfo").getString("email"))
                                        .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.API_UID, responseJSON.getString("ui"))
                                        .apply();
                                transferMainPage();
                            } else {
                                Toast.makeText(getApplication(), "登入失敗", Toast.LENGTH_SHORT).show();
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

    private void loginThird() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = loginJSONFormat(3);
        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url(GlobalVariable.API_LINK_LOGIN)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getApplicationContext(), "連線失敗", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseStr = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject responseJSON = new JSONObject(responseStr);
                            if (responseJSON.getInt("res") == 1) {
                                //要存TOKEN
                                prefs.edit()
                                        .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.API_UID, responseJSON.getString("ui"))
                                        .apply();
                                transferMainPage();
                            } else {
                                Toast.makeText(getApplication(), "登入失敗", Toast.LENGTH_SHORT).show();
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

    private JSONObject loginJSONFormat(int type) {
        JSONObject json = new JSONObject();
        try {
            switch (type) {
                case 3:
                    if (prefs.getString(GlobalVariable.userPlatformStr, "").equals(GlobalVariable.THIRD_LOGIN_FACEBOOK)) {
                        json.put("uc", prefs.getString(GlobalVariable.userFbIdStr, ""));
                    } else {
                        json.put("uc", prefs.getString(GlobalVariable.userEmailStr, ""));
                    }
                    json.put("ut", prefs.getString(GlobalVariable.userPlatformStr, ""));
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    json.put("thirdEmail", prefs.getString(GlobalVariable.userEmailStr, ""));
                    json.put("thirdPictureUrl", prefs.getString(GlobalVariable.userImgUrlStr, ""));
                    break;
                case 1:
                    json.put("uc", prefs.getString(GlobalVariable.userEmailStr, "null"));
                    json.put("up", prefs.getString("emailLoginPwd", "null"));
                    json.put("ut", GlobalVariable.EMAIL_LOGIN);
                    json.put("dt", GlobalVariable.LOGIN_PLATFORM);
                    json.put("fn", GlobalVariable.API_LOGIN);
                    break;
            }
            Log.d("JSON DATA", json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    public void transferMainPage() {
        Intent it = new Intent();
        it.setClass(this, MainActivity.class);
        startActivity(it);
        finish();
    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragment = getSupportFragmentManager();
        if (fragment != null) {
            fragment.findFragmentByTag("LoginFragment").onActivityResult(requestCode, resultCode, data);
        } else Log.d("Twitter", "fragment is null");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
