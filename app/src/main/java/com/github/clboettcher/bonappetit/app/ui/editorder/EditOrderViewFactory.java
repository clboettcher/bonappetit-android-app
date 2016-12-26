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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.order.entity.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Builds {@link View}s in the context of the edit order activity.
 */
public class EditOrderViewFactory {

    private static final String TAG = EditOrderViewFactory.class.getName();

    static TableRow createViewForOptionOrder(OptionOrderEntity optionOrder,
                                             LayoutInflater layoutInflater,
                                             ViewGroup viewGroup,
                                             EditOrderActivity callback) {
        TableRow wrapper;
        switch (optionOrder.getOptionType()) {
            case CHECKBOX:
                wrapper = createViewForCheckboxOptionOrder(
                        optionOrder,
                        layoutInflater,
                        viewGroup,
                        callback);
                break;
            case VALUE:
                wrapper = createViewForValueOptionOrder(
                        optionOrder,
                        layoutInflater,
                        viewGroup,
                        callback
                );
                break;
            case RADIO:
                wrapper = createViewForRadioOptionOrder(
                        optionOrder,
                        layoutInflater,
                        viewGroup,
                        callback);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown enum value %s.%s",
                        OptionEntityType.class.getName(),
                        optionOrder.getOptionType()));
        }
        return wrapper;
    }

    /**
     * Creates a new {@link TableRow} to represent the given {@link CheckboxOptionOrder} in the UI.
     *
     * @param optionOrder    The order.
     * @param layoutInflater The layout inflater to instantiate the actual view.
     * @param viewGroup      The view group to set as parent of the inflated layout.
     * @param callback       The callback to register for changes in the checked state of the checkbox.
     * @return The new view.
     */
    private static TableRow createViewForCheckboxOptionOrder(final CheckboxOptionOrder optionOrder,
                                                             LayoutInflater layoutInflater,
                                                             ViewGroup viewGroup,
                                                             final EditOrderActivityCallback callback) {
        TableRow checkboxRoot = (TableRow) layoutInflater
                .inflate(R.layout.activity_edit_order_checkbox_order, viewGroup, false);
        // The checkbox
        CheckBox checkbox = (CheckBox)
                checkboxRoot.findViewById(R.id.activityEditOrderCheckboxOrder);

        checkbox.setText(optionOrder.getOptionTitle());
        checkbox.setChecked(optionOrder.getChecked());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                optionOrder.setChecked(isChecked);
                callback.updateTotalPrice();
            }
        });

        return checkboxRoot;
    }

    private static TableRow createViewForValueOptionOrder(final ValueOptionOrder optionOrder,
                                                          LayoutInflater layoutInflater,
                                                          ViewGroup viewGroup,
                                                          final EditOrderActivityCallback callback) {
        TableRow integerOptionWrapperTableRow = (TableRow) layoutInflater
                .inflate(R.layout.activity_edit_order_value_option, viewGroup, false);

        // Name
        TextView optionName = (TextView) integerOptionWrapperTableRow
                .findViewById(R.id.textview_order_option_type_integer_name);
        optionName.setText(optionOrder.getOptionTitle());

        // Count
        final TextView optionCount = (TextView) integerOptionWrapperTableRow
                .findViewById(R.id.textview_order_option_type_integer_count);
        optionCount.setText(String.valueOf(optionOrder.getValue()));

        // Buttons
        final Button increment = (Button) integerOptionWrapperTableRow
                .findViewById(R.id.button_order_option_type_integer_increment);
        final Button decrement = (Button) integerOptionWrapperTableRow
                .findViewById(R.id.button_order_option_type_integer_decrement);

        // Increment Button
        increment.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currentCount = Integer.valueOf((String) optionCount.getText());
                int newCount = currentCount + 1;
                optionCount.setText(String.valueOf(newCount));
                decrement.setEnabled(true);

                optionOrder.setValue(Integer.valueOf((String) optionCount.getText()));
                callback.updateTotalPrice();
            }
        });

        // Decrement Button
        decrement.setEnabled(Integer.valueOf((String) optionCount.getText()) > 0);
        decrement.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                int currentCount = Integer.valueOf((String) optionCount.getText());
                int newCount = currentCount - 1;

                if (newCount > 0) {
                    optionCount.setText(String.valueOf(newCount));
                } else if (newCount == 0) {
                    decrement.setEnabled(false);
                    optionCount.setText(String.valueOf(newCount));
                } else {
                    // newCount < 0
                    decrement.setEnabled(false);
                }

                optionOrder.setValue(Integer.valueOf((String) optionCount.getText()));
                callback.updateTotalPrice();
            }
        });

        return integerOptionWrapperTableRow;
    }

    private static TableRow createViewForRadioOptionOrder(final RadioOptionOrder optionOrder,
                                                          LayoutInflater layoutInflater,
                                                          ViewGroup viewGroup,
                                                          final EditOrderActivityCallback callback) {
        Log.d(TAG, "Creating wrapper for RADIO-Option " + optionOrder.getOptionTitle());

        TableRow radioOptionWrapper = (TableRow) layoutInflater.inflate(
                R.layout.activity_edit_order_radio_option, viewGroup, false);

        // Title
        TextView optionName = (TextView) radioOptionWrapper.findViewById(
                R.id.activityEditOrderRadioOptionTitle);
        optionName.setText(optionOrder.getOptionTitle());

        // Items
        RadioGroup radioGroup = (RadioGroup) radioOptionWrapper.findViewById(
                R.id.activityEditOrderRadioOptionRadioGroup);

        // Sort the radio items
        Collection<RadioItemOrderEntity> radioItems = optionOrder.getAvailableRadioItemEntities();
        if (CollectionUtils.sizeIsEmpty(radioItems)) {
            throw new IllegalStateException(String.format("Radio items is null or empty " +
                    "for option order %s", optionOrder));
        }
        List<RadioItemOrderEntity> radioItemsSorted = new ArrayList<>(radioItems);
        Collections.sort(radioItemsSorted, RadioItemOrderEntityComparator.INSTANCE);

        RadioItemOrderEntity selectedItem = optionOrder.getSelectedRadioItemEntity();
        for (final RadioItemOrderEntity radioItem : radioItemsSorted) {
            // Each RadioItem is one RadioButton in the RadioGroup; first create the RadioButton
            // corresponding to the RadioItem.
            RadioButton radioButton = (RadioButton) layoutInflater.inflate(
                    R.layout.activity_edit_order_radio_option_item, radioGroup, false);
            radioButton.setTag(radioItem);
            radioButton.setText(radioItem.getTitle());
            radioButton.setOnClickListener(new RadioButton.OnClickListener() {
                public void onClick(View radioButton) {
                    RadioItemOrderEntity radioItem = (RadioItemOrderEntity) radioButton.getTag();
                    Log.d(TAG, String.format("Selected %s", radioItem));
                    optionOrder.setSelectedRadioItemEntity(radioItem);
                    // The total price might change due to the selection of another radio item.
                    callback.updateTotalPrice();
                }
            });

            // Add the RadioButton to the RadioGroup
            Log.i(TAG, String.format("Adding radio button for %s to %s", radioButton.getTag(), radioGroup));
            radioGroup.addView(radioButton);
        }

        // Initial check the correct radio button. Note that this must be done _after_ all the radio buttons
        // have been added to the radio group. Otherwise the checking/un-checking of radio buttons when the user
        // selects another option won't work correctly.
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
            RadioItemOrderEntity selectedRadioItem = optionOrder.getSelectedRadioItemEntity();
            RadioItemOrderEntity currentRadioItem = (RadioItemOrderEntity) radioButton.getTag();

            Log.d(TAG, String.format("Trying to determine if radio item %s should be checked" +
                            " based on the selected radio item %s",
                    currentRadioItem, selectedItem));

            if (selectedRadioItem.getRadioItemId().equals(currentRadioItem.getRadioItemId())) {
                Log.d(TAG, String.format("Setting initial checked = true for %s", currentRadioItem));
                radioButton.setChecked(true);
            }
        }

        return radioOptionWrapper;
    }

}
