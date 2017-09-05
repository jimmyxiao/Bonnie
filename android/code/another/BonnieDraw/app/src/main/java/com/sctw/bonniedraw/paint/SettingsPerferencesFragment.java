
package com.sctw.bonniedraw.paint;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.sctw.bonniedraw.R;


public class SettingsPerferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("畫板設定");
        addPreferencesFromResource(R.xml.settings_perference);
    }

}
