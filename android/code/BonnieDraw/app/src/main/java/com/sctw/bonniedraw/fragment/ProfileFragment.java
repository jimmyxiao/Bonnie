package com.sctw.bonniedraw.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.activity.LoginActivity;
import com.sctw.bonniedraw.utility.GlobalVariable;

import java.io.IOException;
import java.net.URL;

import static android.content.Context.MODE_PRIVATE;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private Toolbar profileToolbar;
    private ImageView profilePhoto;
    private TextView profileUserName,profileUserId,profileWorks,profileUserFans,profileUserFollow;
    private ImageButton logoutBotton,profileSettingBtn;
    SharedPreferences prefs;
    GoogleApiClient mGoogleApiClient;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefs = getActivity().getSharedPreferences("userInfo", MODE_PRIVATE);
        profilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
        profileUserName = (TextView) view.findViewById(R.id.profile_userName);
        profileUserId = (TextView) view.findViewById(R.id.profile_userId);
        profileWorks = (TextView) view.findViewById(R.id.profile_userWorks);
        profileUserFollow = (TextView) view.findViewById(R.id.profile_userFans);
        profileUserFans = (TextView) view.findViewById(R.id.profile_userFollow);
        logoutBotton = (ImageButton) view.findViewById(R.id.logoutBtn);
        profileToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        profileSettingBtn = (ImageButton) view.findViewById(R.id.profile_setting_btn);
        ((AppCompatActivity)getActivity()).setSupportActionBar(profileToolbar);
        String userName = prefs.getString(GlobalVariable.userNameStr, "Null");
        String userEmail = prefs.getString(GlobalVariable.userEmailStr, "Null");
        URL profilePicUrl = null;
        try {
            profilePicUrl = new URL(prefs.getString("userImgUrl", "FailLoad"));
            Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
            profilePhoto.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int temp=userEmail.indexOf('@');
        profileUserId.setText(userEmail.substring(0,temp));
        profileUserName.setText(userName);
        profileUserName.setTextColor(getResources().getColor(R.color.Black));

        profileSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
            }
        });

        logoutBotton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                profileUserName.setText("");
                                profilePhoto.setImageResource(R.drawable.ic_person_black_24dp);
                                logoutPlatform();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setTitle("登出？");
                alertDialog.setMessage("您是否要登出呢");
                alertDialog.show();
            }
        });
    }

    public void logoutPlatform(){
        switch (prefs.getString(GlobalVariable.userPlatformStr, "Null")) {
            case "0":
                break;
            case "1":
                break;
            case "2":
                LoginManager.getInstance().logOut();
                cleanValue();
                break;
            case "3":
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(getApplicationContext(), "Logged Out", Toast.LENGTH_SHORT).show();
                        cleanValue();
                    }
                });
                break;
            case "Null":
                Toast.makeText(getActivity(), "has error", Toast.LENGTH_SHORT).show();
        }
    }

    public void cleanValue(){
        prefs.edit().clear().apply();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onStart() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        super.onStart();
    }
}
