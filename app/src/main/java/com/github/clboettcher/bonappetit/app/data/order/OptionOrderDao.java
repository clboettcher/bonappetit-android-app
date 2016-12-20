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
package com.github.clboettcher.bonappetit.app.data.order;

import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collection;

public class OptionOrderDao {

    private RuntimeExceptionDao<OptionOrderEntity, Long> dao;
    private BonAppetitDbHelper bonAppetitDbHelper;

    @Inject
    public OptionOrderDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(OptionOrderEntity.class);
    }

    public void save(Collection<OptionOrderEntity> optionOrderEntities) {
        dao.create(optionOrderEntities);
    }

    public void delete(Collection<OptionOrderEntity> optionOrderEntities) {
        Preconditions.checkNotNull(optionOrderEntities, "optionOrderEntities");
        dao.delete(optionOrderEntities);
    }

    public void deleteAll() throws SQLException {
        TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), OptionOrderEntity.class);
    }
}
