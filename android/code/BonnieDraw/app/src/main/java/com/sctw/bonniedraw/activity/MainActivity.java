package com.sctw.bonniedraw.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.fragment.FollowFragment;
import com.sctw.bonniedraw.fragment.HomeFragment;
import com.sctw.bonniedraw.fragment.HotFragment;
import com.sctw.bonniedraw.fragment.ProfileFragment;
import com.sctw.bonniedraw.paint.PaintActivity;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageButton toolbarSearch, icBtnPaint, icBtnHome, icBtnLike, icBtnNotice, icBtnUser;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbarSearch = (ImageButton) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        icBtnPaint = (ImageButton) findViewById(R.id.ic_btn_paint);
        icBtnHome = (ImageButton) findViewById(R.id.ic_btn_home);
        icBtnLike = (ImageButton) findViewById(R.id.ic_btn_like);
        icBtnNotice = (ImageButton) findViewById(R.id.ic_btn_notice);
        icBtnUser = (ImageButton) findViewById(R.id.ic_btn_user);
        fragmentManager = getSupportFragmentManager();
        icBtnHome.setOnTouchListener(iconOnTouch);
        icBtnLike.setOnTouchListener(iconOnTouch);
        icBtnNotice.setOnTouchListener(iconOnTouch);
        icBtnUser.setOnTouchListener(iconOnTouch);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_actitivy_layout, new HomeFragment());
        fragmentTransaction.commit();

        toolbarSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "搜尋", Toast.LENGTH_SHORT).show();
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
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public View.OnTouchListener iconOnTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageButton view = (ImageButton) v;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                    v.invalidate();
                    break;
                }
                case MotionEvent.ACTION_UP:
                    fragmentTransaction = fragmentManager.beginTransaction();
                    switch (v.getId()) {
                        case R.id.ic_btn_home:
                            fragmentTransaction.replace(R.id.main_actitivy_layout, new HomeFragment());
                            break;
                        case R.id.ic_btn_like:
                            fragmentTransaction.replace(R.id.main_actitivy_layout, new HotFragment());
                            break;
                        case R.id.ic_btn_notice:
                            fragmentTransaction.replace(R.id.main_actitivy_layout, new FollowFragment());
                            break;
                        case R.id.ic_btn_user:
                            fragmentTransaction.replace(R.id.main_actitivy_layout, new ProfileFragment());
                            break;
                    }
                    fragmentTransaction.commit();
                    // Your action here on button click
                case MotionEvent.ACTION_CANCEL: {
                    view.getDrawable().clearColorFilter();
                    view.invalidate();
                    break;
                }
            }
            return true;
        }
    };

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
