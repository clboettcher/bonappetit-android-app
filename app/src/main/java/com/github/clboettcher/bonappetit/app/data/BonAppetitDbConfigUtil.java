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
