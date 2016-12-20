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
 * This table contains the currently selected staff member.
 * <p>
 * This is modeled as a table (not shared preferences, etc.) to favor uniform data access. In
 * the future we might provide a list of the n last selected staff members. This use case
 * is best supported with a table.
 */
@DatabaseTable(tableName = "SELECTED_STAFF_MEMBER")
public class StaffMemberRefEntity {

    @DatabaseField(columnName = "SELECTED_STAFF_MEMBER_REFERENCE_ID", id = true)
    private Long id;

    @DatabaseField(canBeNull = false, foreign = true, foreignAutoRefresh = true)
    private StaffMemberEntity staffMemberEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StaffMemberEntity getStaffMemberEntity() {
        return staffMemberEntity;
    }

    public void setStaffMemberEntity(StaffMemberEntity staffMemberEntity) {
        this.staffMemberEntity = staffMemberEntity;
    }
}
