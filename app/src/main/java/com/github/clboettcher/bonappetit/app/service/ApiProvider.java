package com.github.clboettcher.bonappetit.app.service;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.ConfigProvider;
import com.github.clboettcher.bonappetit.app.Constants;
import com.github.clboettcher.bonappetit.app.event.BaseUrlChangedEvent;
import com.github.clboettcher.bonappetit.app.service.staffmembers.StaffMembersApi;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;

/**
 * Provides implementations of the API interfaces that can be used
 * to perform backend calls.
 */
public class ApiProvider {

    /**
     * The bean providing the apps config.
     */
    private ConfigProvider configProvider;

    /**
     * The API to access the staff members resource.
     */
    private StaffMembersApi staffMemberApi;

    /**
     * Constructor setting the specified properties.
     *
     * @param configProvider The bean providing the apps config.
     * @param eventBus       The bus.
     */
    @Inject
    public ApiProvider(ConfigProvider configProvider, EventBus eventBus) {
        this.configProvider = configProvider;
        eventBus.register(this);
        // Initialize the APIs.
        this.onBaseUrlChanged(null);
    }

    @Subscribe
    public void onBaseUrlChanged(BaseUrlChangedEvent ignored) {
        Log.i(Constants.TAG, "Base URL changed. Reinitializing the APIs.");
        String baseUrl = configProvider.getBaseUrl();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        // Reinitialize server APIs.
        staffMemberApi = retrofit.create(StaffMembersApi.class);
    }

    public StaffMembersApi getStaffMemberApi() {
        return staffMemberApi;
    }
}