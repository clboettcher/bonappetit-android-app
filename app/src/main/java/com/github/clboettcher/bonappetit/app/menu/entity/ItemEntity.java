package com.github.clboettcher.bonappetit.app.menu.entity;

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
                .append("menu.id", menu.getId())
                .append("options.size()", CollectionUtils.size(options))
                .toString();
    }
}
