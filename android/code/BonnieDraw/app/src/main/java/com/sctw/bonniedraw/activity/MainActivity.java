package com.sctw.bonniedraw.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.fragment.FollowFragment;
import com.sctw.bonniedraw.fragment.HomeFragment;
import com.sctw.bonniedraw.fragment.LikeFragment;
import com.sctw.bonniedraw.fragment.ProfileFragment;
import com.sctw.bonniedraw.paint.PaintActivity;
import com.sctw.bonniedraw.utility.BottomNavigationViewEx;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.LoadImageApp;
import com.twitter.sdk.android.core.TwitterCore;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    DrawerLayout mDrawerLayout;
    ImageButton mImgBtnBack, mImgBtnPaint;
    BottomNavigationViewEx mBottomNavigationViewEx;
    NavigationView mNavigationView;
    View mHeaderView;
    CircleImageView mImgHeaderPhoto;
    TextView mTextViewHeaderText;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    GoogleApiClient mGoogleApiClient;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        createProfileInfo();
    }

    public void changeFragment(Fragment fragment) {
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
        fragmentTransaction.commit();
    }

    //init Btn,Toolbar,BottomNav
    void init() {
        // findview by id
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_actitivy_drawlayout);
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mNavigationView = (NavigationView) findViewById(R.id.sidebarView);
        mHeaderView = mNavigationView.getHeaderView(0);
        mImgHeaderPhoto = (CircleImageView) mHeaderView.findViewById(R.id.header_user_photo);
        mTextViewHeaderText = (TextView) mHeaderView.findViewById(R.id.header_user_name);
        mImgBtnBack = (ImageButton) mHeaderView.findViewById(R.id.header_btn_back);
        mImgBtnPaint = (ImageButton) findViewById(R.id.imgBtn_paint_start);
        mBottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomView_layout);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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

        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
            }
        });

        mImgBtnPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, PaintActivity.class);
                startActivity(it);
            }
        });

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
    }

    void createProfileInfo() {
        String userName = prefs.getString(GlobalVariable.USER_NAME_STR, "Null");
        mTextViewHeaderText.setText(userName);
        String url = GlobalVariable.API_LINK_GET_FILE + prefs.getString(GlobalVariable.USER_IMG_URL_STR, "");
        ImageLoader.getInstance().displayImage(url, mImgHeaderPhoto, LoadImageApp.optionsUserImg);

    }

    void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.public_commit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutPlatform();
                        mImgHeaderPhoto.setBackgroundColor(Color.BLACK);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.public_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setTitle(getString(R.string.logout_title));
        alertDialog.setMessage(getString(R.string.logout_msg));
        alertDialog.show();
    }

    public void logoutPlatform() {
        switch (prefs.getString(GlobalVariable.USER_PLATFORM_STR, "null")) {
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
        for (Fragment frag : fragmentManager.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return;
                }
            }
        }
        if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
            mDrawerLayout.closeDrawers();
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
