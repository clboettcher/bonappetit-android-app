package com.github.clboettcher.bonappetit.app.data;

import android.util.Base64;
import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clboettcher.bonappetit.app.core.ConfigProvider;
import com.github.clboettcher.bonappetit.app.data.menu.MenusApi;
import com.github.clboettcher.bonappetit.app.data.order.OrdersApi;
import com.github.clboettcher.bonappetit.app.data.preferences.ServerConfigChangedEvent;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMembersApi;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Provides implementations of the API interfaces that can be used
 * to perform backend calls.
 * <p/>
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
     * The API to create orders.
     */
    private OrdersApi ordersApi;

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
        this.onServerConfigChanged(null);
    }

    @Subscribe
    public void onServerConfigChanged(ServerConfigChangedEvent ignored) {
        Log.i(TAG, "Base URL changed. Reinitializing the APIs.");
        String baseUrl = configProvider.getBaseUrl();

        // Enable basic auth on all requests via request interceptor on the HTTP client
        String credentials = String.format("%s:%s",
                configProvider.getUsername(),
                configProvider.getPassword());
        final String basic =
                "Basic " + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", basic)
                                .header("Accept", "application/json")
                                .method(original.method(), original.body());
                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(JacksonConverterFactory.create(objectMapper))
                .build();

        // Reinitialize server APIs.
        staffMemberApi = retrofit.create(StaffMembersApi.class);
        menusApi = retrofit.create(MenusApi.class);
        ordersApi = retrofit.create(OrdersApi.class);
    }

    public StaffMembersApi getStaffMemberApi() {
        return staffMemberApi;
    }

    public MenusApi getMenusApi() {
        return menusApi;
    }

    public OrdersApi getOrdersApi() {
        return ordersApi;
    }
}