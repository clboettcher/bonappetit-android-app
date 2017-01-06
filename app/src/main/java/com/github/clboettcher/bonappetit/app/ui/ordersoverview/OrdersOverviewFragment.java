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
package com.github.clboettcher.bonappetit.app.ui.ordersoverview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.ErrorCode;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerDao;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.menu.dao.ItemDao;
import com.github.clboettcher.bonappetit.app.data.order.OrdersResource;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.event.FinishOrdersCompletedEvent;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.ui.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.ui.UiUtils;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersFragment;
import com.google.common.base.Optional;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class OrdersOverviewFragment extends TakeOrdersFragment {

    public static final String TAG = OrdersOverviewFragment.class.getName();

    private boolean initialized;
    private TextView staffMemberTextView;
    private TextView customerTextView;
    private OnSwitchToTabListener mListener;
    private ViewFlipper viewFlipper;
    private AlertDialog errorDialog;

    @Inject
    CustomerDao customerDao;

    @Inject
    StaffMemberDao staffMemberDao;

    @Inject
    SelectedStaffMemberDao selectedStaffMemberDao;

    @Inject
    OrdersResource ordersResource;

    @Inject
    EventBus eventBus;

    @Inject
    ItemDao itemDao;

    /**
     * The adapter that is responsible for displaying a collection of orders in a list view.
     */
    private ItemOrderAdapter adapter;

    /**
     * The button that finishes orders by sendings them to the server.
     */
    private Button finishOrdersButton;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.mListener = (OnSwitchToTabListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(String.format("%s must implement OnSwitchToTabListener",
                    context.toString()));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders_overview, container, false);

        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.fragmentOrdersOverviewViewSwitcher);

        // Configure the inactive view.
        String selectCustomerTabTitle = getActivity().getString(R.string.fragment_select_customer_tab_title);
        String menuTabTitle = getActivity().getString(R.string.fragment_menu_tab_title);

        Button switchToSelectCustomerButton = (Button) rootView.findViewById(
                R.id.fragmentOrdersOverviewInactiveButtonSwitchToSelectCustomer);
        switchToSelectCustomerButton.setText(String.format(String.valueOf(switchToSelectCustomerButton.getText()),
                selectCustomerTabTitle));
        switchToSelectCustomerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onSwitchToTab(TakeOrdersActivity.TAB_SELECT_CUSTOMER);
            }
        });

        Button switchToMenuButton = (Button) rootView.findViewById(
                R.id.fragmentOrdersOverviewInactiveButtonSwitchToMenu);
        switchToMenuButton.setText(String.format(String.valueOf(switchToMenuButton.getText()), menuTabTitle));
        switchToMenuButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onSwitchToTab(TakeOrdersActivity.TAB_MENU);
            }
        });

        TextView t = (TextView) rootView.findViewById(R.id.fragmentOrdersOverviewInactiveTextViewExplanation);
        t.setText(String.format(String.valueOf(t.getText()), selectCustomerTabTitle, menuTabTitle));

        // configure the active view
        this.finishOrdersButton = (Button) rootView.findViewById(R.id.fragmentOrdersOverviewButtonFinish);
        finishOrdersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finishOrdersButtonHandler();
            }
        });

        staffMemberTextView = (TextView) rootView.findViewById(R.id.take_orders_overview_textview_username_label_text);
        customerTextView = (TextView) rootView.findViewById(R.id.take_orders_overview_textview_customer_label_text);

        // Initialize the order list that displays a collection of orders with an adapter.
        ListView orderContainer = (ListView) rootView.findViewById(R.id.fragmentOrdersOverviewListViewOrders);
        adapter = new ItemOrderAdapter(new ArrayList<ItemOrderEntity>(),
                ordersResource,
                mListener,
                getActivity().getLayoutInflater(),
                getActivity(),
                itemDao,
                this
        );
        orderContainer.setAdapter(adapter);

        // Error dialog
        errorDialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.fragment_orders_overview_active_dialog_finish_failed_title)
                .setIcon(R.drawable.ic_alerts_and_states_warning)
                .setPositiveButton(getString(R.string.general_action_retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setCustomerAndFinishOrders();
                    }
                })
                .setNegativeButton(getString(R.string.general_action_cancel), null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // Reset the resource state. If the dialog is dismissed in any way that means
                        // cancel the operation as a whole.
                        // The error popup should not be shown when the activity is loaded again.
                        ordersResource.reset();
                    }
                })
                .create();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.eventBus.register(this);
        Log.i(TAG, "Starting. Registering for events");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        update();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.eventBus.unregister(this);
        Log.i(TAG, "Stopping. Unregistering from events");
    }

    public void finishOrdersButtonHandler() {
        final long orderCount = ordersResource.count();
        // Ask for confirmation before finishing the orders.

        String message;
        if (orderCount == 1) {
            message = String.format(getActivity().getString(
                    R.string.fragment_orders_overview_dialog_finish_orders_confirmation_message_singular),
                    orderCount);
        } else {
            message = String.format(getActivity().getString(
                    R.string.fragment_orders_overview_dialog_finish_orders_confirmation_message_plural),
                    orderCount);
        }

        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.fragment_orders_overview_dialog_finish_orders_confirmation_title)
                .setIcon(R.drawable.ic_alerts_and_states_warning)
                .setMessage(message)
                .setPositiveButton(
                        getString(R.string.fragment_orders_overview_dialog_finish_orders_confirmation_affirmative),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                setCustomerAndFinishOrders();
                            }
                        }).setNegativeButton(getString(R.string.general_action_cancel), null)
                .show();
    }


    private void setCustomerAndFinishOrders() {
        Log.i(TAG, "Finishing orders and sending them to the server.");
        // Disable finish orders button to prevent multiple requests with the same
        // orders if the user presses multiple times.
        this.finishOrdersButton.setEnabled(false);

        // Init upload
        final List<ItemOrderEntity> orders = ordersResource.list();

        // Set the customer on all orders
        // This is done here because the customer might changed after the item order was created.
        // At this point, the customer must be set.
        Optional<CustomerEntity> customerOpt = customerDao.get();
        final CustomerEntity customer = customerOpt.get();
        for (ItemOrderEntity order : orders) {
            order.setCustomer(customer);
        }
        ordersResource.finishOrders(orders);
        this.update();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFinishOrdersCompletedEvent(FinishOrdersCompletedEvent event) {
        Log.i(TAG, String.format("Received %s. Triggering update()", FinishOrdersCompletedEvent.class.getSimpleName()));
        this.update();
    }

    @Override
    public void update() {
        // Make sure this instance is the one that has been dependency injected. Apparently
        // android sets all fields to null when the fragment is destroyed. Unfortunately the update() method
        // might get called after this leading to NPE. Discovered while changing the orientation during a
        // network operation in progress.
        if (!initialized) {
            return;
        }
        // First check if we display any content at all
        // To show content we must have orders and a customer must be selected.
        Optional<CustomerEntity> customerOpt = customerDao.get();
        boolean atLeastOneOrderTaken = ordersResource.count() > 0;
        Loadable<Void> finishOrdersLoadable = ordersResource.getFinishOrdersLoadable();

        Log.i(TAG, String.format("update() called. Customer is %s. Order loadable is %s",
                customerOpt, finishOrdersLoadable));

        if (!customerOpt.isPresent() || !atLeastOneOrderTaken) {
            this.setState(OrdersOverviewViewState.INACTIVE);
        } else if (finishOrdersLoadable.isLoaded()) {
            // The orders are now saved in the server. No need for local copies anymore.
            ordersResource.deleteAllOrders();
            // Clear the customer for the next set of orders
            customerDao.clear();
            // Reset state of the operation
            ordersResource.reset();
            // Show success-toast
            Toast.makeText(getActivity(), R.string.fragment_orders_overview_active_toast_finish_success,
                    Toast.LENGTH_LONG).show();
            // Switch to "select-customer"-tab
            mListener.onSwitchToTab(TakeOrdersActivity.TAB_SELECT_CUSTOMER);
            return;
        } else if (finishOrdersLoadable.isLoading()) {
            this.setState(OrdersOverviewViewState.LOADING);
        } else if (finishOrdersLoadable.isFailed()) {
            this.setState(OrdersOverviewViewState.ACTIVE);
            Log.e(TAG, String.format("Finishing orders failed with code %s", finishOrdersLoadable.getErrorCode()));
            showErrorDialog(finishOrdersLoadable.getErrorCode());
        } else if (finishOrdersLoadable.isInitial()) {
            this.setState(OrdersOverviewViewState.ACTIVE);
        }

        this.finishOrdersButton.setEnabled(true);
        updateStaffMemberAndCustomer();
        updateOrdersTable();
    }

    private void updateStaffMemberAndCustomer() {
        // Customer
        final Optional<CustomerEntity> customerOpt = customerDao.get();
        String customerDisplayText = UiUtils.getDisplayText(customerOpt);
        customerTextView.setText(customerDisplayText);

        // Staff member
        Optional<SelectedStaffMemberEntity> selectedStaffMemberOpt = selectedStaffMemberDao.get();
        if (selectedStaffMemberOpt.isPresent()) {
            SelectedStaffMemberEntity selectedStaffMemberEntity = selectedStaffMemberOpt.get();
            String name = String.format(" %s %s",
                    selectedStaffMemberEntity.getStaffMemberFirstName(),
                    selectedStaffMemberEntity.getStaffMemberLastName());

            if (!this.staffMemberDao.exists(selectedStaffMemberEntity.getStaffMemberId())) {
                name += " (!)";
            }
            this.staffMemberTextView.setText(name);
        }
    }

    private void updateOrdersTable() {
        Log.i(TAG, "Updating displayed orders.");
        List<ItemOrderEntity> itemOrders = ordersResource.list();

        adapter.clear();
        adapter.addAll(itemOrders);
        adapter.notifyDataSetChanged();
    }

    private void setState(OrdersOverviewViewState state) {
        View newView = viewFlipper.findViewById(state.getViewId());
        int newIndex = viewFlipper.indexOfChild(newView);
        viewFlipper.setDisplayedChild(newIndex);
    }

    private void showErrorDialog(ErrorCode errorCode) {
        String errorMsg = String.format(getActivity().getString(
                R.string.fragment_orders_overview_active_dialog_finish_failed_body),
                errorCode);
        errorDialog.setMessage(errorMsg);
        errorDialog.show();
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
        this.initialized = true;
    }
}