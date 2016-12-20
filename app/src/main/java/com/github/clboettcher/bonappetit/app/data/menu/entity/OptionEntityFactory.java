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
package com.github.clboettcher.bonappetit.app.data.menu.entity;

import java.math.BigDecimal;

/**
 * Provides factory methods to create objects modelling the subtypes of {@link Option}.
 */
public class OptionEntityFactory {

    public static OptionEntity newValueOption(Long id, String title, Integer index, BigDecimal priceDiff,
                                              Integer defaultValue, ItemEntity item) {

        OptionEntity optionEntity = new OptionEntity();
        optionEntity.setId(id);
        optionEntity.setTitle(title);
        optionEntity.setIndex(index);
        optionEntity.setPriceDiff(priceDiff);
        optionEntity.setDefaultValue(defaultValue);
        optionEntity.setItem(item);
        optionEntity.setType(OptionEntityType.VALUE);

        return optionEntity;
    }

    public static OptionEntity newCheckboxOption(Long id, String name, Integer index,
                                                 BigDecimal priceDiff, Boolean defaultChecked, ItemEntity item) {

        OptionEntity optionEntity = new OptionEntity();
        optionEntity.setId(id);
        optionEntity.setTitle(name);
        optionEntity.setIndex(index);
        optionEntity.setPriceDiff(priceDiff);
        optionEntity.setDefaultChecked(defaultChecked);
        optionEntity.setItem(item);
        optionEntity.setType(OptionEntityType.CHECKBOX);

        return optionEntity;
    }

    public static OptionEntity newRadioOption(Long id, String title, Integer index, ItemEntity item) {
        OptionEntity optionEntity = new OptionEntity();

        optionEntity.setId(id);
        optionEntity.setTitle(title);
        optionEntity.setIndex(index);
        optionEntity.setItem(item);
        optionEntity.setType(OptionEntityType.RADIO);

        return optionEntity;
    }
}
