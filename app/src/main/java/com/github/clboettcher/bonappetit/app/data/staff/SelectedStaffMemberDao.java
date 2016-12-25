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
package com.github.clboettcher.bonappetit.app.data.staff;

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

public class SelectedStaffMemberDao {

    private static final String TAG = SelectedStaffMemberDao.class.getName();
    private final BonAppetitDbHelper bonAppetitDbHelper;
    private final RuntimeExceptionDao<SelectedStaffMemberEntity, Long> dao;

    @Inject
    public SelectedStaffMemberDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(SelectedStaffMemberEntity.class);
    }

    public void save(StaffMemberEntity staffMemberEntity) {
        Preconditions.checkNotNull(staffMemberEntity, "staffMemberEntity");

        // Delete all entries. We have only the reference to one staff member at the moment.
        try {
            TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), SelectedStaffMemberEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        SelectedStaffMemberEntity data = new SelectedStaffMemberEntity();
        data.setStaffMemberId(staffMemberEntity.getId());
        data.setStaffMemberFirstName(staffMemberEntity.getFirstName());
        data.setStaffMemberLastName(staffMemberEntity.getLastName());
        dao.create(data);
        Log.i(TAG, String.format("Saved reference to staff member %s to database.", staffMemberEntity));
    }

    /**
     * Queries for the saved staff member. This method takes no id param since we only
     * have one staff member at a time at the moment.
     *
     * @return The staff member.
     */
    public Optional<SelectedStaffMemberEntity> get() {
        List<SelectedStaffMemberEntity> staffMemberRefEntities = dao.queryForAll();
        if (CollectionUtils.isEmpty(staffMemberRefEntities)) {
            return Optional.absent();
        } else {
            return Optional.of(staffMemberRefEntities.get(0));
        }
    }
}
