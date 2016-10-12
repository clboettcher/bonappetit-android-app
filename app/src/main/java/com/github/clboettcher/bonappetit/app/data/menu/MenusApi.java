package com.github.clboettcher.bonappetit.app.data.menu;

import com.github.clboettcher.bonappetit.server.menu.api.MenuManagement;
import com.github.clboettcher.bonappetit.server.menu.api.dto.MenuDto;
import retrofit2.Call;
import retrofit2.http.GET;

public interface MenusApi {

    @GET(MenuManagement.ROOT_PATH + MenuManagement.CURRENT_MENU_PATH)
    Call<MenuDto> getCurrentMenu();
}
