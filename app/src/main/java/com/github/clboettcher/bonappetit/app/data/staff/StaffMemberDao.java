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

    public List<StaffMemberEntity> list() {
        return Collections.unmodifiableList(
                dao.queryForAll()
        );
    }

    public long count() {
        return dao.countOf();
    }


}
