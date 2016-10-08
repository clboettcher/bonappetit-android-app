package com.github.clboettcher.bonappetit.app.menu.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.math.BigDecimal;

@DatabaseTable(tableName = "ITEM")
public class ItemEntity {

    @DatabaseField(id = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "NAME")
    private String name;

    @DatabaseField(columnName = "PRICE")
    private BigDecimal price;

    @DatabaseField(columnName = "TYPE")
    private ItemEntityType type;

    /**
     * Required by ORMLite to be able to query the foreign collection {@link MenuEntity#getItems()}.
     */
    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private MenuEntity menu;

//    @ForeignCollectionField(eager = true)
//    private Collection<OptionImpl> options;

//    public boolean hasOptions() {
//        return !CollectionUtils.isEmpty(getOptions());
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    //    public Collection<OptionImpl> getOptions() {
//        return options;
//    }

//    public void setOptions(Collection<OptionImpl> options) {
//        this.options = options;
//    }
}
