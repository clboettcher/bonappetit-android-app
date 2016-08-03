package com.github.clboettcher.bonappetit.app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.github.clboettcher.bonappetit.app.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.Constants;
import com.github.clboettcher.bonappetit.app.event.BaseUrlChangedEvent;
import com.github.clboettcher.bonappetit.app.fragment.BonAppetitPreferencesFragment;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class BonAppetitPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    EventBus eventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BonAppetitApplication) getApplication()).getDiComponent().inject(this);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new BonAppetitPreferencesFragment())
                .commit();
        Log.i(Constants.TAG, "Registering for shared preferences changed events");
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("baseUrl".equals(key)) {
            Log.i(Constants.TAG, String.format("Base URL change detected. Posting %s.",
                    BaseUrlChangedEvent.class.getSimpleName()));
            eventBus.post(new BaseUrlChangedEvent());
        } else {
            Log.i(Constants.TAG, String.format("Ignoring shared preferences change for key '%s'", key));
        }
    }
}