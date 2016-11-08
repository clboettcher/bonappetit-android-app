package com.github.clboettcher.bonappetit.app.data.order;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.ApiProvider;
import com.github.clboettcher.bonappetit.server.order.api.dto.ItemOrderDto;
import retrofit2.Call;
import retrofit2.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;

@Singleton
public class OrdersService {

    private static final String TAG = OrdersService.class.getName();
    private ApiProvider apiProvider;

    @Inject
    public OrdersService(ApiProvider apiProvider) {
        Log.i(TAG, String.format("Creating %s.", this.getClass().getSimpleName()));
        this.apiProvider = apiProvider;
    }

    public void createOrders(Collection<ItemOrderDto> orders, Callback<Void> callback) {
        Call<Void> call = apiProvider
                .getOrdersApi()
                .createOrders(orders);
        call.enqueue(callback);
    }
}
