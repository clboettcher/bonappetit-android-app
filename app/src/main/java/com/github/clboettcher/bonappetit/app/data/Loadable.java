package com.github.clboettcher.bonappetit.app.data;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Loadable<T> {

    private enum LoadingState {
        INITIAL,
        LOADING,
        LOADED,
        FAILED
    }

    private LoadingState loadingState;
    private T value;

    private Loadable(LoadingState loadingState) {
        this.loadingState = loadingState;
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

    public static <T> Loadable<T> failed() {
        return new Loadable<>(LoadingState.FAILED);
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
        return value;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("loadingState", loadingState)
                .append("value", value)
                .toString();
    }
}
