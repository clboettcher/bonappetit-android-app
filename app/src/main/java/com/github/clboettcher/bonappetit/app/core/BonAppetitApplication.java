package com.github.clboettcher.bonappetit.app.core;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;
import com.github.clboettcher.bonappetit.app.data.menu.event.PerformMenuUpdateEvent;
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

    @Inject
    EventBus eventBus;

    private DiComponent diComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, String.format("%s created. Initializing DI framework.",
                this.getClass().getSimpleName()));

        this.diComponent = DaggerDiComponent
                .builder()
                .diModule(new DiModule(this))
                .build();

        // Invoke components that are not injected anywhere manually.
        // Otherwise they are not instantiated.
        this.diComponent.staffMembersService();
        this.diComponent.menusService();

        // Inject dependencies into this class.
        this.diComponent.inject(this);

        // Register for global events
        this.eventBus.register(this);

        // Trigger data update from the server to minimize waiting.
        this.eventBus.post(new PerformMenuUpdateEvent());
    }

    public DiComponent getDiComponent() {
        return diComponent;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoSubscriberEvent(NoSubscriberEvent event) {
        String errorMsg = String.format("ERROR: No subscriber for event: %s", event.originalEvent);
        Log.e(TAG, errorMsg);
        Toast.makeText(this, errorMsg,
                Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscriberExceptionEvent(SubscriberExceptionEvent event) {
        String errorMsg = String.format("ERROR: Exception thrown inside @Subscribe method: %s", event.throwable);
        Log.e(TAG, errorMsg);
        Toast.makeText(this, errorMsg,
                Toast.LENGTH_LONG).show();
    }
}
