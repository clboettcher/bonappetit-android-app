package com.github.clboettcher.bonappetit.app.staff;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.db.BonAppetitDbHelper;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;

public class StaffMemberRefDao {

    private static final String TAG = StaffMemberRefDao.class.getName();
    private final BonAppetitDbHelper bonAppetitDbHelper;
    private final RuntimeExceptionDao<StaffMemberRefEntity, Long> dao;

    @Inject
    public StaffMemberRefDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(StaffMemberRefEntity.class);
    }

    public void save(StaffMemberEntity staffMemberEntity) {
        Preconditions.checkNotNull(staffMemberEntity, "staffMemberEntity");

        // Delete all entries. We have only the reference to one staff member at the moment.
        try {
            TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), StaffMemberRefEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        StaffMemberRefEntity data = new StaffMemberRefEntity();
        data.setStaffMemberEntity(staffMemberEntity);
        dao.create(data);
        Log.i(TAG, String.format("Saved reference to staff member %s to database.", staffMemberEntity));
    }

    public StaffMemberRefEntity get() {
        return dao.queryForAll().get(0);
    }
}
