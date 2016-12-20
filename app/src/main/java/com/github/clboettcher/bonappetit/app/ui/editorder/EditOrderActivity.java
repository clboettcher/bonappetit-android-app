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
import android.content.Intent;
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
import com.github.clboettcher.bonappetit.app.data.order.OrderDao;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderFactory;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefEntity;
import com.github.clboettcher.bonappetit.app.ui.BonAppetitBaseActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import com.google.common.base.Optional;
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
    StaffMemberRefDao staffMemberRefDao;

    @Inject
    CustomerDao customerDao;

    @Inject
    PriceCalculator priceCalculator;

    // TODO: initialize item in on create or when it is accessed. Reasoning: need to check if item still present.
//    private ItemEntity item;
//    private PriceCalculator priceCalculator = new PriceCalculatorImpl();

    private EditOrderActivityMode mode;
    /**
     * The ID of the item that is ordered. From intent extras.
     */
    private Long itemId;
    private ItemEntity item;

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, String.format("onCreate()"));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        // Set the action bar subtitle
        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setSubtitle(getString(R.string.activity_take_orders_title));
        }

        // Load the item by the id contained in the intent extras
        Bundle extras = getIntent().getExtras();
        itemId = extras.getLong(MENU_ITEM_ID_INTENT_EXTRA_KEY);

        ItemEntity item = getRefreshedItemOrFinishIfNotPresent(itemId);
        if (item == null) {
            // The activity is finishing ...
            return;
        } else {
            this.item = item;
        }

        // Load the order by the id contained in the intent extras (mode EDIT)
        // or create a new one (mode CREATE)
        Long orderId = extras.getLong(ORDER_ID_INTENT_EXTRA_KEY, -1);
        if (orderId < 0) {
            itemOrder = new ItemOrderEntity();
            itemOrder.setItem(item);
            Optional<CustomerEntity> customerOpt = customerDao.get();
            itemOrder.setCustomer(customerOpt.get());
            Optional<StaffMemberRefEntity> staffMemberRefEntityOptional = staffMemberRefDao.get();
            StaffMemberEntity staffMember = staffMemberRefEntityOptional.get()
                    .getStaffMemberEntity();
            itemOrder.setStaffMember(staffMember);
            mode = EditOrderActivityMode.CREATE;
        } else {
            itemOrder = orderDao.get(orderId);
            // Make sure the item and options are refreshed. Those properties are guaranteed to be present
            // at this point.
            itemDao.refresh(itemOrder.getItem());
            Collection<OptionOrderEntity> optionOrderEntities = itemOrder.getOptionOrderEntities();
            for (OptionOrderEntity optionOrderEntity : optionOrderEntities) {
                optionDao.refresh(optionOrderEntity.getOption());
            }
            mode = EditOrderActivityMode.EDIT;
        }

        // Different texts are used in the UI depending on the mode
        setTextsAccordingToMode(this.item.getTitle());

        // The options (if any)
        initOptions();

        // The edit text field for notes
        note = (EditText) findViewById(R.id.activityEditOrderEditTextNote);
        note.setText(itemOrder.getNote());

        // Total Price
        updateTotalPrice();
    }

    /**
     * Checks whether the database contains an item with the given ID and returns it.
     * <p>
     * If no item with the given ID is found the activity is finished. If an order exists for
     * the item it is deleted. If an item is found it is returned.
     *
     * @param itemId The item ID.
     * @return The retrieved item.
     */
    private ItemEntity getRefreshedItemOrFinishIfNotPresent(Long itemId) {
        Optional<ItemEntity> itemOpt = itemDao.get(itemId);
        if (!itemOpt.isPresent()) {
            Toast.makeText(this, getString(R.string.activity_edit_order_item_removed),
                    Toast.LENGTH_LONG).show();
            if (mode == EditOrderActivityMode.EDIT) {
                orderDao.delete(itemOrder);
            }
            startActivity(new Intent(this, TakeOrdersActivity.class));
            finish();
            return null;
        } else {
            return itemOpt.get();
        }
    }

    private void initOptions() {
        if (item.hasOptions()) {
            // Initialize the list of orders depending on mode.
            // For mode CREATE a new order is created for each option with
            // the default configuration found in the option.
            // For mode EDIT the the ordered options are already present in the item order.
            Collection<OptionOrderEntity> orders;
            switch (mode) {
                case CREATE:
                    Collection<OptionEntity> options = item.getOptions();
                    orders = OptionOrderFactory.createOrders(options, itemOrder);
                    // Add to the option orders associated with the item order
                    // to be saved in the DB.
                    itemOrder.getOptionOrderEntities().addAll(orders);
                    break;
                case EDIT:
                    orders = itemOrder.getOptionOrderEntities();
                    break;
                default:
                    throw new IllegalStateException(String.format("Unknown enum value %s.%s",
                            EditOrderActivityMode.class.getName(),
                            mode));
            }

            // Sort the option orders by index of their corresponding option
            List<OptionOrderEntity> ordersSorted = new ArrayList<>(orders);
            Collections.sort(ordersSorted, new Comparator<OptionOrderEntity>() {
                // TODO: refactor to not query db here.
                @Override
                public int compare(OptionOrderEntity lhs, OptionOrderEntity rhs) {
                    OptionEntity lhsOption = lhs.getOption();
                    OptionEntity rhsOption = rhs.getOption();
                    return lhsOption.getIndex()
                            .compareTo(rhsOption.getIndex());
                }
            });

            for (final OptionOrderEntity optionOrder : ordersSorted) {
                // Get the corresponding Option
                final OptionEntity option = optionOrder.getOption();
                // Need to refresh fields because only the id is stored for foreign objects,
                // see http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_2.html#Foreign-Objects
                optionDao.refresh(option);

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

        // A different string is prefixed before the item name depending on the mode
        String headingPrefix;
        switch (mode) {
            case CREATE:
                headingPrefix = getString(R.string.activity_edit_order_create_heading);
                confirmButton.setText(getString(R.string.activity_edit_order_action_confirm_create));
                cancelButton.setText(getString(R.string.activity_edit_order_action_abort_create));
                break;
            case EDIT:
                headingPrefix = getString(R.string.activity_edit_order_edit_heading);
                confirmButton.setText(getString(R.string.activity_edit_order_confirm_edit));
                cancelButton.setText(getString(R.string.activity_edit_order_abort_edit));
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown value %s.%s",
                        EditOrderActivityMode.class.getName(),
                        mode));
        }

        heading.setText(String.format("%s %s", headingPrefix, itemTitle));
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

        Optional<StaffMemberRefEntity> staffMemberRefEntityOptional = staffMemberRefDao.get();
        StaffMemberEntity staffMember = staffMemberRefEntityOptional.get()
                .getStaffMemberEntity();
        itemOrder.setStaffMember(staffMember);
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