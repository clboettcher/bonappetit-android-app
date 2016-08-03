package com.github.clboettcher.bonappetit.app.application;

import android.app.Application;
import android.widget.Toast;
import com.github.clboettcher.bonappetit.app.dagger.DaggerDiComponent;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;
import com.github.clboettcher.bonappetit.app.dagger.DiModule;
import org.greenrobot.eventbus.*;

import javax.inject.Inject;

public class BonAppetitApplication extends Application {

    @Inject
    EventBus eventBus;

    private DiComponent diComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        this.diComponent = DaggerDiComponent
                .builder()
                .diModule(new DiModule(this))
                .build()
        ;
        // Invoke components that are not injected anywhere manually.
        // Otherwise they are not instantiated.
        this.diComponent.staffMembersService();

        // Inject dependencies into this class.
        this.diComponent.inject(this);
        // Register for global events
        this.eventBus.register(this);
    }

    public DiComponent getDiComponent() {
        return diComponent;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoSubscriberEvent(NoSubscriberEvent event) {
        Toast.makeText(this, "ERROR: No subscriber for event: " + event.originalEvent, Toast.LENGTH_LONG).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSubscriberExceptionEvent(SubscriberExceptionEvent event) {
        Toast.makeText(this, "ERROR: Exception thrown inside @Subscribe method: " + event.throwable, Toast.LENGTH_LONG).show();
    }
}
