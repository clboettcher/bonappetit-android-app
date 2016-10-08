package com.github.clboettcher.bonappetit.app.menu.entity;

import java.util.Collection;

public interface RadioOption extends Option {
    Collection<RadioItemEntity> getRadioItemEntities();

    void setRadioItemEntities(Collection<RadioItemEntity> radioItemEntities);

    void setDefaultSelectedItem(RadioItemEntity radioItemEntity);

    RadioItemEntity getDefaultSelectedItem();

}
