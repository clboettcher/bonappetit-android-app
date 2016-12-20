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
package com.github.clboettcher.bonappetit.app.data;

import com.github.clboettcher.bonappetit.app.data.customer.CustomerEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.MenuEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.RadioItemEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.ItemOrderEntity;
import com.github.clboettcher.bonappetit.app.data.order.entity.OptionOrderEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefEntity;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class BonAppetitDbConfigUtil extends OrmLiteConfigUtil {

    /**
     * All classes that are mapped to a db table.
     */
    public static final Class<?>[] DATABASE_CLASSES = new Class[]{
            // Staff
            StaffMemberEntity.class,
            StaffMemberRefEntity.class,

            // Customer
            CustomerEntity.class,

            // Menu
            MenuEntity.class,
            ItemEntity.class,
            OptionEntity.class,
            RadioItemEntity.class,

            // Order
            ItemOrderEntity.class,
            OptionOrderEntity.class,
    };

    /**
     * Creates the config file that makes ORM lite fast.
     * <p/>
     * For this to work you have to:
     * <p/>
     * - Run with work dir \app\src\main
     * - Create dir res/raw
     */
    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile("ormlite_config.txt", DATABASE_CLASSES);
    }
}
