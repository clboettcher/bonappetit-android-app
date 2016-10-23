package com.github.clboettcher.bonappetit.app.data.order.entity;


import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;

public interface OptionOrder {
    
    Long getId();

    OptionEntity getOption();
}
