package com.github.clboettcher.bonappetit.app.ui.selectstaffmember;

import com.github.clboettcher.bonappetit.app.R;

public enum StaffMembersListActivityViewState {

    OK(R.id.staffMembersListValueView),
    UPDATE_IN_PROGRESS(R.id.staffMembersListProgressView),
    UPDATE_FAILED(R.id.staffMembersListFailedView);

    private int viewId;

    StaffMembersListActivityViewState(int viewId) {
        this.viewId = viewId;
    }

    public int getViewId() {
        return viewId;
    }
}
