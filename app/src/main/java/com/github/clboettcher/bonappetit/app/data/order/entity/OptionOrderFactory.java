package com.github.clboettcher.bonappetit.app.data.order.entity;

public class OptionOrderFactory {

    public static OptionOrderEntity newValueOptionOrder(ItemOrder itemOrder, Long optionId, Integer value) {
        OptionOrderEntity e = new OptionOrderEntity();

        e.setItemOrder(itemOrder);
        e.setOptionId(optionId);
        e.setValue(value);

        return e;
    }

    public static OptionOrderEntity newCheckboxOptionOrder(ItemOrder itemOrder, Long optionId, Boolean checked) {
        OptionOrderEntity e = new OptionOrderEntity();

        e.setItemOrder(itemOrder);
        e.setOptionId(optionId);
        e.setChecked(checked);

        return e;
    }

    public static OptionOrderEntity newRadioOptionOrder(ItemOrder itemOrder,
                                                        Long optionId, Long selectedRadioItemId) {
        OptionOrderEntity e = new OptionOrderEntity();
        e.setItemOrder(itemOrder);
        e.setOptionId(optionId);
        e.setSelectedRadioItemId(selectedRadioItemId);
        return e;
    }
}
