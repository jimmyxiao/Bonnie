package com.sctw.bonniedraw.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.fragment.FollowFragment;
import com.sctw.bonniedraw.fragment.HomeFragment;
import com.sctw.bonniedraw.fragment.LikeFragment;
import com.sctw.bonniedraw.fragment.ProfileFragment;
import com.sctw.bonniedraw.paint.PaintActivity;
import com.sctw.bonniedraw.utility.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private ImageButton icBtnPaint, icBtnHome, icBtnLike, icBtnNotice, icBtnUser;
    private BottomNavigationViewEx mBottomNavigationViewEx;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        icBtnPaint = (ImageButton) findViewById(R.id.ic_btn_paint);
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




        icBtnPaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setClass(MainActivity.this, PaintActivity.class);
                startActivity(it);
            }
        });

        mBottomNavigationViewEx.setCurrentItem(0);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public void changeFragment(Fragment fragment) {
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_actitivy_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onBackPressed() {
        if (mBottomNavigationViewEx.getCurrentItem() != 0) {
            mBottomNavigationViewEx.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }
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
