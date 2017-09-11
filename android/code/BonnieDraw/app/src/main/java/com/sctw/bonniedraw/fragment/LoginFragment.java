package com.sctw.bonniedraw.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

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
import com.sctw.bonniedraw.utility.GlobalVariable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;
import static com.sctw.bonniedraw.activity.MainActivity.mGoogleApiClient;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;

    Button signinButton;
    LinearLayout emailLoginLayout;
    LinearLayout facebookLayout;
    LinearLayout googlePlusLayout;
    FragmentTransaction fragmentTransaction;
    CallbackManager callbackManager;
    LoginButton loginButton;
    SharedPreferences prefs;
    AccessToken accessToken;
    URL profilePicUrl;
    private boolean loginSuccess=false;

    public LoginFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("登入");
        signinButton = (Button) view.findViewById(R.id.signinButton);
        signinButton.setOnClickListener(signIn);
        emailLoginLayout = (LinearLayout) view.findViewById(R.id.emailLayout);
        emailLoginLayout.setOnClickListener(emailLogin);
        facebookLayout = (LinearLayout) view.findViewById(R.id.facebookLayout);
        facebookLayout.setOnClickListener(facebookLogin);
        googlePlusLayout = (LinearLayout) view.findViewById(R.id.googlePlusLayout);
        googlePlusLayout.setOnClickListener(googlePlusLogin);
        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        checkLogin();
        facebookResult();

        super.onViewCreated(view, savedInstanceState);
    }

    public void facebookResult() {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.d("Token",accessToken.getToken());
                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try {
                                    profilePicUrl = new URL(object.getJSONObject("picture").getJSONObject("data").getString("url"));
                                    Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                                    prefs.edit()
                                            .putString(GlobalVariable.userPlatformStr, "1")
                                            .putString(GlobalVariable.userTokenStr, accessToken.toString())
                                            .putString(GlobalVariable.userNameStr, object.getString("name"))
                                            .putString(GlobalVariable.userEmailStr, object.getString("email"))
                                            .putString(GlobalVariable.userImgUrlStr, profilePicUrl.toString())
                                            .apply();
                                } catch (IOException | JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,email,picture.width(72).height(72)");
                request.setParameters(parameters);
                request.executeAsync();
                fragmentReplace(new HomeFragment());
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

    private void checkLogin() {
        if (prefs != null && !prefs.getString(GlobalVariable.userTokenStr, "").isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            fragmentReplace(new HomeFragment());
                        }
                    });
            alertDialog.setTitle("重複登入");
            alertDialog.setMessage("請勿重複登入，確認後會回到首頁。");
            alertDialog.show();
            alertDialog.setCancelable(false);
        }
    }

    private View.OnClickListener googlePlusLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    };

    private View.OnClickListener facebookLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            loginButton.performClick();
        }
    };

    private View.OnClickListener emailLogin = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fragmentReplace(new LoginInfoFragment());
        }
    };

    private View.OnClickListener signIn = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            fragmentReplace(new RegisterFragment());
        }
    };

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
                    Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                    prefs.edit()
                            .putString(GlobalVariable.userPlatformStr, "2")
                            .putString(GlobalVariable.userTokenStr, acct.getIdToken())
                            .putString(GlobalVariable.userNameStr, acct.getDisplayName())
                            .putString(GlobalVariable.userEmailStr, acct.getEmail())
                            .putString(GlobalVariable.userImgUrlStr, profilePicUrl.toString())
                            .apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fragmentReplace(new HomeFragment());
            }
        }
    }

    public void fragmentReplace(Fragment fragment) {
        fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

}
