package com.github.clboettcher.bonappetit.app.ui;

import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntityType;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.google.common.base.Optional;

public final class UiUtils {
    /**
     * No instance
     */
    private UiUtils() {
    }

    public static String getDisplayText(Optional<CustomerEntity> customerOpt) {
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
                    customerDisplayText = String.format(" %s (MA)", staffMember.getFirstName());
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown enum value: %s.%s",
                            CustomerEntityType.class.getName(),
                            customer.getType()));
            }
        } else {
            customerDisplayText = "";
        }
        return customerDisplayText;
    }
}
