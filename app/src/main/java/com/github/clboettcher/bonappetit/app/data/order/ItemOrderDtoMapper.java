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
import com.github.clboettcher.bonappetit.app.data.menu.entity.*;
import com.github.clboettcher.bonappetit.app.data.order.entity.CheckboxOptionOrder;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.RadioOptionOrder;
import com.github.clboettcher.bonappetit.server.menu.api.dto.common.ItemDtoType;
import com.github.clboettcher.bonappetit.server.order.api.dto.read.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemOrderDtoMapper {
    /**
     * No instance.
     */
    private ItemOrderDtoMapper() {
    }

    public static List<ItemOrderDto> mapToItemOrderDtos(List<ItemOrderEntity> orders) {
        List<ItemOrderDto> result = new ArrayList<>();
        for (ItemOrderEntity order : orders) {
            result.add(mapToItemOrderDto(order));
        }
        return result;
    }


    public static ItemOrderDto mapToItemOrderDto(ItemOrderEntity order) {
        ItemEntity orderedItem = order.getItem();
        return ItemOrderDto.builder()
                .itemId(orderedItem.getId())
                .itemType(mapType(orderedItem.getType()))
                .itemTitle(orderedItem.getTitle())
                .itemPrice(orderedItem.getPrice())
                .customer(mapToCustomerDto(order.getCustomer()))
                .note(order.getNote())
                .orderTime(order.getOrderTime())
                .staffMemberId(order.getStaffMember().getId())
                .optionOrders(mapToOrderOptionDtos(order.getOptionOrderEntities()))
                .build();
    }

    private static CustomerDto mapToCustomerDto(CustomerEntity customer) {
        switch (customer.getType()) {
            case TABLE:
                return TableCustomerDto.builder()
                        .tableNumber(customer.getTableNumber())
                        .build();
            case FREE_TEXT:
                return FreeTextCustomerDto.builder()
                        .value(customer.getValue())
                        .build();
            case STAFF_MEMBER:
                return StaffMemberCustomerDto.builder()
                        .staffMemberId(customer.getStaffMember().getId())
                        .build();
            default:
                throw new IllegalArgumentException(String.format("Unknown enum value %s.%s",
                        CustomerEntityType.class.getName(),
                        customer.getType()));
        }
    }

    private static ItemDtoType mapType(ItemEntityType type) {
        if (type == null) {
            return null;
        }
        return ItemDtoType.valueOf(type.name());
    }

    private static List<OptionOrderDto> mapToOrderOptionDtos(Collection<OptionOrderEntity> optionOrderEntities) {
        List<OptionOrderDto> orderOptionDtos = new ArrayList<>();
        if (optionOrderEntities != null) {
            for (OptionOrderEntity optionOrderEntity : optionOrderEntities) {
                orderOptionDtos.add(mapToOrderOptionDto(optionOrderEntity));
            }
        }

        return orderOptionDtos;
    }

    private static OptionOrderDto mapToOrderOptionDto(OptionOrderEntity optionOrderEntity) {
        final OptionEntity option = optionOrderEntity.getOption();
        if (option.getType() == OptionEntityType.VALUE) {
            return mapToValueOptionDto(optionOrderEntity);
        } else if (option.getType() == OptionEntityType.CHECKBOX) {
            return mapToCheckboxOrderOptionDto(optionOrderEntity);
        } else if (option.getType() == OptionEntityType.RADIO) {
            return mapToRadioOrderOptionDto(optionOrderEntity);
        } else {
            throw new IllegalStateException(String.format("Unknown enum value %s.%s",
                    OptionEntityType.class.getSimpleName(),
                    option.getType()));
        }
    }

    private static ValueOptionOrderDto mapToValueOptionDto(OptionOrderEntity o) {
        OptionEntity orderedOption = o.getOption();
        return ValueOptionOrderDto.builder()
                .valueOptionId(orderedOption.getId())
                .optionTitle(orderedOption.getTitle())
                .optionPriceDiff(orderedOption.getPriceDiff())
                .value(o.getValue())
                .build();
    }

    private static CheckboxOptionOrderDto mapToCheckboxOrderOptionDto(CheckboxOptionOrder o) {
        OptionEntity orderedOption = o.getOption();
        return CheckboxOptionOrderDto.builder()
                .checkboxOptionId(orderedOption.getId())
                .optionTitle(orderedOption.getTitle())
                .optionPriceDiff(orderedOption.getPriceDiff())
                .checked(o.getChecked())
                .build();
    }

    private static RadioOptionOrderDto mapToRadioOrderOptionDto(RadioOptionOrder o) {
        RadioItemEntity orderedRadioItem = o.getSelectedRadioItem();
        return RadioOptionOrderDto.builder()
                .selectedRadioItemId(orderedRadioItem.getId())
                .selectedRadioItemTitle(orderedRadioItem.getTitle())
                .selectedRadioItemPriceDiff(orderedRadioItem.getPriceDiff())
                .build();
    }
}
