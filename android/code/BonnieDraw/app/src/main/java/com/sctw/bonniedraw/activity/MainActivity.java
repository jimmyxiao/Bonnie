package com.sctw.bonniedraw.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.adapter.SideBarAdapter;
import com.sctw.bonniedraw.bean.SidebarBean;
import com.sctw.bonniedraw.fragment.HomeAndHotFragment;
import com.sctw.bonniedraw.fragment.NoticeFragment;
import com.sctw.bonniedraw.fragment.ProfileFragment;
import com.sctw.bonniedraw.paint.PaintActivity;
import com.sctw.bonniedraw.utility.BottomNavigationViewEx;
import com.sctw.bonniedraw.utility.ExtraUtil;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements SideBarAdapter.SideBarClickListener {
    DrawerLayout mDrawerLayout;
    ImageButton mImgBtnBack, mImgBtnPaint;
    TextView mTvVersion, mTvDownload;
    BottomNavigationViewEx mBottomNavigationViewEx;
    RelativeLayout mNavigationView;
    RecyclerView mRv;
    SideBarAdapter mAdapter;
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
    }

    private void changeFragment(Fragment fragment) {
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
        fragmentTransaction.commit();
    }

    private void changeFragmentWithBundle(Fragment fragment, int num) {
        Bundle bundle = new Bundle();
        bundle.putInt("page", num);
        fragment.setArguments(bundle);
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
        fragmentTransaction.commit();
    }

    //init Btn,Toolbar,BottomNav
    private void init() {
        // findview by id
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_actitivy_drawlayout);
        mNavigationView = (RelativeLayout) findViewById(R.id.sidebarView);
        mImgBtnPaint = (ImageButton) findViewById(R.id.imgBtn_paint_start);
        mImgHeaderPhoto = (CircleImageView) mNavigationView.findViewById(R.id.header_user_photo);
        mTextViewHeaderText = (TextView) mNavigationView.findViewById(R.id.header_user_name);
        mImgBtnBack = (ImageButton) mNavigationView.findViewById(R.id.header_btn_back);
        mBottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomView_layout);
        mTvVersion = (TextView) mNavigationView.findViewById(R.id.textView_version_name);
        mTvDownload = (TextView) mNavigationView.findViewById(R.id.textView_download);
        mTvVersion.setText("當前版本 " + ExtraUtil.getVersionName(this));
        mTvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ie = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.download_html)));
                startActivity(ie);
            }
        });
        mRv = findViewById(R.id.recyclerView_sidebar);
        mRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ArrayList<SidebarBean> list = new ArrayList<>();
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, "熱門畫作"));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, "最新畫作"));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, "我的畫作"));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, "類別一"));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, "類別二"));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, "類別三"));
        list.add(new SidebarBean(R.drawable.collect_ic_off, "我的收藏"));
        list.add(new SidebarBean(R.drawable.menu_ic_account, "帳號設定"));
        list.add(new SidebarBean(R.drawable.menu_ic_out, "登出"));
        mAdapter = new SideBarAdapter(this, list, this);
        mRv.setAdapter(mAdapter);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        createProfileInfo();
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                createProfileInfo();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mImgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawers();
            }
        });

        mImgBtnPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPaint();
            }
        });

        mBottomNavigationViewEx.enableShiftingMode(false);
        mBottomNavigationViewEx.enableItemShiftingMode(false);
        mBottomNavigationViewEx.setTextVisibility(false);
        fragmentManager = getSupportFragmentManager();
        mBottomNavigationViewEx.enableAnimation(false);
        mBottomNavigationViewEx.getBottomNavigationItemView(2).setBackgroundColor(getResources().getColor(R.color.Transparent));
        mBottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.ic_btn_home:
                        changeFragmentWithBundle(new HomeAndHotFragment(), 2);
                        return true;
                    case R.id.ic_btn_hot:
                        changeFragmentWithBundle(new HomeAndHotFragment(), 1);
                        return true;
                    case R.id.ic_btn_notice:
                        changeFragment(new NoticeFragment());
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

    private void startPaint() {
        Intent it = new Intent();
        it.setClass(MainActivity.this, PaintActivity.class);
        startActivity(it);
    }

    private void createProfileInfo() {
        String userName = prefs.getString(GlobalVariable.USER_NAME_STR, "Null");
        mTextViewHeaderText.setText(userName);
        String path;
        if (prefs.getString(GlobalVariable.USER_IMG_URL_STR, "").equals("null")) {
            path = "";
        } else {
            path = GlobalVariable.API_LINK_GET_FILE + prefs.getString(GlobalVariable.USER_IMG_URL_STR, "");
        }
        Glide.with(this).load(path).apply(GlideAppModule.getUserOptions()).into(mImgHeaderPhoto);
    }

    private void logout() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.commit),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logoutPlatform();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setTitle(getString(R.string.logout_title));
        alertDialog.setMessage(getString(R.string.logout_msg));
        alertDialog.show();
    }

    private void logoutPlatform() {
        switch (prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0)) {
            case GlobalVariable.EMAIL_LOGIN:
                cleanValue();
                break;
            case GlobalVariable.THIRD_LOGIN_FACEBOOK:
                LoginManager.getInstance().logOut();
                cleanValue();
                break;
            case GlobalVariable.THIRD_LOGIN_GOOGLE:
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        cleanValue();
                    }
                });
                break;
            case GlobalVariable.THIRD_LOGIN_TWITTER:
                TwitterCore.getInstance().getSessionManager().clearActiveSession();
                cleanValue();
                break;
            case 0:
                Toast.makeText(this, "has error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void cleanValue() {
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
        //接收推播
        Intent intent = getIntent();
        String event = intent.getStringExtra("evnet");
        Log.d("FCM", "event:" + event);
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

    @Override
    public void onClick(int position) {
        if (position == 8) {
            logout();
        }
    }
}
