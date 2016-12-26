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


import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntityType;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.order.entity.CheckboxOptionOrder;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.RadioOptionOrder;
import com.github.clboettcher.bonappetit.server.order.api.dto.write.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Map item orders and related types to DTOs.
 */
public final class ItemOrderCreationDtoMapper {

    /**
     * No instance.
     */
    private ItemOrderCreationDtoMapper() {
    }

    public static List<ItemOrderCreationDto> mapToItemOrderCreationDtos(List<ItemOrderEntity> orders) {
        List<ItemOrderCreationDto> result = new ArrayList<>();
        for (ItemOrderEntity order : orders) {
            result.add(mapToItemOrderDto(order));
        }
        return result;
    }


    public static ItemOrderCreationDto mapToItemOrderDto(ItemOrderEntity order) {
        return ItemOrderCreationDto.builder()
                .itemId(order.getItemId())
                .customer(mapToCustomerCreationDto(order.getCustomer()))
                .note(order.getNote())
                .orderTime(order.getOrderTime())
                .staffMemberId(order.getSelectedStaffMember().getStaffMemberId())
                .optionOrders(mapToOptionOrderCreationDtos(order.getOptionOrderEntities()))
                .build();
    }

    private static CustomerCreationDto mapToCustomerCreationDto(CustomerEntity customer) {
        switch (customer.getType()) {
            case TABLE:
                return TableCustomerCreationDto.builder()
                        .tableNumber(customer.getTableNumber())
                        .build();
            case FREE_TEXT:
                return FreeTextCustomerCreationDto.builder()
                        .value(customer.getValue())
                        .build();
            case STAFF_MEMBER:
                return StaffMemberCustomerCreationDto.builder()
                        .staffMemberId(customer.getStaffMember().getId())
                        .build();
            default:
                throw new IllegalArgumentException(String.format("Unknown enum value %s.%s",
                        CustomerEntityType.class.getName(),
                        customer.getType()));
        }
    }

    private static List<OptionOrderCreationDto> mapToOptionOrderCreationDtos(
            Collection<OptionOrderEntity> optionOrderEntities) {
        List<OptionOrderCreationDto> orderOptionDtos = new ArrayList<>();
        if (optionOrderEntities != null) {
            for (OptionOrderEntity optionOrderEntity : optionOrderEntities) {
                orderOptionDtos.add(mapToOptionOrderCreationDto(optionOrderEntity));
            }
        }

        return orderOptionDtos;
    }

    private static OptionOrderCreationDto mapToOptionOrderCreationDto(OptionOrderEntity optionOrder) {
        if (optionOrder.getOptionType() == OptionEntityType.VALUE) {
            return mapToValueOptionOrderCreationDto(optionOrder);
        } else if (optionOrder.getOptionType() == OptionEntityType.CHECKBOX) {
            return mapToCheckboxOptionOrderCreationDto(optionOrder);
        } else if (optionOrder.getOptionType() == OptionEntityType.RADIO) {
            return mapToRadioOptionOrderCreationDto(optionOrder);
        } else {
            throw new IllegalStateException(String.format("Unknown enum value %s.%s",
                    OptionEntityType.class.getSimpleName(),
                    optionOrder.getOptionType()));
        }
    }

    private static ValueOptionOrderCreationDto mapToValueOptionOrderCreationDto(OptionOrderEntity optionOrder) {
        return ValueOptionOrderCreationDto.builder()
                .optionId(optionOrder.getOptionId())
                .value(optionOrder.getValue())
                .build();
    }

    private static CheckboxOptionOrderCreationDto mapToCheckboxOptionOrderCreationDto(CheckboxOptionOrder optionOrder) {
        return CheckboxOptionOrderCreationDto.builder()
                .optionId(optionOrder.getOptionId())
                .checked(optionOrder.getChecked())
                .build();
    }

    private static RadioOptionOrderCreationDto mapToRadioOptionOrderCreationDto(RadioOptionOrder optionOrder) {
        return RadioOptionOrderCreationDto.builder()
                .selectedRadioItemId(optionOrder.getSelectedRadioItemEntity().getRadioItemId())
                .build();
    }
}
