package com.github.clboettcher.bonappetit.app.ui.editorder;

import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioItemEntity;

import java.util.Comparator;

/**
 * {@link Comparator} that can be used to sort {@link RadioItemEntity} by index.
 */
class RadioItemEntityComparator implements Comparator<RadioItemEntity> {

    public static final RadioItemEntityComparator INSTANCE = new RadioItemEntityComparator();

    @Override
    public int compare(RadioItemEntity lhs, RadioItemEntity rhs) {
        final Integer i1 = lhs.getIndex();
        final Integer i2 = rhs.getIndex();

        return i1.compareTo(i2);
    }
}
