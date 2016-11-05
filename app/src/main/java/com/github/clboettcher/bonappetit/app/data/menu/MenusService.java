package com.github.clboettcher.bonappetit.app.data.menu;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.ApiProvider;
import com.github.clboettcher.bonappetit.server.menu.api.dto.MenuDto;
import retrofit2.Call;
import retrofit2.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MenusService {

    private static final String TAG = MenusService.class.getName();

    private ApiProvider apiProvider;

    @Inject
    public MenusService(ApiProvider apiProvider) {
        Log.i(TAG, "Creating MenusService.");
        this.apiProvider = apiProvider;
    }

    public void getCurrentMenu(Callback<MenuDto> callback) {
        Call<MenuDto> currentMenuCall = apiProvider
                .getMenusApi()
                .getCurrentMenu();
        currentMenuCall.enqueue(callback);
    }
}
