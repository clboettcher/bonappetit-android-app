package com.github.clboettcher.bonappetit.app.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.github.clboettcher.bonappetit.app.db.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberEntityMapper;
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
    public StaffMemberEntityMapper provideStaffMemberEntityMapper() {
        return new StaffMemberEntityMapper();
    }

    @Provides
    @Singleton
    public BonAppetitDbHelper provideBonAppetitDbHelper() {
        return new BonAppetitDbHelper(app);
    }

}
