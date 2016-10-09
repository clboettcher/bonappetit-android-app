package com.github.clboettcher.bonappetit.app.menu.entity;

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
