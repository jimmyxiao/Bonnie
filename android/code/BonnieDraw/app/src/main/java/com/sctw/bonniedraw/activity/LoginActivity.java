package com.sctw.bonniedraw.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.firebase.iid.FirebaseInstanceId;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.fragment.LoginFragment;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 0;
    SharedPreferences prefs;
    Boolean mbLoginCheck = false, mBFirst = false;
    FragmentManager fragmentManager;
    LinearLayout mLinearLayout;
    FrameLayout mFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        updateFCM();
        mLinearLayout = findViewById(R.id.frameLayout_login_frist);
        mFrameLayout = findViewById(R.id.frameLayout_login);
        mFrameLayout.setVisibility(View.INVISIBLE);
        AlphaAnimation mAlphaAnimation = new AlphaAnimation(0, 1);
        mAlphaAnimation.setDuration(2000);
        mLinearLayout.setAnimation(mAlphaAnimation);
        mAlphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                if (prefs != null && !prefs.getString(GlobalVariable.USER_TOKEN_STR, "").isEmpty()) {
                    mbLoginCheck = true;
                } else {
                    mbLoginCheck = false;
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (mbLoginCheck) {
                    checkLoginInfo(prefs);
                } else {
                    transferLoginPage();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //login animation
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_STORAGE);
            } else {
                startAndCheck();
            }
        }
    }

    private void updateFCM() {
        prefs.edit()
                .putString(GlobalVariable.USER_FCM_TOKEN_STR, FirebaseInstanceId.getInstance().getToken())
                .putString(GlobalVariable.USER_DEVICE_ID_STR, FirebaseInstanceId.getInstance().getId())
                .apply();
    }

    void startAndCheck() {
        mLinearLayout.startLayoutAnimation();
    }

    private void checkLoginInfo(SharedPreferences prefs) {
        switch (prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0)) {
            case GlobalVariable.EMAIL_LOGIN:
                loginEamil();
                break;
            case 0:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.createToastWindow(getApplicationContext(), getString(R.string.login_data_error), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
                    }
                });
                break;
            default:
                loginThird();
                break;
        }
    }

    private void loginEamil() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.loginJson(prefs, 1);
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.createToastWindow(LoginActivity.this, getString(R.string.connection_failed), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
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
                                        .putInt(GlobalVariable.USER_THIRD_PLATFORM_STR, GlobalVariable.EMAIL_LOGIN)
                                        .putString(GlobalVariable.USER_TOKEN_STR, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.USER_NAME_STR, responseJSON.getJSONObject("userInfo").getString("userName"))
                                        .putString(GlobalVariable.USER_EMAIL_STR, responseJSON.getJSONObject("userInfo").getString("email"))
                                        .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.API_UID, responseJSON.getString("ui"))
                                        .putString(GlobalVariable.USER_IMG_URL_STR, responseJSON.getJSONObject("userInfo").getString("profilePicture"))
                                        .apply();
                                transferMainPage();
                            } else {
                                ToastUtil.createToastWindow(LoginActivity.this, getString(R.string.login_fail), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            transferMainPage();
                        }
                    }
                });
            }
        });
    }

    private void loginThird() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.loginJson(prefs, 3);
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.createToastWindow(LoginActivity.this, getString(R.string.login_fail), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
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
                                //要存TOKEN
                                prefs.edit()
                                        .putString(GlobalVariable.API_TOKEN, responseJSON.getString("lk"))
                                        .putString(GlobalVariable.API_UID, responseJSON.getString("ui"))
                                        .putString(GlobalVariable.USER_NAME_STR, responseJSON.getJSONObject("userInfo").getString("userName"))
                                        .putString(GlobalVariable.USER_IMG_URL_STR, responseJSON.getJSONObject("userInfo").getString("profilePicture"))
                                        .apply();
                                transferMainPage();
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.createToastWindow(LoginActivity.this, getString(R.string.login_fail), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
                            alertDialog.setMessage(R.string.login_fail_server);
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                            alertDialog.show();
                        } finally {
                            transferMainPage();
                        }
                    }
                });
            }
        });
    }

    public void transferLoginPage() {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        LoginFragment loginFragment = new LoginFragment();
        ft.replace(R.id.frameLayout_login, loginFragment, "LoginFragment");
        ft.commit();
        mLinearLayout.setVisibility(View.INVISIBLE);
        mFrameLayout.setVisibility(View.VISIBLE);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startAndCheck();
                } else {
                    //使用者拒絕權限，關閉程式
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
