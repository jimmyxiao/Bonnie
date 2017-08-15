
package com.sctw.bonnie.paint;
import com.sctw.bonnie.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;



public class SettingsPerferencesActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings_perference);
    }

}
