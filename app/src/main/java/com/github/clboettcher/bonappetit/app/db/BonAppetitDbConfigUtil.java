package com.github.clboettcher.bonappetit.app.db;

import com.github.clboettcher.bonappetit.app.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.staff.StaffMemberRefEntity;
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
            StaffMemberRefEntity.class
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
