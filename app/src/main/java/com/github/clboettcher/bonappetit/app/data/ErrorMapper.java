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

    public static ErrorCode toErrorCode(int httpCode) {
        // 5XX Server Error
        if (httpCode == 500) {
            return ErrorCode.ERR_SRV_INTERNAL;
        } else if (httpCode == 501) {
            return ErrorCode.ERR_SRV_NOT_IMPLEMENTED;
        } else if (httpCode == 502) {
            return ErrorCode.ERR_SRV_BAD_GATEWAY;
        } else if (httpCode == 503) {
            return ErrorCode.ERR_SRV_SERVICE_UNAVAILABLE;
        } else if (httpCode == 504) {
            return ErrorCode.ERR_SRV_GATEWAY_TIMEOUT;
        }

        // 4XX Client Error
        if (httpCode == 400) {
            return ErrorCode.ERR_CLI_BAD_REQUEST;
        } else if (httpCode == 401) {
            return ErrorCode.ERR_CLI_UNAUTHORIZED;
        } else if (httpCode == 403) {
            return ErrorCode.ERR_CLI_FORBIDDEN;
        } else if (httpCode == 404) {
            return ErrorCode.ERR_CLI_NOT_FOUND;
        } else if (httpCode == 405) {
            return ErrorCode.ERR_CLI_METHOD_NOT_ALLOWED;
        }

        return ErrorCode.ERR_GENERAL;
    }
}
