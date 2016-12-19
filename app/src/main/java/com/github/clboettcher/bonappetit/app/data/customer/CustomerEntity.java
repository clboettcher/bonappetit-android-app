package com.github.clboettcher.bonappetit.app.data.customer;

import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@DatabaseTable(tableName = "CUSTOMER")
public class CustomerEntity {

    @DatabaseField(columnName = "CUSTOMER_ID", id = true)
    private Long id;

    @DatabaseField(columnName = "CUSTOMER_TYPE")
    private CustomerEntityType type;

    @DatabaseField(columnName = "TABLE_NUMBER")
    private Long tableNumber;

    @DatabaseField(columnName = "TABLE_DISPLAY_VALUE")
    private String tableDisplayValue;

    @DatabaseField(canBeNull = true, foreign = true, foreignAutoRefresh = true)
    private StaffMemberEntity staffMember;

    @DatabaseField(columnName = "TEXT_VALUE")
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerEntityType getType() {
        return type;
    }

    public void setType(CustomerEntityType type) {
        this.type = type;
    }

    public Long getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Long tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getTableDisplayValue() {
        return tableDisplayValue;
    }

    public void setTableDisplayValue(String tableDisplayValue) {
        this.tableDisplayValue = tableDisplayValue;
    }

    public StaffMemberEntity getStaffMember() {
        return staffMember;
    }

    public void setStaffMember(StaffMemberEntity staffMember) {
        this.staffMember = staffMember;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("type", type)
                .append("tableNumber", tableNumber)
                .append("tableDisplayValue", tableDisplayValue)
                .append("staffMember", staffMember)
                .append("value", value)
                .toString();
    }
}
