package com.github.clboettcher.bonappetit.app.data.order;

import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.order.entity.RadioItemOrderEntity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.table.TableUtils;

import javax.inject.Inject;
import java.sql.SQLException;
import java.util.Collection;

public class RadioItemOrderDao {

    private RuntimeExceptionDao<RadioItemOrderEntity, Long> radioItemOrderDao;
    private BonAppetitDbHelper bonAppetitDbHelper;

    @Inject
    public RadioItemOrderDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.bonAppetitDbHelper = bonAppetitDbHelper;
        this.radioItemOrderDao = bonAppetitDbHelper.getRuntimeExceptionDao(RadioItemOrderEntity.class);
    }

    public void save(Collection<RadioItemOrderEntity> radioItemEntities) {
        this.radioItemOrderDao.create(radioItemEntities);
    }

    public void deleteAll() throws SQLException {
        TableUtils.clearTable(bonAppetitDbHelper.getConnectionSource(), RadioItemOrderEntity.class);
    }
}
