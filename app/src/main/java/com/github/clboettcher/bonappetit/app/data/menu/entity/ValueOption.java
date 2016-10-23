package com.github.clboettcher.bonappetit.app.data.menu.entity;

import java.math.BigDecimal;

/**
 *
 */
public interface ValueOption extends Option{
    BigDecimal getPriceDiff();

    Integer getDefaultValue();

}
