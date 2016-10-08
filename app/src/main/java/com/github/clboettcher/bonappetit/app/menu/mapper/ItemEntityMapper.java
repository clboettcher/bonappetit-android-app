package com.github.clboettcher.bonappetit.app.menu.mapper;

import com.github.clboettcher.bonappetit.app.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.menu.entity.ItemEntityType;
import com.github.clboettcher.bonappetit.app.menu.entity.MenuEntity;
import com.github.clboettcher.bonappetit.server.menu.api.dto.ItemDto;
import com.github.clboettcher.bonappetit.server.menu.api.dto.ItemDtoType;
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
