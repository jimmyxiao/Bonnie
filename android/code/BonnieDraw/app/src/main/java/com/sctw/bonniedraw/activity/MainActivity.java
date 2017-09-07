package com.sctw.bonniedraw.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.fragment.DescriptionFragment;
import com.sctw.bonniedraw.fragment.HomeFragment;
import com.sctw.bonniedraw.fragment.HotFragment;
import com.sctw.bonniedraw.fragment.LibraryFragment;
import com.sctw.bonniedraw.fragment.LoginFragment;
import com.sctw.bonniedraw.fragment.SettingFragment;
import com.sctw.bonniedraw.paint.PaintActivity;
import com.sctw.bonniedraw.utility.GlobalVariable;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    ImageButton backButton;
    ActionBarDrawerToggle actionBarDrawerToggle;
    FragmentTransaction fragmentTransaction;
    public static NavigationView navigationView;
    public static TextView userName, userEmail;
    public static ImageView profile;
    View navHeader;
    SharedPreferences prefs;
    public static GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backButton = (ImageButton) findViewById(R.id.backButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        //設定首頁
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container, new HomeFragment());
        fragmentTransaction.commit();
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(navLister);
        navHeader = navigationView.getHeaderView(0);

        userName = navHeader.findViewById(R.id.userName);
        userEmail = navHeader.findViewById(R.id.userEmail);
        profile = navHeader.findViewById(R.id.profile);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        prefs = getSharedPreferences("userInfo", MODE_PRIVATE);
        checkLoginInfo(prefs);
        googlePlusResult();
    }

    public void LoginOut(int platform) {
        switch (platform) {
            case 0:
                //Email
                break;
            case 1:
                //FB
                LoginManager.getInstance().logOut();
                LoginOutSuccess();
                break;
            case 2:
                //GOOGLE
                Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                LoginOutSuccess();
                            }
                        });
                break;

        }
    }

    public void LoginOutSuccess() {
        // if successful,clear
        drawerLayout.closeDrawers();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, new HomeFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        openMenu(false);
        navigationView.getMenu().getItem(0).setChecked(true);
        Snackbar.make(drawerLayout, "登出成功", Snackbar.LENGTH_SHORT).show();
    }

    private void checkLoginInfo(SharedPreferences prefs) {
        if (prefs != null && !prefs.getString(GlobalVariable.userTokenStr, "").isEmpty()) {
            userName.setText(prefs.getString(GlobalVariable.userNameStr, "FailLoad"));
            userEmail.setText(prefs.getString(GlobalVariable.userEmailStr, "FailLoad"));
            try {
                URL profilePicUrl = new URL(prefs.getString("userImgUrl", "FailLoad"));
                Bitmap bitmap = BitmapFactory.decodeStream(profilePicUrl.openConnection().getInputStream());
                profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            openMenu(true);
        } else {
            Log.e("Not Found", "No LoginInfo");
        }

    }

    public void checkLogin(View view) {
        //if登入 else 產生 alert 登出i
        if (prefs.getString("userToken", "").isEmpty()) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, new LoginFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            uncheckNavigationView();
            drawerLayout.closeDrawers();
        } else {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "確認",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            userName.setText(R.string.not_login);
                            userEmail.setText(R.string.please_click_icon_login);
                            profile.setImageResource(R.drawable.ic_person_black_72dp);
                            LoginOut(Integer.parseInt(prefs.getString(GlobalVariable.userPlatformStr, "error")));
                            prefs.edit().clear().apply();
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
    }

    public NavigationView.OnNavigationItemSelectedListener navLister = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.menu_home:
                    fragmentTransaction.replace(R.id.main_container, new HomeFragment());
                    item.setChecked(true);
                    break;

                case R.id.menu_libray:
                    fragmentTransaction.replace(R.id.main_container, new LibraryFragment());
                    item.setChecked(true);
                    break;

                case R.id.menu_paint:
                    Intent it=new Intent();
                    it.setClass(MainActivity.this, PaintActivity.class);
                    startActivity(it);
                    break;

                case R.id.menu_setting:
                    fragmentTransaction.replace(R.id.main_container, new SettingFragment());
                    item.setChecked(true);
                    break;

                case R.id.menu_description:
                    fragmentTransaction.replace(R.id.main_container, new DescriptionFragment());
                    item.setChecked(true);
                    break;

                case R.id.menu_hot:
                    fragmentTransaction.replace(R.id.main_container, new HotFragment());
                    item.setChecked(true);
                    break;
            }
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            drawerLayout.closeDrawers();
            return false;
        }
    };

    public static void uncheckNavigationView() {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }

    public void setBackButton(View view) {
        drawerLayout.closeDrawers();
    }

    //登入啟動左邊面板
    public static void openMenu(boolean select) {
        if (select) {
            navigationView.getMenu().findItem(R.id.menu_libray).setVisible(true);
            navigationView.getMenu().findItem(R.id.menu_paint).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.menu_libray).setVisible(false);
            navigationView.getMenu().findItem(R.id.menu_paint).setVisible(false);
        }
    }

    public SharedPreferences.OnSharedPreferenceChangeListener prefsListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            Log.d("sharedPreferences", s);
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

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(R.id.drawer_layout)) {
            drawerLayout.closeDrawers();
        } else {
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onResume() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).registerOnSharedPreferenceChangeListener(prefsListener);
        super.onResume();
    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).unregisterOnSharedPreferenceChangeListener(prefsListener);
        super.onPause();
    }

}
