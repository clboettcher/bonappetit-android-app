package com.github.clboettcher.bonappetit.app.ui.editorder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.data.order.entity.CheckboxOptionOrder;

public class CheckboxOptionView extends TableRow implements LayoutInflater.Factory {

    private ViewGroup rootView;
    private LayoutInflater layoutInflater;

    public CheckboxOptionView(Context context) {
        super(context);
    }

    public CheckboxOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckboxOptionView(Context context,
                              String optionTitle,
                              final CheckboxOptionOrder order,
                              final EditOrderActivityCallback callback,
                              ViewGroup rootView
    ) {
        super(context);
        this.rootView = rootView;
        LayoutInflater sysInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layoutInflater = sysInflater.cloneInContext(context);
        this.layoutInflater.setFactory(this);
        // TODO how to inflate the layout? Answer: the system creates an instance of this class, not we. Therefore we
        // must pass config in AttributeSet and specify the according xml attrs.
//        layoutInflater
//                .inflate(R.layout.tablerow_edit_item_order_options_type_checkbox, null);
        this.setLayoutParams(new TableLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        // The checkbox
        CheckBox checkbox = (CheckBox)
                findViewById(R.id.activityEditOrderCheckboxOrder);

        checkbox.setText(optionTitle);
        checkbox.setChecked(order.getChecked());
        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                order.setChecked(isChecked);
                callback.updateTotalPrice();
            }
        });

        // TODO dont forget to add >>this<< to the tablelayout container
//        container.addView(checkboxOptionWrapperTableRow);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return this.layoutInflater.inflate(
                R.layout.activity_edit_order_checkbox_order, rootView);
    }
}
