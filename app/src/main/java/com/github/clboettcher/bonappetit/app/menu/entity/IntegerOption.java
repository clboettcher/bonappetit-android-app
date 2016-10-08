package com.github.clboettcher.bonappetit.app.menu.entity;

import java.math.BigDecimal;

/**
 *
 */
public interface IntegerOption extends Option{
    BigDecimal getPriceDiff();

    Integer getDefaultValue();

}
