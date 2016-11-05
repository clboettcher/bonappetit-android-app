package com.github.clboettcher.bonappetit.app.ui.selectstaffmember;

import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;

import java.util.Comparator;

/**
 * Comparator for {@link StaffMemberEntity} that compares by first name,
 * then by last name.
 */
final class StaffMemberEntityComparator implements Comparator<StaffMemberEntity> {

    public static final StaffMemberEntityComparator INSTANCE = new StaffMemberEntityComparator();

    private StaffMemberEntityComparator() {
    }

    @Override
    public int compare(StaffMemberEntity lhs, StaffMemberEntity rhs) {
        int firstNameComparison = lhs.getFirstName().compareTo(rhs.getFirstName());
        if (firstNameComparison == 0) {
            return lhs.getLastName().compareTo(rhs.getLastName());
        } else {
            return firstNameComparison;
        }
    }
}
