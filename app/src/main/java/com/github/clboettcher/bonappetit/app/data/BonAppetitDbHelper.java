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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.github.clboettcher.bonappetit.app.R;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Database helper class used to manage the creation
 * and upgrading of the database. This class also usually provides
 * the DAOs used by the other DATABASE_CLASSES.
 */
public class BonAppetitDbHelper extends OrmLiteSqliteOpenHelper {

    /**
     * The name of the database file.
     */
    private static final String DATABASE_NAME = "bonappetit.db";

    /**
     * The version of the database. Must be incremented whenever the schema changes.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * The tag for logging.
     */
    private static final String TAG = BonAppetitDbHelper.class.getName();

    /**
     * Constructor that initializes the helper.
     *
     * @param context The application context.
     */
    public BonAppetitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable
     * statements here to create the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        Log.i(TAG, "onCreate called");
        try {
            for (Class clazz : BonAppetitDbConfigUtil.DATABASE_CLASSES) {
                Log.i(TAG, String.format("Trying to create table for class '%s'", clazz.getName()));
                TableUtils.createTable(connectionSource, clazz);
                Log.i(TAG, String.format("Successfully created table for class '%s'", clazz.getName()));
            }
        } catch (SQLException e) {
            Log.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number.
     * This allows you to adjust the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.i(TAG, String.format("onUpgrade called. Upgrading from v%d to v%d", oldVersion, newVersion));
    }
}
