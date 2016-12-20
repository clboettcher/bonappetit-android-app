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
package com.github.clboettcher.bonappetit.app.ui.menu;

import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntityType;
import com.google.common.collect.ImmutableMap;

import java.util.Comparator;
import java.util.Map;

/**
 * {@link Comparator} for {@link ItemEntity}s that can be used to
 * sort by type (see {@link ItemEntity#getType()}) then by title
 * (see {@link ItemEntity#getTitle()} ).
 */
class ItemEntityComparator implements Comparator<ItemEntity> {


    private static final Map<ItemEntityType, Integer> ITEM_TYPE_TO_DISPLAY_INDEX =
            ImmutableMap.<ItemEntityType, Integer>builder()
                    .put(ItemEntityType.DRINK_NON_ALCOHOLIC, 0)
                    .put(ItemEntityType.DRINK_ALCOHOLIC, 1)
                    .put(ItemEntityType.FOOD, 2)
                    .build();

    @Override
    public int compare(ItemEntity lhs, ItemEntity rhs) {
        // First sort by type
        int result = ITEM_TYPE_TO_DISPLAY_INDEX.get(lhs.getType()).compareTo(
                ITEM_TYPE_TO_DISPLAY_INDEX.get(rhs.getType()));

        // Within a type sort by title
        if (result == 0) {
            result = lhs.getTitle().compareTo(rhs.getTitle());
        }

        return result;
    }
}
