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
import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioItemEntity;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Data access facade for {@link RadioItemEntity}.
 */
public class RadioItemDao {

    private static final String TAG = RadioItemDao.class.getName();
    private final BonAppetitDbHelper bonAppetitDbHelper;
    private RuntimeExceptionDao<RadioItemEntity, Long> dao;

    @Inject
    public RadioItemDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(RadioItemEntity.class);
    }

    public void save(Collection<RadioItemEntity> radioItems) {
        Preconditions.checkNotNull(radioItems, "radioItems");

        for (RadioItemEntity radioItem : radioItems) {
            Log.i(TAG, String.format("Saving radio item %s to database.", radioItem));
            dao.create(radioItem);
        }
    }

    public Collection<RadioItemEntity> list() {
        return dao.queryForAll();
    }

    int deleteAll() throws SQLException {
        return TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), RadioItemEntity.class);
    }
}
