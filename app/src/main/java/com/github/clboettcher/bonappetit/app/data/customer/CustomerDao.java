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
package com.github.clboettcher.bonappetit.app.data.customer;


import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.List;

/**
 * Data access facade for the selected customer.
 */
public class CustomerDao {

    private static final String TAG = CustomerDao.class.getName();
    private final BonAppetitDbHelper bonAppetitDbHelper;
    private final RuntimeExceptionDao<CustomerEntity, Long> dao;

    @Inject
    public CustomerDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(CustomerEntity.class);
    }

    public void save(CustomerEntity customerEntity) {
        Preconditions.checkNotNull(customerEntity, "customerEntity");

        // Delete all entries. We only have one selected customer at a time.
        try {
            TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), CustomerEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        dao.create(customerEntity);
        Log.i(TAG, String.format("Saved customer entity %s in database", customerEntity));
    }

    public Optional<CustomerEntity> get() {
        List<CustomerEntity> customerEntities = dao.queryForAll();
        if (CollectionUtils.isEmpty(customerEntities)) {
            return Optional.absent();
        } else {
            return Optional.fromNullable(customerEntities.get(0));
        }
    }

    public void refresh(CustomerEntity customer) {
        dao.refresh(customer);
    }
}
