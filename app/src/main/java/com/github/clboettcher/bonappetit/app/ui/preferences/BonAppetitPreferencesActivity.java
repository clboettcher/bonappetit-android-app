package com.github.clboettcher.bonappetit.app.ui.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.data.preferences.ServerConfigChangedEvent;
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

    private final Set<String> serverConfigKeys = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BonAppetitApplication) getApplication()).getDiComponent().inject(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new BonAppetitPreferencesFragment())
                .commit();
        Log.i(TAG, "Registering for shared preferences changed events");

        serverConfigKeys.add(getString(R.string.prefs_key_server_host_name));
        serverConfigKeys.add(getString(R.string.prefs_key_server_port));
        serverConfigKeys.add(getString(R.string.prefs_key_server_context_path));
        serverConfigKeys.add(getString(R.string.prefs_key_username));
        serverConfigKeys.add(getString(R.string.prefs_key_password));

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (serverConfigKeys.contains(key)) {
            Log.i(TAG, String.format("Shared preferences change detected for key '%s' " +
                            "which is part of the server config. Posting %s.",
                    key,
                    ServerConfigChangedEvent.class.getSimpleName()));
            eventBus.post(new ServerConfigChangedEvent());
        } else {
            Log.i(TAG, String.format("Ignoring shared preferences change for key '%s'", key));
        }
    }
}