package com.github.clboettcher.bonappetit.app.data;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clboettcher.bonappetit.app.core.ConfigProvider;
import com.github.clboettcher.bonappetit.app.data.menu.MenusApi;
import com.github.clboettcher.bonappetit.app.data.preferences.BaseUrlChangedEvent;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMembersApi;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;

/**
 * Provides implementations of the API interfaces that can be used
 * to perform backend calls.
 * <p>
 * The API implementations are not injected directly into the services
 * which use them, because their configuration might change. E.g. the user might
 * alter the servers backend URL. This class is responsible for detecting configuration
 * changes and reinitializing API implementations accordingly. The service should query
 * this class for the API implementation before performing any request. This way we
 * can make sure that the most current (and correct) API implementation is used at all times.
 */
public class ApiProvider {

    private static final String TAG = ApiProvider.class.getName();

    /**
     * The bean providing the apps config.
     */
    private ConfigProvider configProvider;

    /**
     * The API to access the staff members resource.
     */
    private StaffMembersApi staffMemberApi;

    /**
     * Jackson object mapper.
     */
    private ObjectMapper objectMapper;

    /**
     * The API to access the menus.
     */
    private MenusApi menusApi;

    /**
     * Constructor setting the specified properties.
     *
     * @param configProvider The bean providing the apps config.
     * @param eventBus       The bus.
     * @param objectMapper   The object mapper.
     */
    @Inject
    public ApiProvider(ConfigProvider configProvider, EventBus eventBus, ObjectMapper objectMapper) {
        this.configProvider = configProvider;
        this.objectMapper = objectMapper;
        eventBus.register(this);
        // Initialize the APIs.
        this.onBaseUrlChanged(null);
    }

    @Subscribe
    public void onBaseUrlChanged(BaseUrlChangedEvent ignored) {
        Log.i(TAG, "Base URL changed. Reinitializing the APIs.");
        String baseUrl = configProvider.getBaseUrl();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        // Reinitialize server APIs.
        staffMemberApi = retrofit.create(StaffMembersApi.class);
        menusApi = retrofit.create(MenusApi.class);
    }

    public StaffMembersApi getStaffMemberApi() {
        return staffMemberApi;
    }

    public MenusApi getMenusApi() {
        return menusApi;
    }
}