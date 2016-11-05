package com.github.clboettcher.bonappetit.app.core;

import com.github.clboettcher.bonappetit.app.data.menu.MenuRepository;
import com.github.clboettcher.bonappetit.app.data.menu.MenusService;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberService;
import com.github.clboettcher.bonappetit.app.ui.editorder.EditOrderActivity;
import com.github.clboettcher.bonappetit.app.ui.main.MainActivity;
import com.github.clboettcher.bonappetit.app.ui.menu.MenuFragment;
import com.github.clboettcher.bonappetit.app.ui.preferences.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.ui.selectcustomer.SelectCustomerFragment;
import com.github.clboettcher.bonappetit.app.ui.selectstaffmember.StaffMembersListActivity;
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

    void inject(MainActivity mainActivity);

    void inject(BonAppetitApplication bonAppetitApplication);

    void inject(BonAppetitPreferencesActivity bonAppetitPreferencesActivity);

    void inject(StaffMembersListActivity staffMembersListActivity);

    void inject(TakeOrdersActivity takeOrdersActivity);

    void inject(SelectCustomerFragment selectCustomerFragment);

    void inject(MenuFragment menuFragment);

    void inject(EditOrderActivity editOrderActivity);

    StaffMemberService staffMembersService();

    MenusService menusService();

    MenuRepository menuRepository();
}
