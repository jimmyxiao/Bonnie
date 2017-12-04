package com.sctw.bonniedraw.utility;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Fatorin on 2017/12/1.
 */

public class NotificationIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        String refreshId = FirebaseInstanceId.getInstance().getId();
        saveToken(refreshToken, refreshId);
    }

    private void saveToken(String refreshToken, String refreshId) {
        SharedPreferences prefs = getSharedPreferences(GlobalVariable.MEMBER_PREFS, MODE_PRIVATE);
        prefs.edit()
                .putString(GlobalVariable.USER_FCM_TOKEN_STR, refreshToken)
                .putString(GlobalVariable.USER_DEVICE_ID_STR, refreshId)
                .apply();
    }


}
