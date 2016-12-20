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
