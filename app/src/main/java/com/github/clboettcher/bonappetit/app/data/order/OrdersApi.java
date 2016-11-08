package com.github.clboettcher.bonappetit.app.data.order;

import com.github.clboettcher.bonappetit.server.order.api.OrderManagement;
import com.github.clboettcher.bonappetit.server.order.api.dto.ItemOrderDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.util.Collection;

public interface OrdersApi {

    @POST(OrderManagement.ROOT_PATH)
    Call<Void> createOrders(@Body Collection<ItemOrderDto> orders);
}
