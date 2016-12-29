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
package com.github.clboettcher.bonappetit.app.ui.editorder;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.gihub.clboettcher.bonappetit.price_calculation.api.PriceCalculator;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerDao;
import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.menu.dao.ItemDao;
import com.github.clboettcher.bonappetit.app.data.menu.dao.OptionDao;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.data.order.ItemOrderDtoMapper;
import com.github.clboettcher.bonappetit.app.data.order.OptionOrderFactory;
import com.github.clboettcher.bonappetit.app.data.order.OrderDao;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.SelectedStaffMemberEntity;
import com.github.clboettcher.bonappetit.app.ui.BonAppetitBaseActivity;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

public class EditOrderActivity extends BonAppetitBaseActivity implements EditOrderActivityCallback {

    private static final String TAG = EditOrderActivity.class.getName();

    public static final String MENU_ITEM_ID_INTENT_EXTRA_KEY = "itemId";
    public static final String ORDER_ID_INTENT_EXTRA_KEY = "orderId";
    private ItemOrderEntity itemOrder;
    private EditText note;

    @Inject
    OrderDao orderDao;

    @Inject
    ItemDao itemDao;

    @Inject
    OptionDao optionDao;

    @Inject
    SelectedStaffMemberDao selectedStaffMemberDao;

    @Inject
    CustomerDao customerDao;

    @Inject
    PriceCalculator priceCalculator;

    private EditOrderActivityMode mode;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        // Set the action bar subtitle
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getString(R.string.activity_take_orders_title));
        }

        // Load the item by the id contained in the intent extras
        Bundle extras = getIntent().getExtras();
        // Load the order by the id contained in the intent extras (mode EDIT)
        // or create a new one (mode CREATE)
        Long orderId = extras.getLong(ORDER_ID_INTENT_EXTRA_KEY, -1);
        List<OptionOrderEntity> optionOrders;
        if (orderId < 0) {
            Long itemId = extras.getLong(MENU_ITEM_ID_INTENT_EXTRA_KEY);
            Optional<ItemEntity> itemOpt = itemDao.get(itemId);
            ItemEntity item;
            if (!itemOpt.isPresent()) {
                throw new IllegalArgumentException(String.format("Cannot create order for item with id %d because " +
                        "that item does not exist", itemId));
            } else {
                item = itemOpt.get();
            }
            itemOrder = new ItemOrderEntity();
            itemOrder.setItemId(item.getId());
            itemOrder.setItemPrice(item.getPrice());
            itemOrder.setItemTitle(item.getTitle());
            itemOrder.setItemType(item.getType());

            Optional<CustomerEntity> customerOpt = customerDao.get();
            itemOrder.setCustomer(customerOpt.get());
            Optional<SelectedStaffMemberEntity> selectedStaffMemberOpt = selectedStaffMemberDao.get();
            if (selectedStaffMemberOpt.isPresent()) {
                SelectedStaffMemberEntity staffMember = selectedStaffMemberOpt.get();
                itemOrder.setSelectedStaffMember(staffMember);
            }

            // Initialize the list of orders depending on mode.
            // For mode CREATE a new order is created for each option with
            // the default configuration found in the option.
            // For mode EDIT the the ordered options are already present in the item order.
            Collection<OptionEntity> options = item.getOptions();
            optionOrders = OptionOrderFactory.createOrders(options, itemOrder);
            // Add to the option orders associated with the item order
            // to be saved in the DB.
            itemOrder.getOptionOrderEntities().addAll(optionOrders);
            mode = EditOrderActivityMode.CREATE;
        } else {
            itemOrder = orderDao.get(orderId);
            optionOrders = Lists.newArrayList(itemOrder.getOptionOrderEntities());
            mode = EditOrderActivityMode.EDIT;
        }

        // Different texts are used in the UI depending on the mode
        setTextsAccordingToMode(this.itemOrder.getItemTitle());

        // The options (if any)
        initOptions(optionOrders);

        // The edit text field for notes
        note = (EditText) findViewById(R.id.activityEditOrderEditTextNote);
        note.setText(itemOrder.getNote());

        // Total Price
        updateTotalPrice();
    }

    private void initOptions(List<OptionOrderEntity> optionOrders) {
        if (CollectionUtils.isNotEmpty(optionOrders)) {
            // Sort the option orders by index of their corresponding option
            List<OptionOrderEntity> ordersSorted = new ArrayList<>(optionOrders);
            Collections.sort(ordersSorted, new Comparator<OptionOrderEntity>() {
                @Override
                public int compare(OptionOrderEntity lhs, OptionOrderEntity rhs) {
                    return lhs.getOptionIndex().compareTo(rhs.getOptionIndex());
                }
            });

            for (final OptionOrderEntity optionOrder : ordersSorted) {
                TableLayout optionsTable = (TableLayout)
                        findViewById(R.id.activityEditOrderOptionsTable);
                TableRow wrapper = EditOrderViewFactory.createViewForOptionOrder(optionOrder,
                        getLayoutInflater(),
                        optionsTable, this);
                optionsTable.addView(wrapper);
            }
        } else {
            // If the item has no options, hide the entire section
            findViewById(R.id.activityEditOrderOptionSectionWrapper)
                    .setVisibility(View.GONE);
        }
    }

    private void setTextsAccordingToMode(String itemTitle) {
        TextView heading = (TextView) findViewById(R.id.activityEditOrderHeading);
        Button confirmButton = (Button) findViewById(R.id.activityEditOrderButtonConfirm);
        Button cancelButton = (Button) findViewById(R.id.activityEditOrderButtonCancel);

        // A different string is appended to the item name depending on the mode
        String headingSuffix;
        switch (mode) {
            case CREATE:
                headingSuffix = getString(R.string.activity_edit_order_create_heading_suffix);
                confirmButton.setText(getString(R.string.activity_edit_order_action_confirm_create));
                cancelButton.setText(getString(R.string.activity_edit_order_action_abort_create));
                break;
            case EDIT:
                headingSuffix = getString(R.string.activity_edit_order_edit_heading_suffix);
                confirmButton.setText(getString(R.string.activity_edit_order_confirm_edit));
                cancelButton.setText(getString(R.string.activity_edit_order_abort_edit));
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown value %s.%s",
                        EditOrderActivityMode.class.getName(),
                        mode));
        }

        heading.setText(String.format("%s %s", itemTitle, headingSuffix));
    }

    public void updateTotalPrice() {
        // The total price
        TextView totalPriceView = (TextView) findViewById(R.id.activityEditOrderTextViewTotalPrice);
        BigDecimal totalPrice = priceCalculator.calculateTotalPrice(ItemOrderDtoMapper.mapToItemOrderDto(itemOrder));
        String priceFormatted = NumberFormat.getCurrencyInstance(Locale.GERMANY).format(totalPrice);
        totalPriceView.setText(priceFormatted);
    }

    @SuppressWarnings("UnusedParameters")
    public void cancelButtonHandler(View target) {
        itemOrder = null;
        Toast t = null;
        switch (mode) {
            case EDIT:
                t = Toast.makeText(this, getString(R.string.activity_edit_order_toast_cancel_edit), Toast.LENGTH_SHORT);
                break;
            case CREATE:
                t = Toast.makeText(this, getString(R.string.activity_edit_order_toast_cancel_create), Toast.LENGTH_SHORT);
                break;
        }
        t.show();
        finish();
    }

    @SuppressWarnings("UnusedParameters")
    public void confirmButtonHandler(View target) {
        itemOrder.setNote(note.getText().toString());

        Optional<SelectedStaffMemberEntity> selectedStaffMemberOpt = selectedStaffMemberDao.get();
        if (selectedStaffMemberOpt.isPresent()) {
            SelectedStaffMemberEntity staffMember = selectedStaffMemberOpt.get();
            itemOrder.setSelectedStaffMember(staffMember);
        }
        itemOrder.setOrderTime(DateTime.now(DateTimeZone.UTC));

        Toast t;
        switch (mode) {
            case EDIT:
                orderDao.update(itemOrder);
                t = Toast.makeText(this, getString(R.string.activity_edit_order_toast_confirm_edit),
                        Toast.LENGTH_SHORT);
                break;
            case CREATE:
                orderDao.save(itemOrder);
                t = Toast.makeText(this, getString(R.string.activity_edit_order_toast_confirm_create),
                        Toast.LENGTH_SHORT);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown enum value %s.%s",
                        EditOrderActivityMode.class.getName(),
                        mode));
        }

        t.show();
        finish();
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }
}