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

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

@DatabaseTable(tableName = "RADIO_ITEM_ORDER")
public class RadioItemOrderEntity {

    @DatabaseField(generatedId = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "RADIO_ITEM_ID", canBeNull = false)
    private Long radioItemId;

    @DatabaseField(columnName = "RADIO_ITEM_INDEX", canBeNull = false)
    private Integer index;

    @DatabaseField(columnName = "RADIO_ITEM_TITLE", canBeNull = false)
    private String title;

    @DatabaseField(columnName = "RADIO_ITEM_PRICE_DIFF", canBeNull = false)
    private BigDecimal priceDiff;

    /**
     * Required by ORMLite to be able to query the foreign collection
     * {@link OptionOrderEntity#getAvailableRadioItemEntities()}.
     */
    @DatabaseField(canBeNull = true, foreign = true)
    private OptionOrderEntity optionOrder;

    public Long getRadioItemId() {
        return radioItemId;
    }

    public void setRadioItemId(Long radioItemId) {
        this.radioItemId = radioItemId;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPriceDiff() {
        return priceDiff;
    }

    public void setPriceDiff(BigDecimal priceDiff) {
        this.priceDiff = priceDiff;
    }

    public OptionOrderEntity getOptionOrder() {
        return optionOrder;
    }

    public void setOptionOrder(OptionOrderEntity optionOrder) {
        this.optionOrder = optionOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("radioItemId", radioItemId)
                .append("index", index)
                .append("title", title)
                .append("priceDiff", priceDiff)
                .toString();
    }
}
