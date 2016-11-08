package com.github.clboettcher.bonappetit.app.ui.ordersoverview;

import com.github.clboettcher.bonappetit.app.R;

public enum OrdersOverviewViewState {

    ACTIVE(R.id.fragmentOrdersOverviewActiveRoot),
    INACTIVE(R.id.fragmentOrdersOverviewInactiveRoot),
    LOADING(R.id.fragmentOrdersOverviewLoadingRoot);

    private int viewId;

    OrdersOverviewViewState(int viewId) {
        this.viewId = viewId;
    }

    public int getViewId() {
        return viewId;
    }
}
