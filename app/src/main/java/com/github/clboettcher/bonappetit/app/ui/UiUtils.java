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
package com.github.clboettcher.bonappetit.app.ui;

import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntityType;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

public final class UiUtils {
    /**
     * No instance
     */
    private UiUtils() {
    }

    public static String getDisplayText(Optional<CustomerEntity> customerOpt) {
        return getDisplayText(customerOpt, false);
    }

    public static String getDisplayText(Optional<CustomerEntity> customerOpt, boolean trimResult) {
        String customerDisplayText;
        if (customerOpt.isPresent()) {
            CustomerEntity customer = customerOpt.get();
            switch (customer.getType()) {
                case TABLE:
                    customerDisplayText = customer.getTableDisplayValue();
                    break;
                case FREE_TEXT:
                    customerDisplayText = String.format(" %s", customer.getValue());
                    break;
                case STAFF_MEMBER:
                    StaffMemberEntity staffMember = customer.getStaffMember();
                    customerDisplayText = String.format(" %s %s (MA)",
                            staffMember.getFirstName(),
                            staffMember.getLastName());
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown enum value: %s.%s",
                            CustomerEntityType.class.getName(),
                            customer.getType()));
            }
        } else {
            customerDisplayText = "";
        }
        return trimResult ? StringUtils.trim(customerDisplayText) : customerDisplayText;
    }
}
