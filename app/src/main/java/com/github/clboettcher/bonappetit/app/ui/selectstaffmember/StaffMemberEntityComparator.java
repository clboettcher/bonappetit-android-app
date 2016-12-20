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
