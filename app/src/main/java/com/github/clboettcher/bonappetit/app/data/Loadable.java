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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Loadable<T> {

    private enum LoadingState {
        INITIAL,
        LOADING,
        LOADED,
        FAILED;
    }

    private LoadingState loadingState;

    private T value;
    private ErrorCode errorCode;

    private Loadable(LoadingState loadingState) {
        this.loadingState = loadingState;
    }

    public Loadable(LoadingState loadingState, ErrorCode errorCode) {
        this.loadingState = loadingState;
        this.errorCode = errorCode;
    }

    private Loadable(LoadingState loadingState, T value) {
        this.loadingState = loadingState;
        this.value = value;
    }

    public static <T> Loadable<T> initial() {
        return new Loadable<>(LoadingState.INITIAL);
    }

    public static <T> Loadable<T> loading() {
        return new Loadable<>(LoadingState.LOADING);
    }

    public static <T> Loadable<T> loaded(T value) {
        return new Loadable<>(LoadingState.LOADED, value);
    }

    public static <T> Loadable<T> failed(ErrorCode errorCode) {
        return new Loadable<>(LoadingState.FAILED, errorCode);
    }

    public boolean isInitial() {
        return this.loadingState == LoadingState.INITIAL;
    }

    public boolean isLoaded() {
        return loadingState == LoadingState.LOADED;
    }

    public boolean isLoading() {
        return loadingState == LoadingState.LOADING;
    }

    public boolean isFailed() {
        return loadingState == LoadingState.FAILED;
    }

    public T getValue() {
        if (this.loadingState != LoadingState.LOADED) {
            throw new IllegalStateException(String.format("The value can only be queried if %s is %s. Check" +
                            "with #isLoaded() before calling this method.",
                    LoadingState.class.getSimpleName(),
                    LoadingState.LOADED));
        }
        return value;
    }

    public ErrorCode getErrorCode() {
        if (this.loadingState != LoadingState.FAILED) {
            throw new IllegalStateException(String.format("%s can only be queried if %s is %s. Check" +
                            "with #isFailed() before calling this method.",
                    ErrorCode.class.getSimpleName(),
                    LoadingState.class.getSimpleName(),
                    LoadingState.FAILED));
        }
        return errorCode;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("loadingState", loadingState)
                .append("value", value)
                .toString();
    }
}
