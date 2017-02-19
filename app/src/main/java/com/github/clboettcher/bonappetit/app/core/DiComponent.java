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
package com.github.clboettcher.bonappetit.app.core;

import com.github.clboettcher.bonappetit.app.data.menu.MenuResource;
import com.github.clboettcher.bonappetit.app.data.menu.MenusService;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberService;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMembersResource;
import com.github.clboettcher.bonappetit.app.ui.editorder.EditOrderActivity;
import com.github.clboettcher.bonappetit.app.ui.menu.MenuFragment;
import com.github.clboettcher.bonappetit.app.ui.ordersoverview.OrdersOverviewFragment;
import com.github.clboettcher.bonappetit.app.ui.preferences.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.ui.selectcustomer.SelectCustomerFragment;
import com.github.clboettcher.bonappetit.app.ui.selectstaffmember.StaffMembersListActivity;
import com.github.clboettcher.bonappetit.app.ui.staffmemberandcustomer.StaffMemberAndCustomerView;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import dagger.Component;

import javax.inject.Singleton;

/**
 * The DI component declares methods for injecting into beans that have dependencies
 * on other beans managed by the di framework. It also contains methods that create
 * beans directly. Those can be called by application code to make sure that beans
 * which are not injected anywhere are created nevertheless.
 */
@Component(modules = DiModule.class)
@Singleton
public interface DiComponent {

    void inject(BonAppetitApplication bonAppetitApplication);

    void inject(BonAppetitPreferencesActivity bonAppetitPreferencesActivity);

    void inject(StaffMembersListActivity staffMembersListActivity);

    void inject(TakeOrdersActivity takeOrdersActivity);

    void inject(SelectCustomerFragment selectCustomerFragment);

    void inject(MenuFragment menuFragment);

    void inject(EditOrderActivity editOrderActivity);

    void inject(OrdersOverviewFragment ordersOverviewFragment);

    void inject(StaffMemberAndCustomerView staffMemberAndCustomerView);

    StaffMemberService staffMembersService();

    MenusService menusService();

    MenuResource menuRepository();

    StaffMembersResource staffMembersResource();
}
