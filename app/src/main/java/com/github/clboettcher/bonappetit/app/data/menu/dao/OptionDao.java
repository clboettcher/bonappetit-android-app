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
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Data access facade for {@link OptionEntity}.
 */
public class OptionDao {

    private static final String TAG = OptionDao.class.getName();
    private BonAppetitDbHelper bonAppetitDbHelper;
    private RuntimeExceptionDao<OptionEntity, Long> dao;
    private RadioItemDao radioItemDao;

    @Inject
    public OptionDao(BonAppetitDbHelper bonAppetitDbHelper, RadioItemDao radioItemDao) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.radioItemDao = radioItemDao;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(OptionEntity.class);
    }

    public void save(Collection<OptionEntity> options) {
        Preconditions.checkNotNull(options, "options");

        for (OptionEntity option : options) {
            Log.i(TAG, String.format("Saving option %s to database", option));
            if (option.getType() == OptionEntityType.RADIO) {
                radioItemDao.save(option.getRadioItemEntities());
            }
            dao.create(option);
        }
    }

    public Collection<OptionEntity> list() {
        return dao.queryForAll();
    }

    int deleteAll() throws SQLException {
        return TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), OptionEntity.class);
    }

    public OptionEntity get(Long optionId) {
        return dao.queryForId(optionId);
    }

    public void refresh(OptionEntity option) {
        dao.refresh(option);
    }
}
