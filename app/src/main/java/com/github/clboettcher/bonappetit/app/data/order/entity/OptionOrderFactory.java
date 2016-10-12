package com.github.clboettcher.bonappetit.app.data.order.entity;

public class OptionOrderFactory {

    public static OptionOrderEntity newValueOptionOrder(ItemOrderEntity itemOrderEntity, Long optionId, Integer value) {
        OptionOrderEntity e = new OptionOrderEntity();

        e.setItemOrderEntity(itemOrderEntity);
        e.setOptionId(optionId);
        e.setValue(value);

        return e;
    }

    public static OptionOrderEntity newCheckboxOptionOrder(ItemOrderEntity itemOrderEntity, Long optionId, Boolean checked) {
        OptionOrderEntity e = new OptionOrderEntity();

        e.setItemOrderEntity(itemOrderEntity);
        e.setOptionId(optionId);
        e.setChecked(checked);

        return e;
    }

    public static OptionOrderEntity newRadioOptionOrder(ItemOrderEntity itemOrderEntity,
                                                        Long optionId, Long selectedRadioItemId) {
        OptionOrderEntity e = new OptionOrderEntity();
        e.setItemOrderEntity(itemOrderEntity);
        e.setOptionId(optionId);
        e.setSelectedRadioItemId(selectedRadioItemId);
        return e;
    }
}
