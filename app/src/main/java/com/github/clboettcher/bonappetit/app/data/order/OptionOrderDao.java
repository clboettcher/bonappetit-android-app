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
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.RadioItemOrderEntity;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collection;

public class OptionOrderDao {

    private RuntimeExceptionDao<OptionOrderEntity, Long> dao;
    private RadioItemOrderDao radioItemOrderDao;
    private BonAppetitDbHelper bonAppetitDbHelper;

    @Inject
    public OptionOrderDao(BonAppetitDbHelper bonAppetitDbHelper,
                          RadioItemOrderDao radioItemOrderDao) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(OptionOrderEntity.class);
        this.radioItemOrderDao = radioItemOrderDao;
    }

    public void save(Collection<OptionOrderEntity> optionOrderEntities) {
        for (OptionOrderEntity optionOrderEntity : optionOrderEntities) {

            dao.create(optionOrderEntity);

            if (optionOrderEntity.getOptionType() == OptionEntityType.RADIO) {
                radioItemOrderDao.save(optionOrderEntity.getAvailableRadioItemEntities());

                // The selected radio item exists in the database but the foreign key field
                // in OPTION_ORDER has not been set so we need to update it.
                RadioItemOrderEntity selectedRadioItem = this.findSelectedRadioItem(optionOrderEntity
                                .getAvailableRadioItemEntities(),
                        optionOrderEntity.getSelectedRadioItemEntity().getRadioItemId());
                optionOrderEntity.setSelectedRadioItemEntity(selectedRadioItem);
                dao.update(optionOrderEntity);
            }
        }
    }

    public void update(Collection<OptionOrderEntity> optionOrderEntities) {
        for (OptionOrderEntity optionOrderEntity : optionOrderEntities) {
            dao.update(optionOrderEntity);
        }
    }

    private RadioItemOrderEntity findSelectedRadioItem(Collection<RadioItemOrderEntity> availableRadioItemEntities,
                                                       Long radioItemId) {
        for (RadioItemOrderEntity availableRadioItemEntity : availableRadioItemEntities) {
            if (radioItemId.equals(availableRadioItemEntity.getRadioItemId())) {
                return availableRadioItemEntity;
            }
        }

        throw new IllegalStateException(String.format("Could not find radio item with id %d in list %s", radioItemId,
                availableRadioItemEntities));
    }

    public void delete(Collection<OptionOrderEntity> optionOrderEntities) {
        Preconditions.checkNotNull(optionOrderEntities, "optionOrderEntities");
        dao.delete(optionOrderEntities);
    }

    public void deleteAll() throws SQLException {
        TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), OptionOrderEntity.class);
        this.radioItemOrderDao.deleteAll();
    }
}
