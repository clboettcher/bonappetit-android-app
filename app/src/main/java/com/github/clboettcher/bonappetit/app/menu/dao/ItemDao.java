package com.github.clboettcher.bonappetit.app.menu.dao;


import android.util.Log;
import com.github.clboettcher.bonappetit.app.db.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.menu.entity.ItemEntity;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import javax.inject.Inject;
import java.util.Collection;

/**
 * Data access facade for {@link ItemEntity}.
 */
public class ItemDao {

    private static final String TAG = ItemDao.class.getName();
    private RuntimeExceptionDao<ItemEntity, Long> dao;
    private OptionDao optionDao;

    @Inject
    public ItemDao(BonAppetitDbHelper bonAppetitDbHelper, OptionDao optionDao) {
        this.optionDao = optionDao;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(ItemEntity.class);
    }

    public void save(Collection<ItemEntity> items) {
        Preconditions.checkNotNull(items, "items");

        for (ItemEntity item : items) {
            Log.i(TAG, String.format("Saving item %s to database", item));
            if (item.hasOptions()) {
                optionDao.save(item.getOptions());
            }
            dao.create(item);
        }
    }

    public Collection<ItemEntity> list() {
        return dao.queryForAll();
    }

}
