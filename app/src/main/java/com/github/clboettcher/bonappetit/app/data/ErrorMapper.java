package com.github.clboettcher.bonappetit.app.data;

import java.net.SocketTimeoutException;

public final class ErrorMapper {
    /**
     * No instance.
     */
    private ErrorMapper() {
    }

    public static ErrorCode toErrorCode(Throwable throwable) {
        if (throwable instanceof SocketTimeoutException) {
            return ErrorCode.ERR_CONN_TIMEOUT;
        }

        return ErrorCode.ERR_GENERAL;
    }
}
