/*
 * Copyright (c) 2016 Claudius Boettcher (pos.bonappetit@gmail.com)
 *
 * This file is part of BonAppetit. BonAppetit is an Android based
 * Point-of-Sale client-server application for small restaurants.
 *
 * BonAppetit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonAppetit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BonAppetit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.clboettcher.bonappetit.app.data.order.entity;

import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioItemEntity;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;

@DatabaseTable(tableName = "OPTION_ORDER")
public class OptionOrderEntity implements CheckboxOptionOrder, ValueOptionOrder, RadioOptionOrder {

    /*
     *****************************************************************************************************************
     * General properties
     *****************************************************************************************************************
     */

    @DatabaseField(generatedId = true, columnName = "ID")
    private Long id;

    /**
     * Required by ORMLite to be able to query the foreign collection {@link ItemOrderEntity#getOptionOrderEntities()}.
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private ItemOrderEntity itemOrderEntity;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = false)
    private OptionEntity option;

    /*
     *****************************************************************************************************************
     * Properties for checkbox option.
     *****************************************************************************************************************
     */

    @DatabaseField(columnName = "CHECKED")
    private Boolean checked;

    /*
     *****************************************************************************************************************
     * Properties for value option
     *****************************************************************************************************************
     */

    @DatabaseField(columnName = "VALUE")
    private Integer value;

    /*
     *****************************************************************************************************************
     * Properties for radio option
     *****************************************************************************************************************
     */

    @DatabaseField(foreign = true, canBeNull = true, foreignAutoRefresh = true)
    private RadioItemEntity selectedRadioItem;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemOrderEntity getItemOrderEntity() {
        return itemOrderEntity;
    }

    public void setItemOrderEntity(ItemOrderEntity itemOrderEntity) {
        this.itemOrderEntity = itemOrderEntity;
    }

    public OptionEntity getOption() {
        return option;
    }

    public void setOption(OptionEntity option) {
        this.option = option;
    }

    @Override
    public Boolean getChecked() {
        return checked;
    }

    @Override
    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Override
    public Integer getValue() {
        return value;
    }

    @Override
    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public RadioItemEntity getSelectedRadioItem() {
        return selectedRadioItem;
    }

    public void setSelectedRadioItem(RadioItemEntity selectedRadioItem) {
        this.selectedRadioItem = selectedRadioItem;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("itemOrderEntity.id", itemOrderEntity.getId())
                .append("option", option)
                .append("checked", checked)
                .append("value", value)
                .append("selectedRadioItem.id",
                        selectedRadioItem != null ?
                                selectedRadioItem.getId() : "--")
                .toString();
    }
}
