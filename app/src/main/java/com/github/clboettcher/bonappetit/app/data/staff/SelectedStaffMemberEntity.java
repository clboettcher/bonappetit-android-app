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
package com.github.clboettcher.bonappetit.app.data.staff;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This table contains the data of the currently selected staff member.
 */
@DatabaseTable(tableName = "SELECTED_STAFF_MEMBER")
public class SelectedStaffMemberEntity {

    @DatabaseField(columnName = "SELECTED_STAFF_MEMBER_REFERENCE_ID", id = true, canBeNull = false)
    private Long id = 1L;

    @DatabaseField(columnName = "STAFF_MEMBER_ID", canBeNull = false)
    private Long staffMemberId;

    @DatabaseField(columnName = "STAFF_MEMBER_FIRST_NAME", canBeNull = false)
    private String staffMemberFirstName;

    @DatabaseField(columnName = "STAFF_MEMBER_LAST_NAME", canBeNull = false)
    private String staffMemberLastName;

    public Long getStaffMemberId() {
        return staffMemberId;
    }

    public void setStaffMemberId(Long staffMemberId) {
        this.staffMemberId = staffMemberId;
    }

    public String getStaffMemberFirstName() {
        return staffMemberFirstName;
    }

    public void setStaffMemberFirstName(String staffMemberFirstName) {
        this.staffMemberFirstName = staffMemberFirstName;
    }

    public String getStaffMemberLastName() {
        return staffMemberLastName;
    }

    public void setStaffMemberLastName(String staffMemberLastName) {
        this.staffMemberLastName = staffMemberLastName;
    }
}
