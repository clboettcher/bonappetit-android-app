package com.github.clboettcher.bonappetit.app.data.menu.entity;

/**
 * The states that a menu update can be in.
 */
public enum MenuUpdateState {
    /**
     * The menu has been created in the db. No update has been requested.
     */
    INITIAL,

    /**
     * The update failed.
     */
    UPDATE_FAILED,

    /**
     * The menu data is currently being updated from the server.
     * Not all properties are filled yet.
     */
    UPDATE_IN_PROGRESS,

    /**
     * The update is completed successfully and we have all data.
     */
    UPDATE_COMPLETED,

}
