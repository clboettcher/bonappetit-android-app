package com.github.clboettcher.bonappetit.app.data;

public enum ErrorCode {
    // General
    ERR_GENERAL,

    // Errors related to server requests.
    ERR_CONN_TIMEOUT,
    ERR_SRV_NOT_IMPLEMENTED,
    ERR_SRV_INTERNAL,
    ERR_SRV_BAD_GATEWAY,
    ERR_SRV_SERVICE_UNAVAILABLE,
    ERR_SRV_GATEWAY_TIMEOUT,
    ERR_CLI_BAD_REQUEST,
    ERR_CLI_UNAUTHORIZED,
    ERR_CLI_FORBIDDEN,
    ERR_CLI_NOT_FOUND,
    ERR_CLI_METHOD_NOT_ALLOWED,

    // Other errors.

    ERR_RESOURCE_ACCESS_FAILED
}
