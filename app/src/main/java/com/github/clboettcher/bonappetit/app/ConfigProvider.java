package com.github.clboettcher.bonappetit.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import javax.inject.Inject;

/**
 * Provides config values from the shared preferences.
 * <p>
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
     * Constructor setting the specified properties.
     *
     * @param context     see {@link #context}.
     * @param sharedPrefs see {@link #sharedPrefs}.
     */
    @Inject
    public ConfigProvider(Context context, SharedPreferences sharedPrefs) {
        this.context = context;
        this.sharedPrefs = sharedPrefs;
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
}
