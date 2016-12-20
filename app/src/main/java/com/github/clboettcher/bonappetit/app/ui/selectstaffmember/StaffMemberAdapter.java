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
package com.github.clboettcher.bonappetit.app.ui.selectstaffmember;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;

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
