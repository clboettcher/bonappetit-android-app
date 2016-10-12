package com.github.clboettcher.bonappetit.app.data.order.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@DatabaseTable(tableName = "OPTION_ORDER")
public class OptionOrderEntity implements CheckboxOptionOrder, IntegerOptionOrder, RadioOptionOrder {

    /*
     *****************************************************************************************************************
     * General properties
     *****************************************************************************************************************
     */

    @DatabaseField(generatedId = true, columnName = "ID")
    private Long id;

    /**
     * Required by ORMLite to be able to query the foreign collection {@link ItemOrder#getOptionOrderEntities()}.
     */
    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = false)
    private ItemOrder itemOrder;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    private Long optionId;

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

    @DatabaseField(columnName = "SELECTED_RADIO_ITEM_ID")
    private Long selectedRadioItemId;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemOrder getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(ItemOrder itemOrder) {
        this.itemOrder = itemOrder;
    }

    @Override
    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
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

    public Long getSelectedRadioItemId() {
        return selectedRadioItemId;
    }

    public void setSelectedRadioItemId(Long selectedRadioItemId) {
        this.selectedRadioItemId = selectedRadioItemId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("itemOrder", itemOrder)
                .append("optionId", optionId)
                .append("checked", checked)
                .append("value", value)
                .append("selectedRadioItemId", selectedRadioItemId)
                .toString();
    }
}
