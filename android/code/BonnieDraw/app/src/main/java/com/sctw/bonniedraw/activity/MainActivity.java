package com.sctw.bonniedraw.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
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
import com.sctw.bonniedraw.fragment.FollowFragment;
import com.sctw.bonniedraw.fragment.HomeFragment;
import com.sctw.bonniedraw.fragment.LikeFragment;
import com.sctw.bonniedraw.fragment.ProfileFragment;
import com.sctw.bonniedraw.paint.PaintActivity;
import com.sctw.bonniedraw.utility.BottomNavigationViewEx;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.twitter.sdk.android.core.TwitterCore;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    ImageButton icBtnBack, icBtnPaint, icBtnHome, icBtnLike, icBtnNotice, icBtnUser;
    BottomNavigationViewEx mBottomNavigationViewEx;
    NavigationView navigationView;
    View headerView;
    ImageView headerPhoto;
    TextView headerText;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    GoogleApiClient mGoogleApiClient;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        creatteProfileInfo();
    }

    public void changeFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_actitivy_layout, fragment);
        fragmentTransaction.commit();
    }

    //init Btn,Toolbar,BottomNav
    void init() {
        // findview by id
        drawerLayout = (DrawerLayout) findViewById(R.id.main_actitivy_drawlayout);
        prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        headerView = navigationView.getHeaderView(0);
        headerPhoto = (ImageView) headerView.findViewById(R.id.header_user_photo);
        headerText = (TextView) headerView.findViewById(R.id.header_user_name);
        icBtnBack = (ImageButton) headerView.findViewById(R.id.header_btn_back);
        icBtnPaint = (ImageButton) findViewById(R.id.ic_btn_paint);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_btn_out:
                        logout();
                        break;
                }
                return true;
            }
        });

        icBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        icBtnPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, PaintActivity.class);
                startActivity(it);
            }
        });

        mBottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.cotrol_panel_layout);
        mBottomNavigationViewEx.enableShiftingMode(false);
        mBottomNavigationViewEx.enableItemShiftingMode(false);
        mBottomNavigationViewEx.setTextVisibility(false);
        fragmentManager = getSupportFragmentManager();
        mBottomNavigationViewEx.getBottomNavigationItemView(2).setBackgroundColor(getResources().getColor(R.color.Transparent));
        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_btn_home:
                        changeFragment(new HomeFragment());
                        return true;
                    case R.id.ic_btn_like:
                        changeFragment(new LikeFragment());
                        return true;
                    case R.id.ic_btn_notice:
                        changeFragment(new FollowFragment());
                        return true;
                    case R.id.ic_btn_user:
                        changeFragment(new ProfileFragment());
                        return true;
                    default:
                        return false;
                }
            }
        });

        mBottomNavigationViewEx.setCurrentItem(0);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    void creatteProfileInfo() {
        String userName = prefs.getString(GlobalVariable.userNameStr, "Null");
        headerText.setText(userName);
        if (!prefs.getString("userImgUrl", "").isEmpty()) {
            try {
                URL profilePicUrl = new URL(prefs.getString("userImgUrl", "FailLoad"));
                Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                headerPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutPlatform();
                        headerPhoto.setBackgroundColor(Color.BLACK);
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

    public void logoutPlatform() {
        switch (prefs.getString(GlobalVariable.userPlatformStr, "null")) {
            case "0":
                break;
            case "1":
                cleanValue();
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
            case "4":
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                cleanValue();
                break;
            case "null":
                Toast.makeText(this, "has error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void cleanValue() {
        prefs.edit().clear().apply();
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawers();
        } else if (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStack();
        } else if (mBottomNavigationViewEx.getCurrentItem() != 0) {
            mBottomNavigationViewEx.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
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
