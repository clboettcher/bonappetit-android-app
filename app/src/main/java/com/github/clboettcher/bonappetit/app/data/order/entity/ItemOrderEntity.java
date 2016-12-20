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
package com.github.clboettcher.bonappetit.app.data.order.entity;

import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collection;

@DatabaseTable(tableName = "ITEM_ORDER")
public class ItemOrderEntity {

    @DatabaseField(generatedId = true, columnName = "ID")
    private Long id;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = false)
    private ItemEntity item;

    @ForeignCollectionField(eager = true)
    private Collection<OptionOrderEntity> optionOrderEntities = new ArrayList<>();

    @DatabaseField(columnName = "NOTE")
    private String note = "";

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = true)
    private CustomerEntity customer;

    @DatabaseField(columnName = "ORDER_TIME", dataType = DataType.DATE_TIME, canBeNull = false)
    private DateTime orderTime;

    @DatabaseField(foreign = true, canBeNull = false, foreignAutoRefresh = false)
    private StaffMemberEntity staffMember;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
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

    public StaffMemberEntity getStaffMember() {
        return staffMember;
    }

    public void setStaffMember(StaffMemberEntity staffMember) {
        this.staffMember = staffMember;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("item", item)
                .append("optionOrderEntities.size", CollectionUtils.size(optionOrderEntities))
                .append("note", note)
                .append("customer", customer)
                .append("orderTime", orderTime)
                .append("staffMember", staffMember)
                .toString();
    }
}
