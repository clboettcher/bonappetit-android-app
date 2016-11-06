package com.github.clboettcher.bonappetit.app.util;

import com.gihub.clboettcher.price_calculation.api.entity.*;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.RadioOptionOrder;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PricesMapper {

    /**
     * No instance
     */
    private PricesMapper() {
    }

    public static ItemOrderPrices mapToItemOrderPrices(ItemOrderEntity itemOrder) {
        BigDecimal itemPrice = itemOrder.getItem().getPrice();
        List<OptionOrderPrices> optionOrderPrices = mapToOptionOrderPrices(itemOrder.getOptionOrderEntities());

        return ItemOrderPrices.builder()
                .price(itemPrice)
                .optionOrderPrices(optionOrderPrices)
                .build();
    }

    private static List<OptionOrderPrices> mapToOptionOrderPrices(Collection<OptionOrderEntity> optionOrders) {
        final ArrayList<OptionOrderPrices> result = Lists.newArrayListWithCapacity(optionOrders.size());
        for (OptionOrderEntity optionOrder : optionOrders) {
            OptionOrderPrices orderPrices = mapToOptionOrderPrice(optionOrder);
            if (orderPrices != null) {
                result.add(orderPrices);
            }
        }
        return result;
    }

    private static OptionOrderPrices mapToOptionOrderPrice(OptionOrderEntity optionOrder) {
        if (optionOrder.getOption().getType() == OptionEntityType.VALUE) {
            return mapToValueOptionPrices(optionOrder);
        } else if (optionOrder.getOption().getType() == OptionEntityType.CHECKBOX) {
            return mapToCheckboxOrderOptionPrices(optionOrder);
        } else if (optionOrder.getOption().getType() == OptionEntityType.RADIO) {
            return mapToRadioOrderOptionPrices(optionOrder);
        } else {
            throw new IllegalArgumentException("Bad order option type: " + optionOrder.getClass().getName());
        }
    }

    private static ValueOptionOrderPrices mapToValueOptionPrices(OptionOrderEntity orderOption) {
        return ValueOptionOrderPrices.builder()
                .value(orderOption.getValue())
                .priceDiff(orderOption.getOption().getPriceDiff())
                .build();
    }

    private static CheckboxOptionOrderPrices mapToCheckboxOrderOptionPrices(OptionOrderEntity orderOption) {
        return CheckboxOptionOrderPrices.builder()
                .checked(orderOption.getChecked())
                .priceDiff(orderOption.getOption().getPriceDiff())
                .build();
    }

    private static RadioOptionOrderPrices mapToRadioOrderOptionPrices(RadioOptionOrder orderOption) {
        return RadioOptionOrderPrices.builder()
                .selectedItemPriceDiff(orderOption.getSelectedRadioItem().getPriceDiff())
                .build();
    }


}
