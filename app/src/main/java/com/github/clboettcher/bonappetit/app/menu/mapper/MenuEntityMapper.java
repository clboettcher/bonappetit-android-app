package com.github.clboettcher.bonappetit.app.menu.mapper;

import com.github.clboettcher.bonappetit.app.menu.entity.MenuEntity;
import com.github.clboettcher.bonappetit.server.menu.api.dto.MenuDto;

import javax.inject.Inject;

public class MenuEntityMapper {

    private ItemEntityMapper itemEntityMapper;

    @Inject
    public MenuEntityMapper(ItemEntityMapper itemEntityMapper) {
        this.itemEntityMapper = itemEntityMapper;
    }


    public MenuEntity mapToMenuEntity(MenuDto menuDto) {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuDto.getId());
        menuEntity.setItems(itemEntityMapper.mapToItemEntities(menuDto.getItems(), menuEntity));
        return menuEntity;
    }
}
