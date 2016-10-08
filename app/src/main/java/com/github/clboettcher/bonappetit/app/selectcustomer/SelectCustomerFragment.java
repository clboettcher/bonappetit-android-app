package com.github.clboettcher.bonappetit.app.selectcustomer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.activity.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberRefEntity;
import com.github.clboettcher.bonappetit.app.takeorders.TakeOrdersActivity;
import com.github.clboettcher.bonappetit.app.takeorders.TakeOrdersFragment;
import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

/**
 * Fragment which provides customer selection.
 * A customer can be a table or an arbitrary string such as "to go".
 */
public class SelectCustomerFragment extends TakeOrdersFragment implements View.OnClickListener {

    /**
     * The tag for logging.
     */
    private static final String TAG = SelectCustomerFragment.class.getName();

    @BindView(R.id.fragment_select_customer_staff_member)
    TextView selectedStaffMember;

    private OnSwitchToTabListener switchToTabListener;

    @BindView(R.id.button_take_orders_select_customer_freetext_confirm)
    Button buttonFreetextConfirm;

    @BindView(R.id.textview_take_orders_select_customer_freetext_text)
    EditText freetextCustomer;

    @Inject
    StaffMemberRefDao staffMemberRefDao;

    @Inject
    CustomerDao customerDao;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()" + ", isAdded(): " + isAdded());

        if (context instanceof OnSwitchToTabListener) {
            switchToTabListener = (OnSwitchToTabListener) context;
        } else {
            throw new IllegalArgumentException(String.format("Context %s must implement OnSwitchToTabListener",
                    context.toString()));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() Activity set to " + getActivity() + ", isAdded(): " + isAdded());
        updateStaffMember();
    }

    @Override
    public void update() {
        Log.d(TAG, "update() called, getActivity(): " + getActivity() + ", isAdded(): " + isAdded());
        updateStaffMember();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()" + ", isAdded(): " + isAdded());
        View rootView = inflater.inflate(R.layout.fragment_select_customer, container, false);
        ButterKnife.bind(this, rootView);

        updateStaffMember();

        // TODO: refactor id to be consistent with naming
        // Create the grid view for the table-customers
        TableLayout tablesContainer = (TableLayout) rootView.findViewById(R.id.take_orders_select_customer_tables_table);
        List<Button> buttons = Arrays.asList(
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_01)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_02)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_03)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_04)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_05)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_06)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_07)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_08)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_09)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_10)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_11)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_12)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_13)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_14)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_bar_backside)),
                ((Button) tablesContainer.findViewById(R.id.button_take_orders_select_customer_table_bar_frontside))
        );

        for (Button button : buttons) {
            button.setOnClickListener(this);
        }

        // Free Text input
        buttonFreetextConfirm.setOnClickListener(this);
        buttonFreetextConfirm.setEnabled(false);

        freetextCustomer.addTextChangedListener(new FreeTextCustomerTextWatcher(buttonFreetextConfirm));
        return rootView;
    }

    private void updateStaffMember() {
        Optional<StaffMemberRefEntity> staffMemberRefOpt = staffMemberRefDao.get();
        if (staffMemberRefOpt.isPresent()) {
            StaffMemberEntity staffMemberEntity = staffMemberRefOpt.get().getStaffMemberEntity();
            selectedStaffMember.setText(String.format("%s %s",
                    staffMemberEntity.getFirstName(),
                    staffMemberEntity.getLastName()));
        } else {
            selectedStaffMember.setText("");
        }
    }

    public void onClick(View view) {
        // Check if a customer name has been entered in the freetext field
        final String newCustomer;
        if (view.equals(buttonFreetextConfirm)) {
            newCustomer = freetextCustomer.getText().toString();
        }

        // Otherwise a table number has been clicked
        else {
            String buttonText = (String) ((Button) view).getText();
            if (StringUtils.isNumeric(buttonText)) {
                newCustomer = String.format("%s %s",
                        getString(R.string.fragment_select_customer_table_prefix),
                        buttonText);
            } else {
                newCustomer = buttonText;
            }
        }

//        TODO: review warning and reenable when db setup for orders is completed
//        final long orderCount = DatabaseAccessHelper.getInstance().getOrderCount(dbHelper);
        // If unprinted orders are present, ask before overwriting the customer.
//        if (orderCount != 0) {
//            new AlertDialog.Builder(getActivity())
//                    .setTitle("Offene Bestellungen vorhanden!")
//                    .setIcon(R.drawable.ic_alerts_and_states_warning)
//                    .setMessage(String.format("Der Kunde für %d nicht gedruckte Bestellung(en) wird auf \"%s\" geändert", orderCount, newCustomer))
//                    .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//                            saveNewCustomerAndSwitchFragment(newCustomer);
//
//                        }
//                    })
//                    .setNegativeButton(getString(R.string.cancel), null)
//                    .show();
//        } else {
        saveNewCustomerAndSwitchFragment(newCustomer);
//        }
    }

    private void saveNewCustomerAndSwitchFragment(String newCustomer) {
        // hide the softkeyboard
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(freetextCustomer.getWindowToken(), 0);

        // Save customer
        CustomerEntity customerEntity = new CustomerEntity();
        // We only have one customer in the db at a time. So we can use a
        // static, not auto generated ID.
        customerEntity.setId(1L);
        customerEntity.setValue(newCustomer);
        customerDao.save(customerEntity);
        // Switch to tab menu
        switchToTabListener.onSwitchToTab(TakeOrdersActivity.TAB_MENU);
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }

}