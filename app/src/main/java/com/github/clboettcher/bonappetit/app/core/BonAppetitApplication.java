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
package com.github.clboettcher.bonappetit.app.core;

import android.app.Application;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.data.menu.event.PerformMenuUpdateEvent;
import com.github.clboettcher.bonappetit.app.data.preferences.ServerConfigChangedEvent;
import com.github.clboettcher.bonappetit.app.data.preferences.TestDataSwitchedEvent;
import com.github.clboettcher.bonappetit.app.data.staff.event.PerformStaffMembersUpdateEvent;
import org.greenrobot.eventbus.*;

import javax.inject.Inject;

/**
 * The custom {@link Application} subclass for the bonappetit app.
 * <p/>
 * The custom application subclass is responsible for
 * - Initialization of the dependency injection framework (dagger)
 * - Instantiate beans manually that are not injected anywhere. Otherwise
 * they are not created by the di framework. Such beans are typically invoked
 * via event notifications over the event bus.
 * - Handle global events. E.g. the event that is triggered when there is no subscriber
 * for an event (see {@link NoSubscriberEvent}).
 */
public class BonAppetitApplication extends Application {

    private static final String TAG = BonAppetitApplication.class.getName();

    /**
     * Whether the application shows debug toasts. This can be helpful to quickly debug
     * errors. It should be set to false in release builds obviously.
     */
    public static final boolean DEBUG_TOASTS_ENABLED = true;

    @Inject
    EventBus eventBus;

    @Inject
    ConfigProvider configProvider;

    private DiComponent diComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, String.format("%s created. Initializing DI framework.",
                this.getClass().getSimpleName()));

        // Set default values of preferences
        // TODO: move to the actual main activity when this activity is deleted.
        Log.i(TAG, "Setting default preferences from R.xml.preferences");
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        this.diComponent = DaggerDiComponent
                .builder()
                .diModule(new DiModule(this))
                .build();

        // Invoke components that are not injected anywhere manually.
        // Otherwise they are not instantiated.
        this.diComponent.staffMembersService();
        this.diComponent.menusService();
        this.diComponent.menuRepository();
        this.diComponent.staffMembersRepository();

        // Inject dependencies into this class.
        this.diComponent.inject(this);

        // Register for global events
        this.eventBus.register(this);

        // Trigger data update from the server to minimize waiting.
        this.fetchDataFromServer();
    }

    public DiComponent getDiComponent() {
        return diComponent;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoSubscriberEvent(NoSubscriberEvent event) {
        String msg = String.format("No subscriber for event: %s", event.originalEvent);
        Log.i(TAG, msg);
        if (this.configProvider.displayDebugMessages()) {
            Toast.makeText(this, msg,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscriberExceptionEvent(SubscriberExceptionEvent event) {
        String errorMsg = String.format("ERROR: Exception thrown inside @Subscribe method: %s", event.throwable);
        Log.e(TAG, errorMsg);
        if (this.configProvider.displayDebugMessages()) {
            Toast.makeText(this, errorMsg,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe
    public void onServerConfigChanged(ServerConfigChangedEvent event) {
        // A server config change means usually that we need to retry to fetch the data.
        this.fetchDataFromServer();
    }

    @Subscribe
    public void onTestDataSwitched(TestDataSwitchedEvent event) {
        this.fetchDataFromServer();
    }

    private void fetchDataFromServer() {
        this.eventBus.post(new PerformMenuUpdateEvent());
        this.eventBus.post(new PerformStaffMembersUpdateEvent());
    }
}
