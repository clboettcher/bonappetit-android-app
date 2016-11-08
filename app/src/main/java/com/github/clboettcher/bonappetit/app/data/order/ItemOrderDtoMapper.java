package com.github.clboettcher.bonappetit.app.data.order;


import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.order.entity.CheckboxOptionOrder;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.RadioOptionOrder;
import com.github.clboettcher.bonappetit.server.order.api.dto.*;

import java.util.*;

/**
 * Map item orders and related types to DTOs.
 */
public final class ItemOrderDtoMapper {

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


    private static ItemOrderDto mapToItemOrderDto(ItemOrderEntity order) {
        return ItemOrderDto.builder()
                .itemId(order.getItem().getId())
                .deliverTo(order.getCustomer().getValue())
                .note(order.getNote())
                .orderTime(order.getOrderTime())
                .staffMemberId(order.getStaffMember().getId())
                .optionOrders(mapToOrderOptionDtos(order.getOptionOrderEntities()))
                .build();
    }

    private static Set<OptionOrderDto> mapToOrderOptionDtos(Collection<OptionOrderEntity> optionOrderEntities) {
        Set<OptionOrderDto> orderOptionDtos = new HashSet<>();
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
        return ValueOptionOrderDto.builder()
                .optionId(o.getOption().getId())
                .value(o.getValue())
                .build();
    }

    private static CheckboxOptionOrderDto mapToCheckboxOrderOptionDto(CheckboxOptionOrder o) {
        return CheckboxOptionOrderDto.builder()
                .optionId(o.getOption().getId())
                .checked(o.getChecked())
                .build();
    }

    private static RadioOptionOrderDto mapToRadioOrderOptionDto(RadioOptionOrder o) {
        return RadioOptionOrderDto.builder()
                .selectedRadioItemId(o.getSelectedRadioItem().getId())
                .build();
    }
}
