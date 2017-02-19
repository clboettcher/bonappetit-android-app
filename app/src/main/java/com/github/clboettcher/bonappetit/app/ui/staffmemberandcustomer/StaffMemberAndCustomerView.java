package com.github.clboettcher.bonappetit.app.ui.staffmemberandcustomer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TableLayout;
import android.widget.TextView;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerDao;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.ui.UiUtils;
import com.google.common.base.Optional;

import javax.inject.Inject;

public class StaffMemberAndCustomerView extends TableLayout {

    private static final String TAG = StaffMemberAndCustomerView.class.getName();

    @Inject
    CustomerDao customerDao;

    @Inject
    StaffMemberDao staffMemberDao;

    @Inject
    SelectedStaffMemberDao selectedStaffMemberDao;

    private TextView staffMemberText;

    private TextView customerText;


    public StaffMemberAndCustomerView(Context context) {
        super(context);
        init(context);
    }

    public StaffMemberAndCustomerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void updateCustomerAndStaffMember() {
        final Optional<CustomerEntity> customerOpt = customerDao.get();
        String customerDisplayText = UiUtils.getDisplayText(customerOpt);
        customerText.setText(customerDisplayText);

        Optional<SelectedStaffMemberEntity> staffMemRefOpt = selectedStaffMemberDao.get();
        if (staffMemRefOpt.isPresent()) {
            SelectedStaffMemberEntity selectedStaffMemberEntity = staffMemRefOpt.get();
            String name = String.format(" %s %s",
                    selectedStaffMemberEntity.getStaffMemberFirstName(),
                    selectedStaffMemberEntity.getStaffMemberLastName());
            if (!this.staffMemberDao.exists(selectedStaffMemberEntity.getStaffMemberId())) {
                name += " (!)";
            }
            staffMemberText.setText(name);
        } else {
            staffMemberText.setText("");
        }

        invalidate();
        requestLayout();
    }

    private void init(Context context) {
        BonAppetitApplication bonAppetitApplication = (BonAppetitApplication) context.getApplicationContext();
        DiComponent diComponent = bonAppetitApplication.getDiComponent();
        diComponent.inject(this);

        inflate(getContext(), R.layout.staff_member_and_customer, this);

        staffMemberText = (TextView) findViewById(R.id.fragmentMenuStaffMember);
        customerText = (TextView) findViewById(R.id.fragmentMenuCustomer);

        this.updateCustomerAndStaffMember();
    }
}
