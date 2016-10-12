package com.github.clboettcher.bonappetit.app.data.order.entity;

import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;

@DatabaseTable(tableName = "ITEM_ORDER")
public class ItemOrderEntity {

    @DatabaseField(generatedId = true, columnName = "ID")
    private Long id;

    @DatabaseField(columnName = "ITEM_ID", canBeNull = false)
    private Long itemId;

    @ForeignCollectionField(eager = true)
    private Collection<OptionOrderEntity> optionOrderEntities = new ArrayList<>();

    @DatabaseField(columnName = "NOTE")
    private String note = "";

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private CustomerEntity customer;

    @DatabaseField(columnName = "ORDER_TIME", dataType = DataType.DATE_TIME, canBeNull = false)
    private DateTime orderTime;

    @DatabaseField(columnName = "STAFF_MEMBER_ID", canBeNull = false)
    private Long staffMemberId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Collection<OptionOrderEntity> getOptionOrderEntities() {
        return optionOrderEntities;
    }

    public void setOptionOrderEntities(Collection<OptionOrderEntity> optionOrderEntities) {
        this.optionOrderEntities = optionOrderEntities;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public DateTime getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(DateTime orderTime) {
        this.orderTime = orderTime;
    }

    public Long getStaffMemberId() {
        return staffMemberId;
    }

    public void setStaffMemberId(Long staffMemberId) {
        this.staffMemberId = staffMemberId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("itemId", itemId)
                .append("optionOrderEntities", optionOrderEntities)
                .append("note", note)
                .append("customer", customer)
                .append("orderTime", orderTime)
                .append("staffMemberId", staffMemberId)
                .toString();
    }
}
