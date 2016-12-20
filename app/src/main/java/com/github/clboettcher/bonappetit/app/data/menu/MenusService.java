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
package com.github.clboettcher.bonappetit.app.data.menu;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.ApiProvider;
import com.github.clboettcher.bonappetit.server.menu.api.dto.read.MenuDto;
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
