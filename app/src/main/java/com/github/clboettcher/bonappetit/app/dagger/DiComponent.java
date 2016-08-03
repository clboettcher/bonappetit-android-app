package com.github.clboettcher.bonappetit.app.dagger;

import com.github.clboettcher.bonappetit.app.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.activity.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.activity.MainActivity;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = DiModule.class)
@Singleton
public interface DiComponent {

    void inject(MainActivity mainActivity);

    void inject(BonAppetitApplication bonAppetitApplication);

    void inject(BonAppetitPreferencesActivity bonAppetitPreferencesActivity);
}
