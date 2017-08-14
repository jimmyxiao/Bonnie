package com.sctw.bonniedraw.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.sctw.bonniedraw.AppDelegate;
import com.sctw.bonniedraw.R;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL = 108000;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    public static void initializeSyncAdapter(String username, String password) {
        Resources resources = AppDelegate.getResource();
        String authority = resources.getString(R.string.content_authority);
        Account account = new Account(resources.getString(R.string.app_name), resources.getString(R.string.sync_account_type));
        AccountManager accountManager = AccountManager.get(AppDelegate.getContext());
        accountManager.addAccountExplicitly(account, "", null);
        ContentResolver.setSyncAutomatically(account, authority, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(SYNC_INTERVAL, SYNC_FLEXTIME).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), SYNC_INTERVAL);
    }

    public static void syncImmediately() {
        Resources resources = AppDelegate.getResource();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(new Account(resources.getString(R.string.app_name), resources.getString(R.string.sync_account_type)), AppDelegate.getResource().getString(R.string.content_authority), bundle);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
    }
}
