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

import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;
import java.util.Collection;

@DatabaseTable(tableName = "OPTION_ORDER")
public class OptionOrderEntity implements CheckboxOptionOrder, ValueOptionOrder, RadioOptionOrder {

    /*
     *****************************************************************************************************************
     * General properties
     *****************************************************************************************************************
     */

    @DatabaseField(generatedId = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "OPTION_ID")
    private Long optionId;

    @DatabaseField(columnName = "OPTION_TITLE")
    private String optionTitle;

    @DatabaseField(columnName = "OPTION_TYPE")
    private OptionEntityType optionType;

    @DatabaseField(columnName = "OPTION_PRICE_DIFF")
    private BigDecimal optionPriceDiff;

    @DatabaseField(columnName = "OPTION_INDEX")
    private Integer optionIndex;

    /**
     * Required by ORMLite to be able to query the foreign collection {@link ItemOrderEntity#getOptionOrderEntities()}.
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private ItemOrderEntity itemOrderEntity;

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
     * Radio option order properties
     *****************************************************************************************************************
     */

    @ForeignCollectionField(eager = true)
    private Collection<RadioItemOrderEntity> availableRadioItemEntities;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private RadioItemOrderEntity selectedRadioItemEntity;

    /*
     *****************************************************************************************************************
     * Accessors
     *****************************************************************************************************************
     */

    public ItemOrderEntity getItemOrderEntity() {
        return itemOrderEntity;
    }

    public void setItemOrderEntity(ItemOrderEntity itemOrderEntity) {
        this.itemOrderEntity = itemOrderEntity;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public OptionEntityType getOptionType() {
        return optionType;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    public void setOptionType(OptionEntityType optionType) {
        this.optionType = optionType;
    }

    public BigDecimal getOptionPriceDiff() {
        return optionPriceDiff;
    }

    public void setOptionPriceDiff(BigDecimal optionPriceDiff) {
        this.optionPriceDiff = optionPriceDiff;
    }

    public Integer getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(Integer optionIndex) {
        this.optionIndex = optionIndex;
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

    public Collection<RadioItemOrderEntity> getAvailableRadioItemEntities() {
        return availableRadioItemEntities;
    }

    public void setAvailableRadioItemEntities(Collection<RadioItemOrderEntity> availableRadioItemEntities) {
        this.availableRadioItemEntities = availableRadioItemEntities;
    }

    public RadioItemOrderEntity getSelectedRadioItemEntity() {
        return selectedRadioItemEntity;
    }

    public void setSelectedRadioItemEntity(RadioItemOrderEntity selectedRadioItemEntity) {
        this.selectedRadioItemEntity = selectedRadioItemEntity;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("optionId", optionId)
                .append("checked", checked)
                .append("value", value)
                .append("availableRadioItemEntities", availableRadioItemEntities)
                .append("selectedRadioItem", selectedRadioItemEntity)
                .toString();
    }
}
