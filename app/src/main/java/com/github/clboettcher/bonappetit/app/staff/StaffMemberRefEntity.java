package com.github.clboettcher.bonappetit.app.staff;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * This table contains the currently selected staff member.
 * <p/>
 * This is modeled as a table (not shared preferences, etc.) to favor uniform data access. In
 * the future we might provide a list of the n last selected staff members. This use case
 * is best supported with a table.
 */
@DatabaseTable(tableName = "SELECTED_STAFF_MEMBER")
public class StaffMemberRefEntity {

    @DatabaseField(columnName = "SELECTED_STAFF_MEMBER_REFERENCE_ID")
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
