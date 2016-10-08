package com.github.clboettcher.bonappetit.app.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clboettcher.bonappetit.app.db.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.menu.MenuDao;
import com.github.clboettcher.bonappetit.app.menu.MenuEntityMapper;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberEntityMapper;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.core.ObjectMapperFactory;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

@Module
public class DiModule {

    private Application app;

    public DiModule(Application app) {
        this.app = app;
    }

    @Provides
    public Context provideContext() {
        return app;
    }

    @Provides
    public SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this.app);
    }

    @Provides
    @Singleton
    public EventBus provideEventBus() {
        return EventBus.getDefault();
    }

    @Provides
    @Singleton
    public StaffMemberDao provideStaffMemberDao(BonAppetitDbHelper bonAppetitDbHelper) {
        return new StaffMemberDao(bonAppetitDbHelper);
    }

    @Provides
    @Singleton
    public StaffMemberRefDao provideStaffMemberRefDao(BonAppetitDbHelper bonAppetitDbHelper) {
        return new StaffMemberRefDao(bonAppetitDbHelper);
    }

    @Provides
    @Singleton
    public StaffMemberEntityMapper provideStaffMemberEntityMapper() {
        return new StaffMemberEntityMapper();
    }

    @Provides
    @Singleton
    public BonAppetitDbHelper provideBonAppetitDbHelper() {
        return new BonAppetitDbHelper(app);
    }

    @Provides
    @Singleton
    public MenuDao provideMenuDao(BonAppetitDbHelper bonAppetitDbHelper) {
        return new MenuDao(bonAppetitDbHelper);
    }

    @Provides
    @Singleton
    public MenuEntityMapper provideMenuEntityMapper() {
        return new MenuEntityMapper();
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return ObjectMapperFactory.create();
    }
}
