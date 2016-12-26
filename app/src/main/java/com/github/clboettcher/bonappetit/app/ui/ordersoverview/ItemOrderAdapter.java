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
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.order.OrdersResource;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.ui.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.ui.editorder.EditOrderActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersFragment;

import java.util.List;

public class ItemOrderAdapter extends ArrayAdapter<ItemOrderEntity> implements View.OnClickListener {

    private static final String TAG = ItemOrderAdapter.class.getName();
    private List<ItemOrderEntity> itemOrders;
    private LayoutInflater layoutInflater;
    private Context context;
    private OrdersResource ordersResource;
    private OnSwitchToTabListener mListener;
    private TakeOrdersFragment takeOrdersFragment;
    public static final int NOTE_LENGTH_THRESHOLD = 30;

    ItemOrderAdapter(List<ItemOrderEntity> itemOrders,
                     OrdersResource ordersResource,
                     OnSwitchToTabListener mListener,
                     LayoutInflater layoutInflater,
                     Context context,
                     TakeOrdersFragment takeOrdersFragment) {
        super(context, 0, itemOrders);
        this.itemOrders = itemOrders;
        this.layoutInflater = layoutInflater;
        this.context = context;
        this.ordersResource = ordersResource;
        this.mListener = mListener;
        this.takeOrdersFragment = takeOrdersFragment;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View cell;

        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            cell = layoutInflater.inflate(
                    R.layout.fragment_orders_overview_order, parent, false);
        } else {
            cell = convertView;
        }

        ItemOrderEntity itemOrder = itemOrders.get(position);
        // Fill the cell with life.
        // Title
        TextView itemName = (TextView) cell.findViewById(R.id.fragmentOrdersOverviewOrderTitle);
        itemName.setText(itemOrder.getItemTitle());

        // Options
        TextView options = (TextView) cell.findViewById(R.id.fragmentOrdersOverviewOrderOptions);
        StringBuilder optionsText = new StringBuilder("");

        for (OptionOrderEntity optionOrder : itemOrder.getOptionOrderEntities()) {
            switch (optionOrder.getOptionType()) {
                case CHECKBOX:
                    if (optionOrder.getChecked()) {
                        optionsText.append(optionOrder.getOptionTitle()).append("\n");
                    }
                    break;
                case VALUE:
                    // append the name and count of the integer option (if > 0)
                    if (optionOrder.getValue() != 0) {
                        optionsText
                                .append(optionOrder.getOptionTitle())
                                .append(" (")
                                .append(optionOrder.getValue())
                                .append("x)")
                                .append("\n");
                    }
                    break;
                case RADIO:
                    String selectedItemTitle = optionOrder.getSelectedRadioItemEntity().getTitle();
                    optionsText.append(selectedItemTitle).append("\n");
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown enum value: %s.%s",
                            OptionEntityType.class.getName(),
                            optionOrder.getOptionType()));
            }
        }

        options.setText(optionsText.toString());

        // Note
        TextView note = (TextView) cell.findViewById(R.id.fragmentOrdersOverviewOrderNote);
        String noteText = itemOrder.getNote();
        if (noteText.length() > NOTE_LENGTH_THRESHOLD) {
            noteText = noteText.substring(0, NOTE_LENGTH_THRESHOLD - 3) + "...";
        }
        note.setText(noteText);

        // Actions

        // EDIT Button
        Button edit = (Button) cell.findViewById(R.id.fragmentOrdersOverviewOrderButtonEdit);
        edit.setTag(itemOrder);
        edit.setOnClickListener(this);

        // Delete Button
        Button delete = (Button) cell.findViewById(R.id.fragmentOrdersOverviewOrderButtonDelete);
        delete.setTag(itemOrder);
        delete.setOnClickListener(this);

        return cell;
    }


    public void onClick(View view) {
        final ItemOrderEntity itemOrder = (ItemOrderEntity) view.getTag();
        int id = view.getId();
        switch (id) {
            // Edit order button pressed
            case R.id.fragmentOrdersOverviewOrderButtonEdit:
                Intent intent = new Intent(context, EditOrderActivity.class);
                intent.putExtra(EditOrderActivity.ORDER_ID_INTENT_EXTRA_KEY, itemOrder.getId());
                intent.putExtra(EditOrderActivity.MENU_ITEM_ID_INTENT_EXTRA_KEY, itemOrder.getItemId());
                context.startActivity(intent);
                break;
            // Delete order button pressed
            case R.id.fragmentOrdersOverviewOrderButtonDelete:
                new AlertDialog.Builder(context)
                        .setTitle(context.getString(R.string.fragment_orders_overview_dialog_delete_order_title))
                        .setMessage(String.format(context.getString(
                                R.string.fragment_orders_overview_dialog_delete_order_safeguard_question),
                                itemOrder.getItemTitle()))
                        .setIcon(R.drawable.ic_alerts_and_states_warning)
                        .setPositiveButton(context.getString(R.string.general_action_yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                deleteOrder(itemOrder);
                                long newOrderCount = ordersResource.count();
                                if (newOrderCount == 0) {
                                    mListener.onSwitchToTab(TakeOrdersActivity.TAB_MENU);
                                } else {
                                    // Update the fragment to reflect the order deletion
                                    takeOrdersFragment.update();
                                }
                            }
                        })
                        .setNegativeButton(context.getString(R.string.general_action_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast t = Toast.makeText(context,
                                        context.getString(R.string.general_toast_cancel_notification),
                                        Toast.LENGTH_SHORT);
                                t.show();
                            }
                        })
                        .show();
                break;
        }
    }


    private void deleteOrder(ItemOrderEntity order) {
        Log.d(TAG, "in delegateOrderDeletion()");
        ordersResource.deleteOrder(order);
        Toast.makeText(context,
                context.getString(R.string.fragment_orders_overview_dialog_delete_order_toast_success),
                Toast.LENGTH_SHORT).show();
    }
}
