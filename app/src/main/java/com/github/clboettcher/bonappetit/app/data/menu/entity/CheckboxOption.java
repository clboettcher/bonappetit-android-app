package com.github.clboettcher.bonappetit.app.data.menu.entity;

import java.math.BigDecimal;

public interface CheckboxOption extends Option {

    BigDecimal getPriceDiff();

    Boolean getDefaultChecked();
}
