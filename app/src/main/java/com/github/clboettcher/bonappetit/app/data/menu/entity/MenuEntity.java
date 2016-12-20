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

import java.util.Collection;

@DatabaseTable(tableName = "MENU")
public class MenuEntity {

    /**
     * The ID identifying the current menu. This is really the only menu in
     * the db we work with.
     */
    public static final String ID_CURRENT = "current";

    /**
     * The ID.
     */
    @DatabaseField(id = true, columnName = "ID", canBeNull = false)
    private String id = MenuEntity.ID_CURRENT;

    /**
     * The items that this menu consists of.
     */
    @ForeignCollectionField(eager = true)
    private Collection<ItemEntity> items;

    public Collection<ItemEntity> getItems() {
        return items;
    }

    public void setItems(Collection<ItemEntity> items) {
        this.items = items;
    }

    public boolean hasItems() {
        return CollectionUtils.isNotEmpty(this.getItems());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("items.size", CollectionUtils.size(items))
                .toString();
    }
}
