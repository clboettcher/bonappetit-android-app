package com.github.clboettcher.bonappetit.app.ui.menu;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.ui.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.menu.dao.ItemDao;
import com.github.clboettcher.bonappetit.app.data.menu.dao.MenuDao;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.data.menu.event.MenuUpdateFailedEvent;
import com.github.clboettcher.bonappetit.app.data.menu.event.MenuUpdateSuccessfulEvent;
import com.github.clboettcher.bonappetit.app.data.menu.event.PerformMenuUpdateEvent;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerDao;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefEntity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersFragment;
import com.google.common.base.Optional;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// TODO: add on click listener for update failed retry button
public class MenuFragment extends TakeOrdersFragment {

    private static final String TAG = MenuFragment.class.getName();

    private TextView staffMemberText;

    private TextView customerText;

    private OnSwitchToTabListener mListener;

    private ViewFlipper viewFlipper;

    @Inject
    EventBus bus;

    @Inject
    MenuDao menuDao;

    @Inject
    ItemDao itemDao;

    @Inject
    CustomerDao customerDao;

    @Inject
    StaffMemberRefDao staffMemberRefDao;

    private Comparator<ItemEntity> itemEntityComparator = new ItemEntityComparator();
    private MenuItemsAdapter menuItemsAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnSwitchToTabListener) context;
        } catch (ClassCastException e) {
            throw new IllegalStateException(context + " must implement OnSwitchToTabListener", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "Starting. Registering for events");
        this.bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "Stopping. Unregistering from events");
        this.bus.unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.actionUpdateMenu:
                Log.i(TAG, "Menu update requested by the user.");
                this.setState(MenuFragmentViewState.MENU_UPDATE_IN_PROGRESS);
                this.bus.post(new PerformMenuUpdateEvent());
                return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_menu, container, false);
        viewFlipper = (ViewFlipper) rootView.findViewById(R.id.fragmentMenuViewFlipper);
        // Configure the inactive view
        initInactiveView(rootView);

        // Configure the active view
        initActiveView(rootView);

        updateCustomerAndUsername();
        return rootView;
    }

    private void initActiveView(View rootView) {
        staffMemberText = (TextView) rootView.findViewById(R.id.fragmentMenuStaffMember);
        customerText = (TextView) rootView.findViewById(R.id.fragmentMenuCustomer);

        Button buttonOverview = (Button) rootView.findViewById(R.id.fragmentMenuButtonSwitchToOverview);
        buttonOverview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onSwitchToTab(TakeOrdersActivity.TAB_OVERVIEW);
            }
        });

        GridView menuItemsContainer = (GridView) rootView.findViewById(R.id.fragmentMenuGridViewItems);
        menuItemsAdapter = new MenuItemsAdapter(getContext(), new ArrayList<ItemEntity>());
        menuItemsContainer.setAdapter(menuItemsAdapter);
    }

    private void initInactiveView(View rootView) {
        String tabTitle = getActivity().getString(R.string.fragment_select_customer_tab_title);

        Button switchToSelectCustomerButton = (Button) rootView.findViewById(R.id.fragmentMenuInactiveButtonSwitchToSelectCustomer);
        switchToSelectCustomerButton.setText(String.format(
                getString(R.string.activity_take_orders_button_switch_tab), tabTitle));
        switchToSelectCustomerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mListener.onSwitchToTab(TakeOrdersActivity.TAB_SELECT_CUSTOMER);
            }
        });

        TextView t = (TextView) rootView.findViewById(R.id.fragmentMenuInactiveNote);
        t.setText(String.format(String.valueOf(t.getText()), tabTitle));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        update();
    }

    private void refreshOrderCounts() {
        // TODO Implement refreshOrderCounts when order management is reintegrated.
//        Button b;
//        for (ItemEntity item : items) {
//            b = (Button) rootView.findViewWithTag(item);
//            if (b != null) {
//                int orderCount = DatabaseAccessHelper.getInstance().getOrderCountForItem(dbHelper, item);
//                b.setText(item.getName() + (orderCount != 0 ? " (" + orderCount + ")" : ""));
//            }
//        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenuUpdateSuccessful(MenuUpdateSuccessfulEvent ignored) {
        this.update();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMenuUpdateFailed(MenuUpdateFailedEvent event) {
        String errorMsg;
        Throwable throwable = event.getThrowable();
        if (throwable != null) {
            errorMsg = String.format("Menu update failed: %s (%s)",
                    throwable.getMessage(),
                    throwable.getClass().getName());
        } else {
            errorMsg = String.format("Menu update failed: %d %s", event.getHttpCode(), event.getHttpMessage());
        }
        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
        this.update();
    }


    @Override
    public void update() {
        final Optional<CustomerEntity> customer = customerDao.get();
        Log.i(TAG, String.format("update() called. Customer is %s. Menu update state is %s",
                customer.orNull(),
                menuDao.getState()));

        updateCustomerAndUsername();
        refreshOrderCounts();

        if (customer.isPresent()) {
            // Customer is there, see if menu update went through OK
            switch (menuDao.getState()) {
                case INITIAL:
                    this.bus.post(new PerformMenuUpdateEvent());
                    this.setState(MenuFragmentViewState.MENU_UPDATE_IN_PROGRESS);
                    break;
                case UPDATE_FAILED:
                    // Show error view with a button to try again
                    this.setState(MenuFragmentViewState.MENU_UPDATE_FAILED);
                    break;
                case UPDATE_IN_PROGRESS:
                    // Show progress bar
                    this.setState(MenuFragmentViewState.MENU_UPDATE_IN_PROGRESS);
                    break;
                case UPDATE_COMPLETED:
                    final List<ItemEntity> itemList = itemDao.list();
                    Log.i(TAG, String.format("onCreate(): Initializing the fragment with %d item(s)",
                            itemList.size()));
                    Collections.sort(itemList, itemEntityComparator);
                    this.menuItemsAdapter.update(itemList);
                    this.setState(MenuFragmentViewState.OK);
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("Unknown enum constant " +
                                    "%s.%s", MenuFragmentViewState.class.getSimpleName(),
                            menuDao.getState()));
            }
        } else {
            // Show customer missing view
            this.setState(MenuFragmentViewState.NO_CUSTOMER);
        }

        // TODO Enable switch to overview button if we have any orders.
//        if (DatabaseAccessHelper.getInstance().getOrderCount(dbHelper) == 0) {
//            buttonOverview.setEnabled(false);
//        } else {
//            buttonOverview.setEnabled(true);
//        }
    }

    private void setState(MenuFragmentViewState state) {
        View newView = viewFlipper.findViewById(state.getViewId());
        int newIndex = viewFlipper.indexOfChild(newView);
        viewFlipper.setDisplayedChild(newIndex);
    }

    private void updateCustomerAndUsername() {
        final Optional<CustomerEntity> customerOpt = customerDao.get();
        if (customerOpt.isPresent()) {
            customerText.setText(String.format(" %s", customerOpt.get().getValue()));
        } else {
            customerText.setText("");
        }

        Optional<StaffMemberRefEntity> staffMemRefOpt = staffMemberRefDao.get();

        if (staffMemRefOpt.isPresent()) {
            StaffMemberEntity staffMemberEntity = staffMemRefOpt.get().getStaffMemberEntity();
            staffMemberText.setText(String.format(" %s %s",
                    staffMemberEntity.getFirstName(),
                    staffMemberEntity.getLastName()));
        } else {
            staffMemberText.setText("");
        }
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }

}