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
