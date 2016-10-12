package com.github.clboettcher.bonappetit.app.data.menu.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

@DatabaseTable(tableName = "RADIO_ITEM")
public class RadioItemEntity {

    @DatabaseField(id = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "INDEX")
    private Integer index;

    @DatabaseField(columnName = "TITLE")
    private String title;

    @DatabaseField(columnName = "PRICE_DIFF")
    private BigDecimal priceDiff = new BigDecimal("0.00");

    /**
     * Required by ORMLite to be able to query the foreign collection {@link RadioOption#getRadioItemEntities()}
     */
    @DatabaseField(canBeNull = false, foreign = true)
    private OptionEntity option;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public OptionEntity getOption() {
        return option;
    }

    public void setOption(OptionEntity option) {
        this.option = option;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("index", index)
                .append("title", title)
                .append("priceDiff", priceDiff)
                .append("option.id", option != null ? option.getId() : "--")
                .toString();
    }
}
