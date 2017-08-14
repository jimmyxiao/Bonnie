package com.sctw.bonniedraw.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.sctw.bonniedraw.AppDelegate;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.constant.RequestCode;
import com.sctw.bonniedraw.fragment.DrawerFragment;

public class MainActivity extends AppCompatActivity implements DrawerFragment.Delegate {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String[] permissions;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int grantPermissionsCount = 0;
        if (requestCode == RequestCode.WRITE_EXTERNAL_STORAGE_PERMISSION &&
                this.permissions != null &&
                this.permissions.length == permissions.length &&
                this.permissions.length == grantResults.length) {
            for (int i = 0; i < this.permissions.length; i++)
                if (this.permissions[i].equals(permissions[i]) && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                    grantPermissionsCount++;
            if (grantPermissionsCount != this.permissions.length) {
                Snackbar snackbar = Snackbar.
                        make(findViewById(android.R.id.content), R.string.request_storage_access, Snackbar.LENGTH_LONG).
                        setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(
                                        new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).
                                                setData(Uri.parse("package:" + getPackageName())));
                            }
                        });
                snackbar.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().add(R.id.drawer, new DrawerFragment()).commit();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.open_menu,
                R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    private void disableHomeIndicator() {
        if (actionBarDrawerToggle.isDrawerIndicatorEnabled()) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    actionBarDrawerToggle.onDrawerSlide(drawerLayout, (Float) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.start();
        }
    }

    private void enableHomeIndicator() {
        if (!actionBarDrawerToggle.isDrawerIndicatorEnabled()) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    actionBarDrawerToggle.onDrawerSlide(drawerLayout, (Float) valueAnimator.getAnimatedValue());
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            valueAnimator.setInterpolator(new DecelerateInterpolator());
            valueAnimator.start();
        }
    }

    @Override
    public void drawerFragmentUpdateApp(final View sender) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permissions, RequestCode.WRITE_EXTERNAL_STORAGE_PERMISSION);
            return;
        }
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                drawerLayout.removeDrawerListener(this);
                if (AppDelegate.hasNetworkAccess()) {
                    if (AppDelegate.downloadAppUpdate())
                        Toast.makeText(MainActivity.this, R.string.downloading, Toast.LENGTH_SHORT).show();
                } else {
                    Snackbar snackbar = Snackbar.
                            make(findViewById(android.R.id.content), R.string.request_network_access, Snackbar.LENGTH_LONG).
                            setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                                }
                            });
                    snackbar.show();
                }
            }
        });
        drawerLayout.closeDrawers();
    }
}
