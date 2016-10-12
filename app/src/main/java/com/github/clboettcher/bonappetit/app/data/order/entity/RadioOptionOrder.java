package com.github.clboettcher.bonappetit.app.data.order.entity;

public interface RadioOptionOrder extends OptionOrder {

    public Long getSelectedRadioItemId();

    public void setSelectedRadioItemId(Long id);
}
