package com.github.clboettcher.bonappetit.app.staff;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Custom {@link ArrayAdapter} for displaying {@link StaffMemberEntity}s in a {@link ListView}.
 */
public class StaffMemberAdapter extends ArrayAdapter<StaffMemberEntity> {

    /**
     * Constructor.
     *
     * @param context           The current context.
     * @param staffMembers      The objects to represent in the ListView.
     */
    public StaffMemberAdapter(Context context, List<StaffMemberEntity> staffMembers) {
        super(context, 0, staffMembers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        StaffMemberEntity staffMember = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1,
                    parent, false);
        }
        // Lookup view for data population
        TextView text1 = (TextView) convertView.findViewById(android.R.id.text1);
        // Populate the data into the template view using the data object
        text1.setText(String.format("%s %s", staffMember.getFirstName(), staffMember.getLastName()));
        text1.setTag(staffMember);
        // Return the completed view to render on screen
        return convertView;
    }
}
