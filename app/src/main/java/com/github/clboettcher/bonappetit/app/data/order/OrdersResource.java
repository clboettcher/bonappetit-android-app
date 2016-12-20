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
package com.github.clboettcher.bonappetit.app.data.order;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.data.ErrorCode;
import com.github.clboettcher.bonappetit.app.data.ErrorMapper;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerDao;
import com.github.clboettcher.bonappetit.app.data.menu.dao.ItemDao;
import com.github.clboettcher.bonappetit.app.data.menu.dao.OptionDao;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.event.FinishOrdersCompletedEvent;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.server.order.api.dto.read.ItemOrderDto;
import org.apache.commons.collections4.CollectionUtils;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class OrdersResource {

    private static final String TAG = OrdersResource.class.getName();
    private OrderDao orderDao;
    private OrdersService ordersService;
    private EventBus eventBus;
    private StaffMemberDao staffMemberDao;
    private CustomerDao customerDao;
    private ItemDao itemDao;
    private Context context;
    private OptionOrderDao optionOrderDao;
    private OptionDao optionDao;
    private AtomicReference<Loadable<Void>> finishOrdersLoadable
            = new AtomicReference<>(Loadable.<Void>initial());

    @Inject
    public OrdersResource(Context context, OrdersService ordersService, EventBus eventBus, OrderDao orderDao,
                          StaffMemberDao staffMemberDao,
                          CustomerDao customerDao,
                          ItemDao itemDao, OptionOrderDao optionOrderDao, OptionDao optionDao) {
        this.orderDao = orderDao;
        this.ordersService = ordersService;
        this.eventBus = eventBus;
        this.staffMemberDao = staffMemberDao;
        this.customerDao = customerDao;
        this.itemDao = itemDao;
        this.context = context;
        this.optionOrderDao = optionOrderDao;
        this.optionDao = optionDao;
    }

    public void finishOrders(List<ItemOrderEntity> orders) {
        List<ItemOrderDto> orderDtos = ItemOrderDtoMapper.mapToItemOrderDtos(orders);
        Log.i(TAG, String.format("Finishing %d order(s).", CollectionUtils.size(orderDtos)));
        this.finishOrdersLoadable.set(Loadable.<Void>loading());
        this.ordersService.createOrders(orderDtos, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    finishOrdersLoadable.set(Loadable.<Void>loaded(null));
                } else {
                    finishOrdersLoadable.set(Loadable.<Void>failed(ErrorMapper.toErrorCode(response.code())));
                }
                // The orders are now saved in the server. No need for local copies anymore.
                deleteAllOrders();
                eventBus.post(new FinishOrdersCompletedEvent());
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ErrorCode errorCode = ErrorMapper.toErrorCode(t);
                finishOrdersLoadable.set(Loadable.<Void>failed(errorCode));
                eventBus.post(new FinishOrdersCompletedEvent());
            }
        });
    }

    public Loadable<Void> getFinishOrdersLoadable() {
        return finishOrdersLoadable.get();
    }

    public void deleteOrder(ItemOrderEntity order) {
        orderDao.delete(order);
    }


    public long count() {
        return orderDao.count();
    }

    private void deleteAllOrders() {
        try {
            orderDao.deleteAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Reinitializes the loadable provided by {@link #getFinishOrdersLoadable()}
     * to its initial value.
     * <p/>
     * This method is called to reset this resource after a completed finish orders
     * operation. It is called after successful or failed operations in order to put
     * the resource in the correct state for the next finish orders operation.
     */
    public void reset() {
        Log.i(TAG, "Resetting loadable.");
        this.finishOrdersLoadable.set(Loadable.<Void>initial());
    }

    public List<ItemOrderEntity> listRefreshedOrders() {
        List<ItemOrderEntity> rawOrders = orderDao.list();

        // We need to refresh the orders fields. This might fail if the referenced entity has been deleted.
        List<ItemOrderEntity> ordersToDelete = new ArrayList<>();
        for (ItemOrderEntity rawOrder : rawOrders) {
            // Staff member must be present.
            StaffMemberEntity staffMember = rawOrder.getStaffMember();
            staffMemberDao.refresh(staffMember);

            // The referenced item might be gone
            boolean itemExists = itemDao.exists(rawOrder.getItem().getId());
            if (itemExists) {
                itemDao.refresh(rawOrder.getItem());

                // Also refresh options. No need to check for existence here. If the item exists the
                // option is guaranteed to exist.
                Collection<OptionOrderEntity> optionOrderEntities = rawOrder.getOptionOrderEntities();
                for (OptionOrderEntity optionOrderEntity : optionOrderEntities) {
                    optionDao.refresh(optionOrderEntity.getOption());
                }
            } else {
                ordersToDelete.add(rawOrder);
            }
        }

        List<ItemOrderEntity> refreshedOrders = new ArrayList<>();
        refreshedOrders.addAll(rawOrders);
        refreshedOrders.removeAll(ordersToDelete);

        for (ItemOrderEntity itemOrderEntity : ordersToDelete) {
            Toast.makeText(context,
                    context.getString(R.string.activity_edit_order_item_removed),
                    Toast.LENGTH_LONG).show();
            orderDao.delete(itemOrderEntity);
        }

        return refreshedOrders;
    }
}
