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
package com.github.clboettcher.bonappetit.app.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.widget.Toast;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.github.clboettcher.bonappetit.app.R;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Provides config values from the shared preferences.
 * <p/>
 * It adds additional value by combining multiple configuration properties to
 * build more high level properties.
 */
public class ConfigProvider {

    /**
     * The application context to access the string table.
     */
    private Context context;

    /**
     * The shared preferences to access.
     */
    private SharedPreferences sharedPrefs;

    /**
     * Jackson object mapper.
     */
    private ObjectMapper objectMapper;

    /**
     * Constructor setting the specified properties.
     *
     * @param context      see {@link #context}.
     * @param sharedPrefs  see {@link #sharedPrefs}.
     * @param objectMapper see {@link #objectMapper}.
     */
    @Inject
    public ConfigProvider(Context context, SharedPreferences sharedPrefs, ObjectMapper objectMapper) {
        this.context = context;
        this.sharedPrefs = sharedPrefs;
        this.objectMapper = objectMapper;
    }

    /**
     * @return The server base URL to prepend to all API requests.
     */
    public String getBaseUrl() {
        String keyHost = context.getString(R.string.prefs_key_server_host_name);
        String keyPort = context.getString(R.string.prefs_key_server_port);
        String keyPath = context.getString(R.string.prefs_key_server_context_path);

        String host = sharedPrefs.getString(keyHost, null);
        Integer port = Integer.valueOf(sharedPrefs.getString(keyPort, "8080"));
        String path = sharedPrefs.getString(keyPath, "");

        if (host == null) {
            Toast.makeText(this.context, "Missing configuration value server hostname. " +
                    "Connecting the server will not work.", Toast.LENGTH_LONG).show();
        }

        return String.format("http://%s:%d%s", host, port, path);
    }

    /**
     * @return Whether to use test data or fetch data from the server.
     */
    public boolean useTestData() {
        String key = context.getString(R.string.prefs_key_use_test_data);
        return sharedPrefs.getBoolean(key, false);
    }

    /**
     * Reads the given resource ID and parses it as JSON list.
     *
     * @param resourceId The resource ID (from {@link R.raw}).
     * @param clazz      The type that the list elements should be deserialized to.
     * @param <T>        The class reference of the type that the list elements should be deserialized to.
     * @return The read list.
     * @throws IOException If I/O error occur during reading the input stream.
     */
    public <T> List<T> readRawResourceAsJsonArray(int resourceId, Class<T> clazz) throws IOException {
        String s = this.readRawResourceAsString(resourceId);
        CollectionType valueType = objectMapper.getTypeFactory().constructCollectionType(List.class, clazz);
        return objectMapper.readValue(s, valueType);
    }

    /**
     * Reads a raw resource with the given ID and returns its content as string.
     *
     * @param resourceId The ID (from {@link R.raw}).
     * @return The resource as string.
     * @throws IOException If I/O error occur during reading the input stream.
     */
    public String readRawResourceAsString(int resourceId) throws IOException {
        Resources resources = context.getResources();
        try (InputStream inputStream = resources.openRawResource(resourceId)) {
            return IOUtils.toString(inputStream, "UTF-8");
        }
    }

    public String getUsername() {
        String key = context.getString(R.string.prefs_key_username);
        String username = sharedPrefs.getString(key, null);
        if (StringUtils.isBlank(username)) {
            throw new IllegalStateException("Config preference username is missing.");
        }
        return username;
    }

    public String getPassword() {
        String key = context.getString(R.string.prefs_key_password);
        String pw = sharedPrefs.getString(key, null);
        if (StringUtils.isBlank(pw)) {
            throw new IllegalStateException("Config preference password is missing.");
        }
        return pw;
    }
}
