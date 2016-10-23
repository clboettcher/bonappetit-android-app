package com.github.clboettcher.bonappetit.app.data.order.entity;

import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioItemEntity;

public interface RadioOptionOrder extends OptionOrder {

    RadioItemEntity getSelectedRadioItem();

    void setSelectedRadioItem(RadioItemEntity id);
}
