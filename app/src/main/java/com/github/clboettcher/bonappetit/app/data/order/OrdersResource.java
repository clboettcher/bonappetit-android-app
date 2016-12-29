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

import android.util.Log;
import com.github.clboettcher.bonappetit.app.core.ConfigProvider;
import com.github.clboettcher.bonappetit.app.data.ErrorCode;
import com.github.clboettcher.bonappetit.app.data.ErrorMapper;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.event.FinishOrdersCompletedEvent;
import com.github.clboettcher.bonappetit.server.order.api.dto.write.ItemOrderCreationDto;
import org.apache.commons.collections4.CollectionUtils;
import org.greenrobot.eventbus.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class OrdersResource {

    private static final String TAG = OrdersResource.class.getName();
    private OrderDao orderDao;
    private OrdersService ordersService;
    private EventBus eventBus;
    private ConfigProvider configProvider;
    private AtomicReference<Loadable<Void>> finishOrdersLoadable
            = new AtomicReference<>(Loadable.<Void>initial());

    @Inject
    public OrdersResource(OrdersService ordersService,
                          EventBus eventBus,
                          OrderDao orderDao,
                          ConfigProvider configProvider
    ) {
        this.orderDao = orderDao;
        this.ordersService = ordersService;
        this.eventBus = eventBus;
        this.configProvider = configProvider;
    }

    public void finishOrders(List<ItemOrderEntity> orders) {
        List<ItemOrderCreationDto> orderDtos = ItemOrderCreationDtoMapper.mapToItemOrderCreationDtos(orders);
        Log.i(TAG, String.format("Finishing %d order(s).", CollectionUtils.size(orderDtos)));
        this.finishOrdersLoadable.set(Loadable.<Void>loading());

        if (configProvider.useTestData()) {
            fakeFinishOrders();
        } else {
            finishOrdersWithServerCall(orderDtos);
        }
    }

    public void finishOrdersWithServerCall(List<ItemOrderCreationDto> orderDtos) {
        this.ordersService.createOrders(orderDtos, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    finishOrdersLoadable.set(Loadable.<Void>loaded(null));
                } else {
                    finishOrdersLoadable.set(Loadable.<Void>failed(ErrorMapper.toErrorCode(response.code())));
                }
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

    public void fakeFinishOrders() {
        Log.i(TAG, "Test data enabled. Fake finish orders operation with success result.");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                finishOrdersLoadable.set(Loadable.<Void>loaded(null));
                eventBus.post(new FinishOrdersCompletedEvent());
            }
        }).start();
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

    public void deleteAllOrders() {
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

    public List<ItemOrderEntity> list() {
        return orderDao.list();
    }
}
