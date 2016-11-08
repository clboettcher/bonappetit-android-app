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
