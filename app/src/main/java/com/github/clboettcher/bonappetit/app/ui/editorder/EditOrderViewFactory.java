package com.github.clboettcher.bonappetit.app.ui.editorder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntityType;
import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioItemEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioOption;
import com.github.clboettcher.bonappetit.app.data.order.entity.CheckboxOptionOrder;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.RadioOptionOrder;
import com.github.clboettcher.bonappetit.app.data.order.entity.ValueOptionOrder;
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
        OptionEntity option = optionOrder.getOption();
        TableRow wrapper;
        switch (option.getType()) {
            case CHECKBOX:
                wrapper = createViewForCheckboxOptionOrder(optionOrder, layoutInflater,
                        viewGroup,
                        callback);
                break;
            case VALUE:
                wrapper = createViewForValueOptionOrder(optionOrder, layoutInflater, viewGroup);
                break;
            case RADIO:
                wrapper = createViewForRadioOptionOrder(optionOrder, layoutInflater, viewGroup,
                        callback);
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown enum value %s.%s",
                        OptionEntityType.class.getName(),
                        option.getType()));
        }
        return wrapper;
    }

    /**
     * Creates a new {@link TableRow} to represent the given {@link CheckboxOptionOrder} in the UI.
     *
     * @param optionOrder    The order. The entity returned by {@link CheckboxOptionOrder#getOption()} must be refreshed
     *                       prior to calling this method.
     * @param layoutInflater The layout inflater to instantiate the actual view.
     * @param viewGroup      The view group to set as parent of the inflated layout.
     * @param callback       The callback to register for changes in the checked state of the checkbox.
     * @return The new view.
     */
    private static TableRow createViewForCheckboxOptionOrder(final CheckboxOptionOrder optionOrder,
                                                             LayoutInflater layoutInflater,
                                                             ViewGroup viewGroup,
                                                             final EditOrderActivityCallback callback) {
        // TODO: evaluate CheckboxOptionView instead of creating the view here.
        TableRow checkboxRoot = (TableRow) layoutInflater
                .inflate(R.layout.activity_edit_order_checkbox_order, viewGroup, false);
        checkboxRoot.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        // The checkbox
        CheckBox checkbox = (CheckBox)
                checkboxRoot.findViewById(R.id.activityEditOrderCheckboxOrder);

        OptionEntity option = optionOrder.getOption();
        checkbox.setText(option.getTitle());
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

    private static TableRow createViewForValueOptionOrder(final ValueOptionOrder optionOrder, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        TableRow integerOptionWrapperTableRow = (TableRow) layoutInflater
                .inflate(R.layout.activity_edit_order_value_option, viewGroup, false);
        integerOptionWrapperTableRow.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Name
        TextView optionName = (TextView) integerOptionWrapperTableRow
                .findViewById(R.id.textview_order_option_type_integer_name);
        OptionEntity option = optionOrder.getOption();
        optionName.setText(option.getTitle());

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
            }
        });
        // TODO: need to register callback to update the total price?

        return integerOptionWrapperTableRow;
    }

    private static TableRow createViewForRadioOptionOrder(final RadioOptionOrder optionOrder,
                                                          LayoutInflater layoutInflater,
                                                          ViewGroup viewGroup,
                                                          final EditOrderActivityCallback callback) {
        RadioOption option = optionOrder.getOption();
        Log.d(TAG, "Creating wrapper for RADIO-Option " + option.getTitle());

        TableRow radioOptionWrapperTableRow = (TableRow) layoutInflater.inflate(
                R.layout.activity_edit_order_radio_option, viewGroup, false);
        radioOptionWrapperTableRow.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Title
        TextView optionName = (TextView) radioOptionWrapperTableRow.findViewById(
                R.id.textview_order_option_type_radio_name);
        optionName.setText(option.getTitle());

        // Items
        RadioGroup radioGroup = (RadioGroup) radioOptionWrapperTableRow.findViewById(
                R.id.radiogroup_order_options_type_radio);

        // Sort the radio items
        Collection<RadioItemEntity> radioItems = option.getRadioItemEntities();
        if (CollectionUtils.sizeIsEmpty(radioItems)) {
            throw new IllegalStateException(String.format("Radio items is null or empty " +
                    "for option %s, corresponding option order is %s", option, optionOrder));
        }
        List<RadioItemEntity> radioItemsSorted = new ArrayList<>(radioItems);
        Collections.sort(radioItemsSorted, RadioItemEntityComparator.INSTANCE);

        RadioItemEntity selectedItem = optionOrder.getSelectedRadioItem();
        for (final RadioItemEntity radioItem : radioItemsSorted) {
            // Each RadioItem is one RadioButton in the RadioGroup; first create the RadioButton corresponding to the RadioItem
            RadioButton radioButton = (RadioButton) layoutInflater.inflate(
                    R.layout.activity_edit_order_radio_option_item, radioGroup, false);
            radioButton.setTag(radioItem);
            radioButton.setText(radioItem.getTitle());
            radioButton.setOnClickListener(new RadioButton.OnClickListener() {
                public void onClick(View radioButton) {
                    optionOrder.setSelectedRadioItem(((RadioItemEntity) radioButton.getTag()));
                    // The total price might change due to the selection of another radio button
                    callback.updateTotalPrice();
                }
            });
            if (radioItem.getId().equals(selectedItem.getId())) {
                radioButton.setChecked(true);
            }

            // Add the RadioButton to the RadioGroup
            radioGroup.addView(radioButton);
        }

        return radioOptionWrapperTableRow;
    }

}
