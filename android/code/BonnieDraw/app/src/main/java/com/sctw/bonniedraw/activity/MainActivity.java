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
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.sctw.bonniedraw.bean.TagListBean;
import com.sctw.bonniedraw.fragment.CollectionFragment;
import com.sctw.bonniedraw.fragment.HomeAndHotFragment;
import com.sctw.bonniedraw.fragment.NoticeFragment;
import com.sctw.bonniedraw.fragment.ProfileFragment;
import com.sctw.bonniedraw.fragment.ProfileSettingFragment;
import com.sctw.bonniedraw.utility.BottomNavigationViewEx;
import com.sctw.bonniedraw.utility.ConnectJson;
import com.sctw.bonniedraw.utility.ExtraUtil;
import com.sctw.bonniedraw.utility.FullScreenDialog;
import com.sctw.bonniedraw.utility.GlideAppModule;
import com.sctw.bonniedraw.utility.GlobalVariable;
import com.sctw.bonniedraw.utility.OkHttpUtil;
import com.sctw.bonniedraw.utility.PxDpConvert;
import com.sctw.bonniedraw.widget.ToastUtil;
import com.twitter.sdk.android.core.TwitterCore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements SideBarAdapter.SideBarClickListener {
    private DrawerLayout mDrawerLayout;
    private ImageButton mImgBtnBack, mImgBtnPaint;
    private TextView mTvVersion, mTvDownload;
    private BottomNavigationViewEx mBottomNavigationViewEx;
    private RelativeLayout mNavigationView;
    private RecyclerView mRv;
    private SideBarAdapter mAdapter;
    private CircleImageView mImgHeaderPhoto;
    private TextView mTextViewHeaderText;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private GoogleApiClient mGoogleApiClient;
    private SharedPreferences prefs;
    private int miFn = 0;
    private boolean mbFirst = false;
    private String mSquery = "";

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

    private void changeFragmentWithBundle(Fragment fragment, int function, String query) {
        Bundle bundle = new Bundle();
        bundle.putInt("page", function);
        bundle.putString("query", query);
        fragment.setArguments(bundle);
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout_actitivy, fragment);
        fragmentTransaction.commit();
    }

    //initTwitter Btn,Toolbar,BottomNav
    private void init() {
        // findview by id
        prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_actitivy_drawlayout);
        mNavigationView = (RelativeLayout) findViewById(R.id.sidebarView);
        mImgBtnPaint = (ImageButton) findViewById(R.id.imgBtn_paint_start);
        mImgBtnPaint.bringToFront();
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
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, getString(R.string.sidebar_class_hot)));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, getString(R.string.sidebar_class_new)));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, getString(R.string.sidebar_class_my)));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, ""));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, ""));
        list.add(new SidebarBean(R.drawable.left_menu_icon_1, ""));
        list.add(new SidebarBean(R.drawable.collect_ic_off, getString(R.string.my_collection)));
        list.add(new SidebarBean(R.drawable.menu_ic_account, getString(R.string.account_setting)));
        list.add(new SidebarBean(R.drawable.menu_ic_out, getString(R.string.logout)));
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
                        if (mBottomNavigationViewEx.getCurrentItem() == 0 && mbFirst) {
                            callFragmentToTop();
                            return false;
                        }
                        mbFirst = true;
                        if (miFn == 0) miFn = 2;
                        changeFragmentWithBundle(new HomeAndHotFragment(), miFn, mSquery);
                        return true;
                    case R.id.ic_btn_hot:
                        if (mBottomNavigationViewEx.getCurrentItem() == 1) {
                            callFragmentToTop();
                            return false;
                        }
                        changeFragmentWithBundle(new HomeAndHotFragment(), miFn, mSquery);
                        return true;
                    case R.id.ic_btn_notice:
                        if (mBottomNavigationViewEx.getCurrentItem() == 3) return false;
                        changeFragment(new NoticeFragment());
                        return true;
                    case R.id.ic_btn_user:
                        if (mBottomNavigationViewEx.getCurrentItem() == 4) return false;
                        changeFragment(new ProfileFragment());
                        return true;
                    default:
                        return false;
                }
            }
        });
        mBottomNavigationViewEx.setCurrentItem(0);
        getTagList();
    }

    private void getTagList() {
        OkHttpClient mOkHttpClient = OkHttpUtil.getInstance();
        Request request = ConnectJson.getTagList(prefs, 0);
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.createToastWindow(MainActivity.this, getString(R.string.connection_failed), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
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
                                showTagList(responseJSON.getJSONArray("tagList"));
                            } else {
                                ToastUtil.createToastWindow(MainActivity.this, getString(R.string.login_fail), PxDpConvert.getSystemHight(getApplicationContext()) / 3);
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

    private void showTagList(JSONArray jsonArray) {
        List<TagListBean> list = new ArrayList<>();
        try {
            for (int x = 0; x < jsonArray.length(); x++) {
                TagListBean bean = new TagListBean(
                        jsonArray.getJSONObject(x).getInt("tagId"),
                        jsonArray.getJSONObject(x).getString("tagName"),
                        jsonArray.getJSONObject(x).getString("tagEngName"),
                        jsonArray.getJSONObject(x).getInt("tagOrder"),
                        jsonArray.getJSONObject(x).getString("countryCode")
                );
                list.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!list.isEmpty()) {
            if (list.size() > 2) {
                for (int x = 0; x < 3; x++) {
                    mAdapter.chagneTitle(3 + x, list.get(x).getTagName());
                }
            } else {
                for (int x = 0; x < 2; x++) {
                    mAdapter.chagneTitle(3 + x, list.get(x).getTagName());
                }
            }
        }
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
        final FullScreenDialog dialog = new FullScreenDialog(this, R.layout.dialog_base);
        FrameLayout layout = dialog.findViewById(R.id.frameLayout_dialog_base);
        TextView title = dialog.findViewById(R.id.textView_dialog_base_title);
        TextView msg = dialog.findViewById(R.id.textView_dialog_base_msg);
        Button yes = dialog.findViewById(R.id.btn_dialog_base_yes);
        Button no = dialog.findViewById(R.id.btn_dialog_base_no);
        title.setText(getString(R.string.logout_title));
        msg.setText(getString(R.string.logout_msg));
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        no.setVisibility(View.VISIBLE);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutPlatform();
                dialog.dismiss();
            }
        });
        dialog.show();
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
        if (prefs.getInt(GlobalVariable.USER_THIRD_PLATFORM_STR, 0) == 0) {
            final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage(R.string.please_login);
            dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            dialog.show();
        } else {
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
            if (event != null && !event.isEmpty()) mBottomNavigationViewEx.setCurrentItem(3);
        }
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
        mDrawerLayout.closeDrawers();
        switch (position) {
            case 0:
                mbFirst = false;
                miFn = 2;
                mBottomNavigationViewEx.setCurrentItem(0);
                break;
            case 1:
                mbFirst = false;
                miFn = 4;
                mBottomNavigationViewEx.setCurrentItem(0);
                break;
            case 2:
                mbFirst = false;
                miFn = 5;
                mBottomNavigationViewEx.setCurrentItem(0);
                //我的畫作
                break;
            case 3:
                if(!mAdapter.getTagName(3).isEmpty()){
                    mbFirst=false;
                    miFn=8;
                    mSquery=mAdapter.getTagName(3);
                    mBottomNavigationViewEx.setCurrentItem(0);
                }
                break;
            case 4:
                if(!mAdapter.getTagName(4).isEmpty()){
                    mbFirst=false;
                    miFn=8;
                    mSquery=mAdapter.getTagName(4);
                    mBottomNavigationViewEx.setCurrentItem(0);
                }
                break;
            case 5:
                if(!mAdapter.getTagName(5).isEmpty()){
                    mbFirst=false;
                    miFn=8;
                    mSquery=mAdapter.getTagName(5);
                    mBottomNavigationViewEx.setCurrentItem(0);
                }
                break;
            case 6:
                //收藏
                CollectionFragment collectionFragment = new CollectionFragment();
                collectionFragment.show(getSupportFragmentManager(), "TAG");
                break;
            case 7:
                //帳號設定
                ProfileSettingFragment profileSettingFragment = new ProfileSettingFragment();
                profileSettingFragment.show(getSupportFragmentManager(), "TAG");
                break;
            case 8:
                logout();
                break;
        }
    }

    public void callFragmentToTop() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        // (if you're not using the support library)
        // FragmentManager fragmentManager = getFragmentManager();
        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment != null && fragment.isVisible() && fragment instanceof HomeAndHotFragment) {
                ((HomeAndHotFragment) fragment).showViewToTop();
            }
        }
    }
}
