/*
 * Copyright (c) 2016 Claudius Boettcher (pos.bonappetit@gmail.com)
 *
 * This file is part of BonAppetit. BonAppetit is an Android based
 * Point-of-Sale client-server application for small restaurants.
 *
 * BonAppetit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonAppetit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BonAppetit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.clboettcher.bonappetit.app.ui.preferences;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerDao;
import com.github.clboettcher.bonappetit.app.data.order.OrdersResource;
import com.github.clboettcher.bonappetit.app.data.preferences.ServerConfigChangedEvent;
import com.github.clboettcher.bonappetit.app.data.preferences.TestDataSwitchedEvent;
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

    @Inject
    OrdersResource ordersResource;

    @Inject
    CustomerDao customerDao;

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
        } else if (getString(R.string.prefs_key_use_test_data).equals(key)) {
            boolean useTestData = sharedPreferences.getBoolean(getString(R.string.prefs_key_use_test_data), false);
            Log.i(TAG, String.format("Preference value for key '%s' changed to %s",
                    getString(R.string.prefs_key_use_test_data),
                    useTestData));
            Log.i(TAG, "Test data usage switched. Clearing all orders and customer.");
            ordersResource.deleteAllOrders();
            customerDao.clear();
            this.eventBus.post(new TestDataSwitchedEvent());
        } else {
            Log.i(TAG, String.format("Ignoring shared preferences change for key '%s'", key));
        }
    }
}