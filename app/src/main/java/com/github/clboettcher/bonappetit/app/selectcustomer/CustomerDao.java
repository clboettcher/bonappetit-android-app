package com.github.clboettcher.bonappetit.app.selectcustomer;


import android.util.Log;
import com.github.clboettcher.bonappetit.app.db.BonAppetitDbHelper;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;

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

    public CustomerEntity get() {
        return dao.queryForAll().get(0);
    }
}
