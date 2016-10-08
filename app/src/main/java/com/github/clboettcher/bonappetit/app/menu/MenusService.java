package com.github.clboettcher.bonappetit.app.menu;

import android.util.Log;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clboettcher.bonappetit.app.ConfigProvider;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.menu.event.MenuUpdateFailedEvent;
import com.github.clboettcher.bonappetit.app.menu.event.MenuUpdateSuccessfulEvent;
import com.github.clboettcher.bonappetit.app.menu.event.PerformMenuUpdateEvent;
import com.github.clboettcher.bonappetit.app.menu.mapper.MenuEntityMapper;
import com.github.clboettcher.bonappetit.app.service.ApiProvider;
import com.github.clboettcher.bonappetit.core.JsonUtils;
import com.github.clboettcher.bonappetit.server.menu.api.dto.MenuDto;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;

public class MenusService {

    private static final String TAG = MenusService.class.getName();

    private MenuDao menuDao;
    private EventBus bus;
    private ApiProvider apiProvider;
    private ConfigProvider configProvider;
    private MenuEntityMapper menuEntityMapper;
    private ObjectMapper objectMapper;

    @Inject
    public MenusService(MenuDao menuDao,
                        MenuEntityMapper menuEntityMapper,
                        EventBus eventBus,
                        ApiProvider apiProvider,
                        ConfigProvider configProvider, ObjectMapper objectMapper) {
        Log.i(TAG, "Creating MenusService. Registering for events.");
        this.objectMapper = objectMapper;
        this.configProvider = configProvider;
        this.menuDao = menuDao;
        this.menuEntityMapper = menuEntityMapper;
        this.bus = eventBus;
        this.apiProvider = apiProvider;
        bus.register(this);
    }

    @Subscribe
    public void onUpdateMenuEvent(PerformMenuUpdateEvent event) {
        if (configProvider.useTestData()) {
            Log.i(TAG, "Test data enabled. Using local menu test data.");
            updateMenuWithTestData();
        } else {
            Log.i(TAG, String.format("Received %s. Creating event to fetch" +
                    " the current menu from the server.", event.getClass().getSimpleName()));
            fetchCurrentMenuFromServer();
        }
    }

    /**
     * Updates the menu db content with content from local files.
     */
    private void updateMenuWithTestData() {
        MenuDto menuDto;
        try {
            String menuJson = configProvider.readRawResourceAsString(R.raw.menu);
            menuDto = JsonUtils.parseJsonObject(menuJson, objectMapper, MenuDto.class);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read menu test data from resources. Update aborted.", e);
            return;
        }

        menuDao.save(menuEntityMapper.mapToMenuEntity(menuDto));
        bus.post(new MenuUpdateSuccessfulEvent());
    }


    private void fetchCurrentMenuFromServer() {
        Call<MenuDto> currentMenuCall = apiProvider
                .getMenusApi()
                .getCurrentMenu();
        currentMenuCall.enqueue(new Callback<MenuDto>() {
            @Override
            public void onResponse(Call<MenuDto> call, Response<MenuDto> response) {
                if (response.isSuccessful()) {
                    menuDao.save(menuEntityMapper.mapToMenuEntity(response.body()));
                    Log.i(TAG, "Menu update successful");
                    bus.post(new MenuUpdateSuccessfulEvent());
                } else {
                    Log.e(TAG, String.format("Menu update failed: %d %s",
                            response.code(),
                            response.message()));
                    bus.post(new MenuUpdateFailedEvent(response.code(), response.message()));
                }
            }

            @Override
            public void onFailure(Call<MenuDto> call, Throwable t) {
                Log.e(TAG, "Menu update failed", t);
                bus.post(new MenuUpdateFailedEvent(t));
            }
        });
    }
}
