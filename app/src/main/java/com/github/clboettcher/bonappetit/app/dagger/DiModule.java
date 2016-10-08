package com.github.clboettcher.bonappetit.app.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clboettcher.bonappetit.app.db.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.menu.dao.ItemDao;
import com.github.clboettcher.bonappetit.app.menu.dao.MenuDao;
import com.github.clboettcher.bonappetit.app.menu.dao.OptionDao;
import com.github.clboettcher.bonappetit.app.menu.dao.RadioItemDao;
import com.github.clboettcher.bonappetit.app.menu.mapper.ItemEntityMapper;
import com.github.clboettcher.bonappetit.app.menu.mapper.MenuEntityMapper;
import com.github.clboettcher.bonappetit.app.menu.mapper.OptionEntityMapper;
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
    public MenuDao provideMenuDao(BonAppetitDbHelper bonAppetitDbHelper, ItemDao itemDao) {
        return new MenuDao(bonAppetitDbHelper, itemDao);
    }

    @Provides
    @Singleton
    public ItemDao provideItemDao(BonAppetitDbHelper bonAppetitDbHelper, OptionDao optionDao) {
        return new ItemDao(bonAppetitDbHelper, optionDao);
    }

    @Provides
    @Singleton
    public OptionDao provideOptionDao(BonAppetitDbHelper bonAppetitDbHelper, RadioItemDao radioItemDao) {
        return new OptionDao(bonAppetitDbHelper, radioItemDao);
    }

    @Provides
    @Singleton
    public RadioItemDao provideRadioItemDao(BonAppetitDbHelper bonAppetitDbHelper) {
        return new RadioItemDao(bonAppetitDbHelper);
    }

    @Provides
    @Singleton
    public OptionEntityMapper provideOptionEntityMapper() {
        return new OptionEntityMapper();
    }

    @Provides
    @Singleton
    public ItemEntityMapper provideItemEntityMapper(OptionEntityMapper optionEntityMapper) {
        return new ItemEntityMapper(optionEntityMapper);
    }

    @Provides
    @Singleton
    public MenuEntityMapper provideMenuEntityMapper(ItemEntityMapper itemEntityMapper) {
        return new MenuEntityMapper(itemEntityMapper);
    }

    @Provides
    @Singleton
    public ObjectMapper provideObjectMapper() {
        return ObjectMapperFactory.create();
    }
}
