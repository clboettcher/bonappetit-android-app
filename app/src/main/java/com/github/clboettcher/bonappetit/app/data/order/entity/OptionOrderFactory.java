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
package com.github.clboettcher.bonappetit.app.data.order.entity;

import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioItemEntity;

import java.util.ArrayList;
import java.util.Collection;

public class OptionOrderFactory {

    /**
     * Creates a new {@link OptionOrderEntity} instance for each given option.
     * <p>
     * The orders will have the default config found in the option. E.g.
     * {@link OptionOrderEntity#getValue()} will be {@link OptionEntity#getDefaultValue()} for
     * options with type {@link OptionEntityType#VALUE}.
     *
     * @param options The options to create orders for.
     * @return The new orders.
     */
    public static Collection<OptionOrderEntity> createOrders(Collection<OptionEntity> options,
                                                             ItemOrderEntity itemOrder) {
        Collection<OptionOrderEntity> orders = new ArrayList<>();
        for (OptionEntity option : options) {
            // Create an order for each option
            OptionOrderEntity order;
            switch (option.getType()) {
                case CHECKBOX:
                    order = newCheckboxOptionOrder(itemOrder,
                            option,
                            option.getDefaultChecked());
                    break;
                case VALUE:
                    order = newValueOptionOrder(itemOrder,
                            option,
                            option.getDefaultValue());
                    break;
                case RADIO:
                    order = newRadioOptionOrder(itemOrder,
                            option,
                            option.getDefaultSelectedItem()
                    );

                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown enum value %s.%s",
                            OptionEntityType.class.getName(),
                            option.getType()));
            }
            orders.add(order);
        }
        return orders;
    }

    private static OptionOrderEntity newValueOptionOrder(ItemOrderEntity itemOrderEntity,
                                                         OptionEntity option, Integer value) {
        OptionOrderEntity e = new OptionOrderEntity();

        e.setItemOrderEntity(itemOrderEntity);
        e.setOption(option);
        e.setValue(value);

        return e;
    }

    private static OptionOrderEntity newCheckboxOptionOrder(ItemOrderEntity itemOrderEntity,
                                                            OptionEntity option, Boolean checked) {
        OptionOrderEntity e = new OptionOrderEntity();

        e.setItemOrderEntity(itemOrderEntity);
        e.setOption(option);
        e.setChecked(checked);

        return e;
    }

    private static OptionOrderEntity newRadioOptionOrder(ItemOrderEntity itemOrderEntity,
                                                         OptionEntity option, RadioItemEntity selectedRadioItem) {
        OptionOrderEntity e = new OptionOrderEntity();
        e.setItemOrderEntity(itemOrderEntity);
        e.setOption(option);
        e.setSelectedRadioItem(selectedRadioItem);
        return e;
    }
}
