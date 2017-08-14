package com.sctw.bonniedraw.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.sctw.bonniedraw.Logger;
import com.sctw.bonniedraw.R;
import com.sctw.bonniedraw.constant.Preference;

public class RegistrationIntentService extends IntentService {
    private static final String TAG = "RegistrationIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            synchronized (TAG) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                sharedPreferences.edit().putString(Preference.GCM_TOKEN, token).apply();
                sendRegistrationToServer(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendRegistrationToServer(String token) {
        Logger.d("GCM Registration Token: " + token);
    }
}
