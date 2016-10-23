package com.github.clboettcher.bonappetit.app.data.order;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.BonAppetitDbHelper;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;

public class OrderDao {

    private static final String TAG = OrderDao.class.getName();
    private RuntimeExceptionDao<ItemOrderEntity, Long> itemOrderDao;
    private OptionOrderDao optionOrderDao;

    @Inject
    public OrderDao(BonAppetitDbHelper bonAppetitDbHelper, OptionOrderDao optionOrderDao) {
        this.itemOrderDao = bonAppetitDbHelper
                .getRuntimeExceptionDao(ItemOrderEntity.class);
        this.optionOrderDao = optionOrderDao;
    }

    public void save(ItemOrderEntity itemOrderEntity) {
        Log.i(TAG, String.format("Saving order %s", itemOrderEntity));
        itemOrderDao.create(itemOrderEntity);
    }

    public void update(ItemOrderEntity itemOrder) {
        Log.i(TAG, String.format("Updating order %s", itemOrder));
        itemOrderDao.update(itemOrder);
    }

    public ItemOrderEntity get(Long orderId) {
        return itemOrderDao.queryForId(orderId);
    }

    public void delete(ItemOrderEntity itemOrder) {
        if (CollectionUtils.isNotEmpty(itemOrder.getOptionOrderEntities())) {
            optionOrderDao.delete(itemOrder.getOptionOrderEntities());
        }
        itemOrderDao.delete(itemOrder);
    }
}
