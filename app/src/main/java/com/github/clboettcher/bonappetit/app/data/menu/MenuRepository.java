package com.github.clboettcher.bonappetit.app.data.menu;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.core.ConfigProvider;
import com.github.clboettcher.bonappetit.app.data.ErrorCode;
import com.github.clboettcher.bonappetit.app.data.ErrorMapper;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.menu.dao.MenuDao;
import com.github.clboettcher.bonappetit.app.data.menu.entity.MenuEntity;
import com.github.clboettcher.bonappetit.app.data.menu.event.MenuUpdateCompletedEvent;
import com.github.clboettcher.bonappetit.app.data.menu.event.PerformMenuUpdateEvent;
import com.github.clboettcher.bonappetit.app.data.menu.mapper.MenuEntityMapper;
import com.github.clboettcher.bonappetit.common.JsonUtils;
import com.github.clboettcher.bonappetit.server.menu.api.dto.read.MenuDto;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

// TODO: rename to resource
@Singleton
public class MenuRepository {

    private static final String TAG = MenuRepository.class.getName();

    private Context context;
    private MenusService menuService;
    private MenuDao menuDao;
    private MenuEntityMapper menuEntityMapper;
    private EventBus bus;
    private ConfigProvider configProvider;
    private ObjectMapper objectMapper;

    private AtomicReference<Loadable<MenuEntity>> menuLoadable =
            new AtomicReference<>(Loadable.<MenuEntity>initial());

    @Inject
    public MenuRepository(Context context,
                          MenusService menuService,
                          MenuDao menuDao,
                          MenuEntityMapper menuEntityMapper,
                          EventBus bus,
                          ConfigProvider configProvider,
                          ObjectMapper objectMapper) {
        this.context = context;
        this.menuService = menuService;
        this.menuDao = menuDao;
        this.menuEntityMapper = menuEntityMapper;
        this.bus = bus;
        this.configProvider = configProvider;
        this.objectMapper = objectMapper;
        bus.register(this);

        Log.i(TAG, this.getClass().getSimpleName() + " initialized.");
    }

    public void updateMenu() {
        this.updateMenu(null);
    }

    @Subscribe
    public void updateMenu(PerformMenuUpdateEvent event) {
        this.menuLoadable.set(Loadable.<MenuEntity>loading());

        if (configProvider.useTestData()) {
            this.updateMenuWithTestData();
        } else {
            this.fetchCurrentMenuFromServer();
        }
    }

    public Loadable<MenuEntity> getMenu() {
        return menuLoadable.get();
    }

    private void fetchCurrentMenuFromServer() {
        Log.i(TAG, "Starting to fetch the current menu from the server.");
        menuService.getCurrentMenu(new Callback<MenuDto>() {
            @Override
            public void onResponse(Call<MenuDto> call, Response<MenuDto> response) {
                if (response.isSuccessful()) {
                    MenuEntity fetchedMenu = menuEntityMapper.mapToMenuEntity(response.body());
                    menuDao.createOrUpdate(fetchedMenu);
                    Log.i(TAG, "Menu update successful");
                    menuLoadable.set(Loadable.loaded(fetchedMenu));
                    bus.post(new MenuUpdateCompletedEvent());
                } else {
                    String errorMsg = String.format("Menu update failed: %d %s",
                            response.code(),
                            response.message());
                    Log.e(TAG, errorMsg);
                    ErrorCode errorCode = ErrorMapper.toErrorCode(response.code());
                    menuLoadable.set(Loadable.<MenuEntity>failed(errorCode));
                    bus.post(new MenuUpdateCompletedEvent());
                }
            }

            @Override
            public void onFailure(Call<MenuDto> call, Throwable t) {
                Log.e(TAG, "Menu update failed", t);
                ErrorCode errorCode = ErrorMapper.toErrorCode(t);
                menuLoadable.set(Loadable.<MenuEntity>failed(errorCode));
                bus.post(new MenuUpdateCompletedEvent());

                if (BonAppetitApplication.DEBUG_TOASTS_ENABLED) {
                    String errorMsg = String.format("Menu update failed: %s (%s)",
                            t.getMessage(),
                            t.getClass().getName());
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Updates the menu db content with content from local files.
     */
    private void updateMenuWithTestData() {
        Log.i(TAG, "Updating the menu with static test data.");
        MenuDto menuDto;
        try {
            String menuJson = configProvider.readRawResourceAsString(R.raw.menu);
            menuDto = JsonUtils.parseJsonObject(menuJson, objectMapper, MenuDto.class);
        } catch (IOException e) {
            String errorMsg = "Failed to read menu test data from resources. Update aborted.";
            Log.e(TAG, errorMsg, e);
            menuLoadable.set(Loadable.<MenuEntity>failed(ErrorCode.ERR_RESOURCE_ACCESS_FAILED));
            bus.post(new MenuUpdateCompletedEvent());
            return;
        }

        MenuEntity menu = menuEntityMapper.mapToMenuEntity(menuDto);
        menuDao.createOrUpdate(menu);
        menuLoadable.set(Loadable.loaded(menu));
        bus.post(new MenuUpdateCompletedEvent());
    }
}

