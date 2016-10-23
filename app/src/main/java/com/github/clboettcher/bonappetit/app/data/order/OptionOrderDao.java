package com.github.clboettcher.bonappetit.app.data.order;

import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import javax.inject.Inject;
import java.util.Collection;

public class OptionOrderDao {

    private RuntimeExceptionDao<OptionOrderEntity, Long> dao;

    @Inject
    public OptionOrderDao(BonAppetitDbHelper bonAppetitDbHelper) {
        this.dao = bonAppetitDbHelper
                .getRuntimeExceptionDao(OptionOrderEntity.class);
    }

    public void delete(Collection<OptionOrderEntity> optionOrderEntities) {
        Preconditions.checkNotNull(optionOrderEntities, "optionOrderEntities");
        dao.delete(optionOrderEntities);
    }
}
