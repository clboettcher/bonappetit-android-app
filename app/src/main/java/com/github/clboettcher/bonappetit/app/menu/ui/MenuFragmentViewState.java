package com.github.clboettcher.bonappetit.app.menu.ui;

import com.github.clboettcher.bonappetit.app.R;

public enum MenuFragmentViewState {

    OK(R.id.fragmentMenuActiveRoot),
    NO_CUSTOMER(R.id.fragmentMenuInactiveRoot),
    MENU_UPDATE_IN_PROGRESS(R.id.fragmentMenuUpdateInProgressRoot),
    MENU_UPDATE_FAILED(R.id.fragmentMenuUpdateFailedRoot);

    private int viewId;

    MenuFragmentViewState(int viewId) {
        this.viewId = viewId;
    }

    public int getViewId() {
        return viewId;
    }
}
