/*
 * Copyright (c) 2016 Claudius Boettcher (pos.bonappetit@gmail.com)
 *
 * This file is part of BonAppetit. BonAppetit is an Android based
 * Point-of-Sale client-server application for small restaurants.
 *
 * BonAppetit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonAppetit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BonAppetit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.clboettcher.bonappetit.app.data.menu.dao;


import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Data access facade for {@link ItemEntity}.
 */
public class ItemDao {

    private static final String TAG = ItemDao.class.getName();
    private RuntimeExceptionDao<ItemEntity, Long> dao;
    private BonAppetitDbHelper bonAppetitDbHelper;
    private OptionDao optionDao;

    @Inject
    public ItemDao(BonAppetitDbHelper bonAppetitDbHelper, OptionDao optionDao) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
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

    public List<ItemEntity> list() {
        return dao.queryForAll();
    }

    public int deleteAll() throws SQLException {
        return TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), ItemEntity.class);
    }

    public long count() {
        return dao.countOf();
    }

    public Optional<ItemEntity> get(Long itemId) {
        return Optional.fromNullable(dao.queryForId(itemId));
    }

    public boolean exists(long id) {
        return dao.idExists(id);
    }

    public void refresh(ItemEntity item) {
        dao.refresh(item);
    }
}
