package com.github.clboettcher.bonappetit.app.menu.entity;

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
