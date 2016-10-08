package com.github.clboettcher.bonappetit.app.takeorders;

import android.content.Context;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.menu.ui.MenuFragment;
import com.github.clboettcher.bonappetit.app.selectcustomer.SelectCustomerFragment;

import java.util.HashMap;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the take orders
 * sections of the app.
 *
 * @see com.github.clboettcher.bonappetit.app.selectcustomer.SelectCustomerFragment
 */
public class TakeOrdersPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private Context takeOrdersActivity;
    private HashMap<Integer, TakeOrdersFragment> fragments = new HashMap<>();

    public TakeOrdersPagerAdapter(Context context, android.support.v4.app.FragmentManager fm) {
        super(fm);
        this.takeOrdersActivity = context;

        // set up the take orders fragments
        fragments.put(TakeOrdersActivity.TAB_SELECT_CUSTOMER, new SelectCustomerFragment());
        fragments.put(TakeOrdersActivity.TAB_MENU, new MenuFragment());
        fragments.put(TakeOrdersActivity.TAB_OVERVIEW, new SelectCustomerFragment());
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
                return takeOrdersActivity.getResources().getString(R.string.fragment_menu_title);
            case TakeOrdersActivity.TAB_OVERVIEW:
                return takeOrdersActivity.getResources().getString(R.string.fragment_order_overview_title);
            default:
                throw new IllegalArgumentException(String.format("TODO: CREATE tab title for " +
                        "fragment at position %d", position));
        }
    }

    public void update(int position) {
        fragments.get(position).update();
    }

}
