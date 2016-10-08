package com.github.clboettcher.bonappetit.app.menu;

import com.github.clboettcher.bonappetit.app.menu.entity.MenuEntity;
import com.github.clboettcher.bonappetit.server.menu.api.dto.MenuDto;

public class MenuEntityMapper {

    public MenuEntity mapToMenuEntity(MenuDto menuDto) {
        MenuEntity menuEntity = new MenuEntity();
        menuEntity.setId(menuDto.getId());
        return menuEntity;
    }
}
