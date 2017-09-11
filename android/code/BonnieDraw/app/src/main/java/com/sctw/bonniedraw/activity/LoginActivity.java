package com.sctw.bonniedraw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.utility.GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private Button signinButton;
    private LinearLayout emailLoginLayout;
    private LoginButton fbLoginBtn;
    private SignInButton googlePlusLoginBtn;
    private CallbackManager callbackManager;
    private SharedPreferences prefs;
    private AccessToken accessToken;
    private URL profilePicUrl;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signinButton = (Button) findViewById(R.id.signinButton);
        signinButton.setOnClickListener(signIn);
        emailLoginLayout = (LinearLayout) findViewById(R.id.emailLayout);
        emailLoginLayout.setOnClickListener(emailLogin);
        fbLoginBtn = (LoginButton) findViewById(R.id.fbLoginBtn);
        fbLoginBtn.setReadPermissions(Arrays.asList("public_profile", "email"));
        googlePlusLoginBtn = (SignInButton) findViewById(R.id.googlePlusloginBtn);
        googlePlusLoginBtn.setOnClickListener(googlePlusLogin);
        callbackManager = CallbackManager.Factory.create();
        prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        checkLoginInfo(prefs);
        googlePlusResult();
    }

    private View.OnClickListener facebookLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private View.OnClickListener emailLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    private View.OnClickListener googlePlusLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };

    public void googlePlusResult() {
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
                                            .putString(GlobalVariable.userPlatformStr, "1")
                                            .putString(GlobalVariable.userTokenStr, accessToken.toString())
                                            .putString(GlobalVariable.userNameStr, object.getString("name"))
                                            .putString(GlobalVariable.userEmailStr, object.getString("email"))
                                            .putString(GlobalVariable.userImgUrlStr, profilePicUrl.toString())
                                            .apply();
                                    transferMainPage();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplication(), "發生錯誤", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,picture.width(72).height(72)");
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
                            .putString(GlobalVariable.userPlatformStr, "2")
                            .putString(GlobalVariable.userTokenStr, acct.getIdToken())
                            .putString(GlobalVariable.userNameStr, acct.getDisplayName())
                            .putString(GlobalVariable.userEmailStr, acct.getEmail())
                            .putString(GlobalVariable.userImgUrlStr, profilePicUrl.toString())
                            .apply();
                    transferMainPage();
                } catch (IOException e) {
                    Toast.makeText(this, "發生錯誤", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }
    }

    private void checkLoginInfo(SharedPreferences prefs) {
        if (prefs != null && !prefs.getString(GlobalVariable.userTokenStr, "").isEmpty()) {
            transferMainPage();
            //登入成功
        } else {
            Log.e("Not Found", "No LoginInfo");
        }
    }

    public void transferMainPage() {
        Intent it = new Intent();
        it.setClass(this, MainActivity.class);
        startActivity(it);
    }
}
