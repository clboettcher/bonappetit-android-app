package com.github.clboettcher.bonappetit.app.menu.entity;

public interface Option {

    Long getId();

    String getTitle();

    Integer getIndex();

    ItemEntity getItem();
}
