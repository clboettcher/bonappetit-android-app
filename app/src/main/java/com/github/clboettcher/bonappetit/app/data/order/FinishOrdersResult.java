package com.github.clboettcher.bonappetit.app.data.order;

import com.github.clboettcher.bonappetit.app.data.ErrorCode;

public class FinishOrdersResult {

    private ErrorCode errorCode;

    public FinishOrdersResult(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public FinishOrdersResult() {
    }

    public boolean isSuccessful() {
        return errorCode == null;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
