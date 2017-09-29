package com.sctw.bonniedraw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    public static GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig(getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_login);
        prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        checkLoginInfo(prefs);
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.main_login_layout, new LoginFragment(), "LoginFragment");
        ft.commit();

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
    }

    private void checkLoginInfo(SharedPreferences prefs) {
        if (prefs != null && !prefs.getString(GlobalVariable.userTokenStr, "").isEmpty()) {
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
            //登入成功
        } else {
            Log.e("Not Found", "No LoginInfo");
        }
    }

    private void loginEamil() {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject json = new JSONObject();
        try {
            json.put("uc", prefs.getString(GlobalVariable.userEmailStr, "null"));
            json.put("up", prefs.getString("emailLoginPwd", "null"));
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
                Toast.makeText(getApplication(), "登入錯誤", Toast.LENGTH_SHORT).show();
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
                                        .putString(GlobalVariable.USER_UID,responseJSON.getJSONObject("userInfo").getString("userId"))
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
        JSONObject json = loginJSONFormat();
        RequestBody body = RequestBody.create(mediaType, json.toString());
        Request request = new Request.Builder()
                .url("https://www.bonniedraw.com/bonniedraw_service/BDService/login/")
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
                                            .putString(GlobalVariable.USER_UID,responseJSON.getJSONObject("userInfo").getString("userId"))
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

    private JSONObject loginJSONFormat() {
        JSONObject json = new JSONObject();
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("JSON DATA", json.toString());
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
        }
        else Log.d("Twitter", "fragment is null");
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
