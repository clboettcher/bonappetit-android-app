package com.github.clboettcher.bonappetit.app.data.order.entity;

public interface ValueOptionOrder extends OptionOrder {
    Integer getValue();

    void setValue(Integer value);
}
