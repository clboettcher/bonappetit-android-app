package com.github.clboettcher.bonappetit.app.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.application.BonAppetitApplication;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public class BonAppetitPreferencesActivity extends PreferenceActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = BonAppetitPreferencesActivity.class.getName();

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    EventBus eventBus;

    private final Set<String> baseUrlKeys = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BonAppetitApplication) getApplication()).getDiComponent().inject(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new BonAppetitPreferencesFragment())
                .commit();
        Log.i(TAG, "Registering for shared preferences changed events");

        String keyHost = getString(R.string.prefs_key_server_host_name);
        String keyPort = getString(R.string.prefs_key_server_port);
        String keyPath = getString(R.string.prefs_key_server_context_path);

        baseUrlKeys.add(keyHost);
        baseUrlKeys.add(keyPort);
        baseUrlKeys.add(keyPath);

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (baseUrlKeys.contains(key)) {
            Log.i(TAG, String.format("Shared preferences change detected for key '%s' " +
                            "which part of the server base URL. Posting %s.",
                    key,
                    BaseUrlChangedEvent.class.getSimpleName()));
            eventBus.post(new BaseUrlChangedEvent());
        } else {
            Log.i(TAG, String.format("Ignoring shared preferences change for key '%s'", key));
        }
    }
}