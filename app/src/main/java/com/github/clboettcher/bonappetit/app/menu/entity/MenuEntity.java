package com.github.clboettcher.bonappetit.app.menu.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Collection;

@DatabaseTable(tableName = "MENU")
public class MenuEntity {

    /**
     * The ID.
     */
    @DatabaseField(id = true, columnName = "ID")
    private Long id;

    /**
     * The items that this menu consists of.
     */
    @ForeignCollectionField(eager = true)
    private Collection<ItemEntity> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<ItemEntity> getItems() {
        return items;
    }

    public void setItems(Collection<ItemEntity> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("items.size()", CollectionUtils.size(items))
                .toString();
    }
}
