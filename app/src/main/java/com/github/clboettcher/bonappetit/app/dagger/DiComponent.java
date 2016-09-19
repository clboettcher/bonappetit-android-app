package com.github.clboettcher.bonappetit.app.dagger;

import com.github.clboettcher.bonappetit.app.activity.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.activity.MainActivity;
import com.github.clboettcher.bonappetit.app.application.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.service.staffmembers.StaffMemberService;
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

    StaffMemberService staffMembersService();
}
