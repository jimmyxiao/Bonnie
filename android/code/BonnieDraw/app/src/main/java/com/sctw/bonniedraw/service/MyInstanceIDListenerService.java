package com.sctw.bonniedraw.service;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {
    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token.
        startService(new Intent(this, RegistrationIntentService.class));
    }
}
