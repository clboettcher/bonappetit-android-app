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
package com.github.clboettcher.bonappetit.app.ui.menu;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.ViewFlipper;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerDao;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.menu.MenuResource;
import com.github.clboettcher.bonappetit.app.data.menu.dao.ItemDao;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.MenuEntity;
import com.github.clboettcher.bonappetit.app.data.menu.event.MenuUpdateCompletedEvent;
import com.github.clboettcher.bonappetit.app.data.menu.event.PerformMenuUpdateEvent;
import com.github.clboettcher.bonappetit.app.data.order.OrdersResource;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.ui.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.ui.UiUtils;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersFragment;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MenuFragment extends TakeOrdersFragment {

    private static final String TAG = MenuFragment.class.getName();

    private TextView staffMemberText;

    private TextView customerText;

    private OnSwitchToTabListener mListener;

    private ViewFlipper viewFlipper;

    @Inject
    EventBus bus;

    @Inject
    MenuResource menuResource;

    @Inject
    ItemDao itemDao;

    @Inject
    CustomerDao customerDao;

    @Inject
    StaffMemberDao staffMemberDao;

    @Inject
    SelectedStaffMemberDao selectedStaffMemberDao;

    @Inject
    OrdersResource ordersResource;

    private TextView errorCode;
    private Button switchToOverviewButton;

    private Comparator<ItemEntity> itemEntityComparator = new ItemEntityComparator();
    private MenuItemsAdapter menuItemsAdapter;
    private boolean initialized = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnSwitchToTabListener) context;
        } catch (ClassCastException e) {
            throw new IllegalStateException(String.format("%s must implement OnSwitchToTabListener", context), e);
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
        this.viewFlipper = (ViewFlipper) rootView.findViewById(R.id.fragmentMenuViewFlipper);
        this.errorCode = (TextView) rootView.findViewById(R.id.generalFailedViewErrorCode);
        this.switchToOverviewButton = (Button) rootView.findViewById(R.id.fragmentMenuButtonSwitchToOverview);

        Button retryButton = (Button) rootView.findViewById(R.id.generalFailedViewButtonRetry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(MenuFragmentViewState.MENU_UPDATE_IN_PROGRESS);
                menuResource.updateMenu();
            }
        });

        // Configure the inactive view
        initInactiveView(rootView);

        // Configure the active view
        initActiveView(rootView);

        updateCustomerAndStaffMember();
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
    public void onMenuUpdateFailed(MenuUpdateCompletedEvent event) {
        this.update();
    }


    @Override
    public void update() {
        Log.d(TAG, String.format("update() called. Initialized is %s customerDao is %s",
                this.initialized, this.customerDao));

        // Make sure this instance is the one that has been dependency injected. Apparently
        // android sets all fields to null when the fragment is destroyed. Unfortunately the update() method
        // might get called after this leading to NPE. Discovered while changing the orientation during a
        // network operation in progress.
        if (!initialized) {
            return;
        }

        final Optional<CustomerEntity> customer = customerDao.get();
        Loadable<MenuEntity> menuLoadable = menuResource.getMenu();
        Log.i(TAG, String.format("update() called. Customer is %s. Menu is %s",
                customer.orNull(),
                menuLoadable));

        updateCustomerAndStaffMember();
        refreshOrderCounts();

        if (customer.isPresent()) {
            // Customer is there, see if menu update went through OK
            if (menuLoadable.isLoading()) {
                // Show progress bar
                this.setState(MenuFragmentViewState.MENU_UPDATE_IN_PROGRESS);
            } else if (menuLoadable.isFailed()) {
                // Show error view with a button to try again
                this.errorCode.setText(menuLoadable.getErrorCode().toString());
                this.setState(MenuFragmentViewState.MENU_UPDATE_FAILED);
            } else if (menuLoadable.isLoaded()) {
                MenuEntity menuEntity = menuLoadable.getValue();
                final List<ItemEntity> itemList = Lists.newArrayList(menuEntity.getItems());
                Log.i(TAG, String.format("onCreate(): Initializing the fragment with %d item(s)",
                        itemList.size()));
                Collections.sort(itemList, itemEntityComparator);
                this.menuItemsAdapter.update(itemList);
                this.setState(MenuFragmentViewState.OK);
            }
        } else {
            // Show customer missing view
            this.setState(MenuFragmentViewState.NO_CUSTOMER);
        }

        // Enable switch to overview button only if we have any orders.
        if (this.ordersResource.count() == 0) {
            switchToOverviewButton.setEnabled(false);
        } else {
            switchToOverviewButton.setEnabled(true);
        }
    }

    private void setState(MenuFragmentViewState state) {
        View newView = viewFlipper.findViewById(state.getViewId());
        int newIndex = viewFlipper.indexOfChild(newView);
        viewFlipper.setDisplayedChild(newIndex);
    }

    private void updateCustomerAndStaffMember() {
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
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        Log.d(TAG, "Injecting dependencies");
        diComponent.inject(this);
        this.initialized = true;
    }

}