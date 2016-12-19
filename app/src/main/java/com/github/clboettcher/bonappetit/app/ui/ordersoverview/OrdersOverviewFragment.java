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
import com.github.clboettcher.bonappetit.app.data.order.OrdersResource;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.event.FinishOrdersCompletedEvent;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefEntity;
import com.github.clboettcher.bonappetit.app.ui.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersFragment;
import com.google.common.base.Optional;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class OrdersOverviewFragment extends TakeOrdersFragment {

    public static final String TAG = OrdersOverviewFragment.class.getName();
    private View rootView;
    private TextView staffMemberTextView;
    private TextView customerTextView;
    private OnSwitchToTabListener mListener;
    private ViewFlipper viewFlipper;
//    private AlertDialog uploadInProgressDialog;

    @Inject
    CustomerDao customerDao;

    @Inject
    StaffMemberRefDao staffMemberRefDao;

    @Inject
    OrdersResource ordersResource;

    @Inject
    EventBus eventBus;
    private AlertDialog errorDialog;
    private boolean initialized;

    /**
     * The adapter that is responsible for displaying a collection of orders in a list view.
     */
    private ItemOrderAdapter adapter;

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
        rootView = inflater.inflate(R.layout.fragment_orders_overview, container, false);

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
        Button finishButton = (Button) rootView.findViewById(R.id.fragmentOrdersOverviewButtonFinish);
        finishButton.setOnClickListener(new View.OnClickListener() {
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
                getActivity()
        );
        orderContainer.setAdapter(adapter);

        // Error dialog
        errorDialog = new AlertDialog.Builder(getActivity())
                .setTitle("Bestellung abschließen fehlgeschlagen!")
                .setIcon(R.drawable.ic_alerts_and_states_warning)
                .setPositiveButton(getString(R.string.general_action_retry), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setCustomerAndFinishOrders();
                    }
                })
                .setNegativeButton(getString(R.string.general_action_cancel), null)
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
        // TODO: introduce string resources
        new AlertDialog.Builder(getActivity())
                .setTitle("Bestellungen drucken")
                .setIcon(R.drawable.ic_alerts_and_states_warning)
                .setMessage(String.format("%d Bestellung(en) werden gedruckt", orderCount))
                .setPositiveButton(getString(R.string.general_action_confirm), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setCustomerAndFinishOrders();
                    }
                }).setNegativeButton(getString(R.string.general_action_cancel), null)
                .show();
    }


    private void setCustomerAndFinishOrders() {
        // Init upload
        final List<ItemOrderEntity> orders = ordersResource.listRefreshedOrders();

        // Set the customer on all orders
        // This is done here because the customer might change after the item order was created.
        // At this point, the customer must be set.
        Optional<CustomerEntity> customerOpt = customerDao.get();
        final CustomerEntity customer = customerOpt.get();
        for (ItemOrderEntity order : orders) {
            order.setCustomer(customer);
        }

        // upload-in-progress dialog
        // TODO: Refactor to androids ProgressDialog (which is a subclass of AlertDialog).
        // TODO. move initializer to onCreateView() or so.
        // TODO: check if cancellable(false) causes the dialog not to be cancelled form code.
        // TODO: drop R.layout.dialog_take_orders_overview_finish_in_progress, null); if not longer needed
//        View uploadInProgressDialogView = getActivity().getLayoutInflater().inflate(
//                R.layout.dialog_take_orders_overview_finish_in_progress, null);
//        uploadInProgressDialog = new AlertDialog.Builder(getActivity())
//                .setView(uploadInProgressDialogView)
//                .setCancelable(false)
//                .create();
//        uploadInProgressDialog.show();
        ordersResource.finishOrders(orders);
        this.update();
    }

    @Subscribe
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

        if (finishOrdersLoadable.isLoaded()) {
            ordersResource.reset();
            // Show success-toast
            Toast.makeText(getActivity(), "Bestellung erfolgreich abgeschlossen!",
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
        } else if (!(customerOpt.isPresent() && atLeastOneOrderTaken)) {
            this.setState(OrdersOverviewViewState.INACTIVE);
        } else if (finishOrdersLoadable.isInitial()) {
            this.setState(OrdersOverviewViewState.ACTIVE);
        }

        updateStaffMemberAndCustomer();
        updateOrdersTable();
    }

    private void updateStaffMemberAndCustomer() {
        Optional<CustomerEntity> customerOpt = customerDao.get();
        Optional<StaffMemberRefEntity> staffMemRefOpt = staffMemberRefDao.get();
        if (staffMemRefOpt.isPresent()) {
            StaffMemberEntity staffMember = staffMemRefOpt.get().getStaffMemberEntity();
            this.staffMemberTextView.setText(String.format(" %s %s",
                    staffMember.getFirstName(),
                    staffMember.getLastName()));
        }
        if (customerOpt.isPresent()) {
            customerTextView.setText(String.format(" %s", customerOpt.get().getValue()));
        }
    }

    private void updateOrdersTable() {
        Log.i(TAG, "Updating displayed orders.");
        // TODO: load all item orders from db
        List<ItemOrderEntity> itemOrders = ordersResource.listRefreshedOrders();

        // TODO: switch if we have no orders to show
//        if (CollectionUtils.isEmpty(itemOrders)) {
//            mListener.onSwitchToTab(TakeOrdersActivity.TAB_MENU);
//        }

        adapter.clear();
        adapter.addAll(itemOrders);
        // TODO: implement sorting
//        adapter.sort(StaffMemberEntityComparator.INSTANCE);
        adapter.notifyDataSetChanged();
    }

    private void setState(OrdersOverviewViewState state) {
        View newView = viewFlipper.findViewById(state.getViewId());
        int newIndex = viewFlipper.indexOfChild(newView);
        viewFlipper.setDisplayedChild(newIndex);
    }

    private void showErrorDialog(ErrorCode errorCode) {
        // TODO: refactor to string resources, include note on taking the orders by hand.
        String errorMsg = String.format("Beim Abschließen der Bestellung und dem Drucken " +
                "der Bons ist ein Fehler aufgetreten: %s", errorCode);
        errorDialog.setMessage(errorMsg);
        errorDialog.show();
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
        this.initialized = true;
    }
}