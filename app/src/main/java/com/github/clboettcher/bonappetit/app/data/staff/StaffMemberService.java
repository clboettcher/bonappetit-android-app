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
package com.github.clboettcher.bonappetit.app.data.staff;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.ApiProvider;
import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import retrofit2.Call;
import retrofit2.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class StaffMemberService {

    private static final String TAG = StaffMemberService.class.getName();
    private ApiProvider apiProvider;

    @Inject
    public StaffMemberService(ApiProvider apiProvider) {
        Log.i(TAG, "Creating StaffMemberService. Registering for events.");
        this.apiProvider = apiProvider;
    }

    public void getStaffMembers(Callback<List<StaffMemberDto>> callback) {
        Call<List<StaffMemberDto>> listCall = apiProvider
                .getStaffMemberApi()
                .listStaffMembers();
        listCall.enqueue(callback);
    }
}
