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
package com.github.clboettcher.bonappetit.app.ui.takeorders;

import android.content.Context;
import android.util.Log;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.ui.menu.MenuFragment;
import com.github.clboettcher.bonappetit.app.ui.ordersoverview.OrdersOverviewFragment;
import com.github.clboettcher.bonappetit.app.ui.selectcustomer.SelectCustomerFragment;

import java.util.HashMap;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the take orders
 * sections of the app.
 *
 * @see SelectCustomerFragment
 */
public class TakeOrdersPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private static final String TAG = TakeOrdersPagerAdapter.class.getName();

    private Context takeOrdersActivity;
    private HashMap<Integer, TakeOrdersFragment> fragments = new HashMap<>();

    public TakeOrdersPagerAdapter(Context context, android.support.v4.app.FragmentManager fm) {
        super(fm);
        this.takeOrdersActivity = context;

        // set up the take orders fragments
        fragments.put(TakeOrdersActivity.TAB_SELECT_CUSTOMER, new SelectCustomerFragment());
        fragments.put(TakeOrdersActivity.TAB_MENU, new MenuFragment());
        fragments.put(TakeOrdersActivity.TAB_OVERVIEW, new OrdersOverviewFragment());
    }

    @Override
    public android.support.v4.app.Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case TakeOrdersActivity.TAB_SELECT_CUSTOMER:
                return takeOrdersActivity.getResources().getString(R.string.fragment_select_customer_tab_title);
            case TakeOrdersActivity.TAB_MENU:
                return takeOrdersActivity.getResources().getString(R.string.fragment_menu_tab_title);
            case TakeOrdersActivity.TAB_OVERVIEW:
                return takeOrdersActivity.getResources().getString(R.string.fragment_orders_overview_tab_title);
            default:
                throw new IllegalArgumentException(String.format("TODO: CREATE tab title for " +
                        "fragment at position %d", position));
        }
    }

    public void update(int position) {
        Log.i(TAG, String.format("update() called on tab at position %d", position));
        fragments.get(position).update();
    }

}
