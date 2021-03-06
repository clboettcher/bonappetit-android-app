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
package com.github.clboettcher.bonappetit.app.data.menu.mapper;

import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.data.menu.entity.ItemEntityType;
import com.github.clboettcher.bonappetit.app.data.menu.entity.MenuEntity;
import com.github.clboettcher.bonappetit.server.menu.api.dto.common.ItemDtoType;
import com.github.clboettcher.bonappetit.server.menu.api.dto.read.ItemDto;
import org.apache.commons.collections4.CollectionUtils;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class ItemEntityMapper {

    private OptionEntityMapper optionEntityMapper;


    @Inject
    public ItemEntityMapper(OptionEntityMapper optionEntityMapper) {
        this.optionEntityMapper = optionEntityMapper;
    }

    public Collection<ItemEntity> mapToItemEntities(Collection<ItemDto> itemDtos, MenuEntity menuEntity) {
        if (CollectionUtils.isEmpty(itemDtos)) {
            return Collections.emptySet();
        }

        Collection<ItemEntity> result = new HashSet<>();

        for (ItemDto itemDto : itemDtos) {
            result.add(mapToItemEntity(itemDto, menuEntity));
        }

        return result;
    }

    private ItemEntity mapToItemEntity(ItemDto itemDto, MenuEntity menuEntity) {
        ItemEntity itemEntity = new ItemEntity();

        itemEntity.setId(itemDto.getId());
        itemEntity.setTitle(itemDto.getTitle());
        itemEntity.setPrice(itemDto.getPrice());
        itemEntity.setType(mapToItemEntityType(itemDto.getType()));
        itemEntity.setOptions(optionEntityMapper.mapToOptionEntites(itemDto.getOptions(),
                itemEntity));
        itemEntity.setMenu(menuEntity);

        return itemEntity;
    }

    private ItemEntityType mapToItemEntityType(ItemDtoType type) {
        switch (type) {
            case FOOD:
                return ItemEntityType.FOOD;
            case DRINK_ALCOHOLIC:
                return ItemEntityType.DRINK_ALCOHOLIC;
            case DRINK_NON_ALCOHOLIC:
                return ItemEntityType.DRINK_NON_ALCOHOLIC;
            default:
                throw new UnsupportedOperationException(String.format("Mapping of enum value %s.%s is not yet supported.",
                        ItemDtoType.class.getSimpleName(),
                        type
                ));
        }
    }
}
