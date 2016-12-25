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
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Data access facade for staff members.
 */
public class StaffMemberDao {

    private static final String TAG = StaffMemberDao.class.getName();
    private BonAppetitDbHelper bonAppetitDbHelper;
    private RuntimeExceptionDao<StaffMemberEntity, Long> dao;

    @Inject
    public StaffMemberDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(StaffMemberEntity.class);
    }

    public void save(List<StaffMemberEntity> staffMembers) {
        Preconditions.checkNotNull(staffMembers, "staffMembers");

        // Delete all stored entities. The server is the master.
        try {
            TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), StaffMemberEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Log.i(TAG, String.format("Saving %d staff member(s) to database", staffMembers.size()));

        for (StaffMemberEntity staffMember : staffMembers) {
            dao.create(staffMember);
        }
    }

    public boolean exists(Long id) {
        return this.dao.idExists(id);
    }

    public List<StaffMemberEntity> list() {
        return Collections.unmodifiableList(
                dao.queryForAll()
        );
    }

    public long count() {
        return dao.countOf();
    }

    public StaffMemberEntity getById(Long staffMemberId) {
        return dao.queryForId(staffMemberId);
    }
}
