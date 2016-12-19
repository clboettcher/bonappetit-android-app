package com.github.clboettcher.bonappetit.app.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gihub.clboettcher.bonappetit.price_calculation.api.PriceCalculator;
import com.gihub.clboettcher.bonappetit.price_calculation.impl.PriceCalculatorImpl;
import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.menu.dao.ItemDao;
import com.github.clboettcher.bonappetit.app.data.menu.dao.MenuDao;
import com.github.clboettcher.bonappetit.app.data.menu.dao.OptionDao;
import com.github.clboettcher.bonappetit.app.data.menu.dao.RadioItemDao;
import com.github.clboettcher.bonappetit.app.data.menu.mapper.ItemEntityMapper;
import com.github.clboettcher.bonappetit.app.data.menu.mapper.MenuEntityMapper;
import com.github.clboettcher.bonappetit.app.data.menu.mapper.OptionEntityMapper;
import com.github.clboettcher.bonappetit.app.data.order.OptionOrderDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntityMapper;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.common.ObjectMapperFactory;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;
import java.math.BigDecimal;

/**
 * The module provides dependencies for the DI framework to use. Its similar to
 * a spring @Context class.
 */
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
    public MenuDao provideMenuDao(BonAppetitDbHelper bonAppetitDbHelper,
                                  ItemDao itemDao,
                                  OptionDao optionDao,
                                  RadioItemDao radioItemDao
    ) {
        return new MenuDao(bonAppetitDbHelper, itemDao, optionDao, radioItemDao);
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
    public OptionOrderDao provideOptionOrderDao(BonAppetitDbHelper bonAppetitDbHelper) {
        return new OptionOrderDao(bonAppetitDbHelper);
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

    @Provides
    @Singleton
    public PriceCalculator providePriceCalculator() {
        return new PriceCalculatorImpl(new BigDecimal("1"), new BigDecimal("0.5"));
    }
}
