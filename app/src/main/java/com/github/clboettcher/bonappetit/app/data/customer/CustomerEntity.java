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
