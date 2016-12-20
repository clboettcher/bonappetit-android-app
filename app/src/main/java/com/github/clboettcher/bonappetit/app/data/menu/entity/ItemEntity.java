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
package com.github.clboettcher.bonappetit.app.data.menu.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;
import java.util.Collection;

@DatabaseTable(tableName = "ITEM")
public class ItemEntity {

    @DatabaseField(id = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "TITLE")
    private String title;

    @DatabaseField(columnName = "PRICE")
    private BigDecimal price;

    @DatabaseField(columnName = "TYPE")
    private ItemEntityType type;

    /**
     * Required by ORMLite to be able to query the foreign collection {@link MenuEntity#getItems()}.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private MenuEntity menu;

    @ForeignCollectionField(eager = true)
    private Collection<OptionEntity> options;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public ItemEntityType getType() {
        return type;
    }

    public void setType(ItemEntityType type) {
        this.type = type;
    }

    public MenuEntity getMenu() {
        return menu;
    }

    public void setMenu(MenuEntity menu) {
        this.menu = menu;
    }

    public Collection<OptionEntity> getOptions() {
        return options;
    }

    public void setOptions(Collection<OptionEntity> options) {
        this.options = options;
    }

    public boolean hasOptions() {
        return CollectionUtils.isNotEmpty(getOptions());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("title", title)
                .append("price", price)
                .append("type", type)
                .append("options.size()", CollectionUtils.size(options))
                .toString();
    }
}
