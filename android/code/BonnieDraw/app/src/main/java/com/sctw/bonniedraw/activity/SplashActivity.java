package com.sctw.bonniedraw.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.sctw.bonniedraw.constant.Preference;
import com.sctw.bonniedraw.service.RegistrationIntentService;
import com.sctw.bonniedraw.service.SyncAdapter;


/**
 * Created by Professor on 12/10/15.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sharedPreferences.contains(Preference.RELAUNCH)) {
            SyncAdapter.initializeSyncAdapter(null, null);
            sharedPreferences.edit().putBoolean(Preference.RELAUNCH, true).apply();
        }
        if (!sharedPreferences.contains(Preference.GCM_TOKEN))
            startService(new Intent(this, RegistrationIntentService.class));
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
