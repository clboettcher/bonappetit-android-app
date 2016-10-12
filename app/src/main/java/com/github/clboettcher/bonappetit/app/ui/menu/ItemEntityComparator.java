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
