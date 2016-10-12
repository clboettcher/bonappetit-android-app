package com.github.clboettcher.bonappetit.app.ui.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.github.clboettcher.bonappetit.app.R;

public class BonAppetitPreferencesFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
