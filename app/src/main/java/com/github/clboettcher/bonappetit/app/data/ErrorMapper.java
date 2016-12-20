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
