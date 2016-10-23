package com.github.clboettcher.bonappetit.app.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.ui.editorder.EditOrderActivity;

import java.util.Collections;
import java.util.List;

public class MenuItemsAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<ItemEntity> items;
    private ItemEntityComparator itemEntityComparator = new ItemEntityComparator();

    public MenuItemsAdapter(Context context, List<ItemEntity> items) {
        this.context = context;
        this.items = items;
    }

    public void update(List<ItemEntity> newItemList) {
        this.items.clear();
        this.items.addAll(newItemList);
        Collections.sort(this.items, itemEntityComparator);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return items.size();
    }

    public Object getItem(int position) {
        return null; // tables.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View cell;

        if (convertView != null) {
            cell = convertView;
        } else {
            cell = LayoutInflater.from(context).inflate(R.layout.fragment_menu_grid_item_button,
                    parent, false);
        }

        Button itemButton = (Button) cell.findViewById(R.id.fragmentMenuGridViewButtonItem);
        ItemEntity item = items.get(position);

        itemButton.setTag(item);
        itemButton.setOnClickListener(this);
        itemButton.setText(item.getTitle());
        // See https://color.adobe.com/de/create/color-wheel/?base=2&rule=Compound&selected=4&name=Mein%20Color-Thema&mode=rgb&rgbvalues=0.25279337460610923,0.8,0.39786816443828626,0.4895950309545819,0.6,0.5188654690987887,0.6326004047060523,1,0.4159917182576365,0.9031454864406192,0.6659917182576365,1,0.49208483124324887,0.25279337460610923,0.8&swatchOrder=0,1,2,3,4
        // for colors.
        switch (item.getType()) {
            case FOOD:
                itemButton.setBackgroundColor(Color.rgb(125, 153, 132));
                break;
            case DRINK_ALCOHOLIC:
                itemButton.setBackgroundColor(Color.rgb(230, 170, 255));
                break;
            case DRINK_NON_ALCOHOLIC:
                itemButton.setBackgroundColor(Color.rgb(125, 64, 204));
                break;
        }

        return cell;
    }

    /**
     * If a view representing an item is clicked the {@link EditOrderActivity} is
     * started to take the order for the item associated with the view.
     */
    public void onClick(View view) {
        ItemEntity item = (ItemEntity) view.getTag();
        Intent intent = new Intent(context, EditOrderActivity.class);
        intent.putExtra(EditOrderActivity.MENU_ITEM_ID_INTENT_EXTRA_KEY, item.getId());
        context.startActivity(intent);
    }
}
