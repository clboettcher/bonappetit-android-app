package com.github.clboettcher.bonappetit.app.data.menu.dao;


import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.menu.entity.MenuEntity;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access facade for {@link MenuEntity}.
 */
public class MenuDao {

    private static final String TAG = MenuDao.class.getName();

    private BonAppetitDbHelper bonAppetitDbHelper;
    private RuntimeExceptionDao<MenuEntity, Long> dao;
    private final ItemDao itemDao;
    private final OptionDao optionDao;
    private final RadioItemDao radioItemDao;

    @Inject
    public MenuDao(BonAppetitDbHelper bonAppetitDbHelper, ItemDao itemDao,
                   OptionDao optionDao, RadioItemDao radioItemDao) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.itemDao = itemDao;
        this.optionDao = optionDao;
        this.radioItemDao = radioItemDao;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(MenuEntity.class);
    }

    /**
     * Clears all related tables and saves the given menu and all containing entities.
     * Should not be called with incomplete data since this operation is destructive.
     *
     * @param menu The menu to save.
     */
    public void createOrUpdate(MenuEntity menu) {
        Preconditions.checkNotNull(menu, "menu");

        // Delete all stored entities. The server is the master.
        try {
            TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), MenuEntity.class);
            itemDao.deleteAll();
            optionDao.deleteAll();
            radioItemDao.deleteAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Log.i(TAG, String.format("Saving menu %s to database", menu));

        itemDao.save(menu.getItems());
        dao.createOrUpdate(menu);
    }

    public Optional<MenuEntity> getFirst() {
        List<MenuEntity> menus = dao.queryForAll();
        if (CollectionUtils.isEmpty(menus)) {
            return Optional.absent();
        } else {
            return Optional.of(menus.get(0));
        }
    }
}
