package com.github.clboettcher.bonappetit.app.menu.mapper;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.menu.entity.ItemEntity;
import com.github.clboettcher.bonappetit.app.menu.entity.OptionEntity;
import com.github.clboettcher.bonappetit.app.menu.entity.OptionEntityFactory;
import com.github.clboettcher.bonappetit.app.menu.entity.RadioItemEntity;
import com.github.clboettcher.bonappetit.server.menu.api.dto.*;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class OptionEntityMapper {

    private static final String TAG = OptionEntityMapper.class.getName();

    public Collection<OptionEntity> mapToOptionEntites(Collection<OptionDto> optionDtos, ItemEntity itemEntity) {
        if (CollectionUtils.isEmpty(optionDtos)) {
            return Collections.emptySet();
        }

        Collection<OptionEntity> result = new HashSet<>();

        for (OptionDto optionDto : optionDtos) {
            OptionEntity optionEntity = mapToOptionEntity(optionDto, itemEntity);
            CollectionUtils.addIgnoreNull(result, optionEntity);
        }

        return result;
    }

    private OptionEntity mapToOptionEntity(OptionDto optionDto, ItemEntity itemEntity) {
        if (optionDto instanceof CheckboxOptionDto) {
            CheckboxOptionDto checkboxOptionDto = (CheckboxOptionDto) optionDto;
            return OptionEntityFactory.newCheckboxOption(
                    checkboxOptionDto.getId(),
                    checkboxOptionDto.getTitle(),
                    checkboxOptionDto.getIndex(),
                    checkboxOptionDto.getPriceDiff(),
                    checkboxOptionDto.getDefaultChecked(),
                    itemEntity
            );
        } else if (optionDto instanceof ValueOptionDto) {
            ValueOptionDto valueOptionDto = (ValueOptionDto) optionDto;
            return OptionEntityFactory.newValueOption(
                    valueOptionDto.getId(),
                    valueOptionDto.getTitle(),
                    valueOptionDto.getIndex(),
                    valueOptionDto.getPriceDiff(),
                    valueOptionDto.getDefaultValue(),
                    itemEntity
            );
        } else if (optionDto instanceof RadioOptionDto) {
            RadioOptionDto radioOptionDto = (RadioOptionDto) optionDto;
            OptionEntity radioOption = OptionEntityFactory.newRadioOption(
                    radioOptionDto.getId(),
                    radioOptionDto.getTitle(),
                    radioOptionDto.getIndex(),
                    itemEntity
            );

            RadioItemEntity defaultSelected = mapToRadioItemEntity(radioOptionDto.getDefaultSelected(),
                    radioOption);
            Collection<RadioItemEntity> radioItems = mapToRadioItemEntites(radioOptionDto.getRadioItems(),
                    radioOption);

            radioOption.setDefaultSelectedItem(defaultSelected);
            radioOption.setRadioItemEntities(radioItems);

            return radioOption;
        } else {
            Log.e(TAG, String.format("Skipping unknown subtype of %s: %s",
                    OptionDto.class.getName(),
                    optionDto.getClass().getName()
            ));
            return null;
        }
    }

    private Collection<RadioItemEntity> mapToRadioItemEntites(Collection<RadioItemDto> radioItemDtos,
                                                              OptionEntity radioOption) {
        if (CollectionUtils.isEmpty(radioItemDtos)) {
            return Collections.emptySet();
        }

        Collection<RadioItemEntity> result = new HashSet<>();

        for (RadioItemDto radioItemDto : radioItemDtos) {
            result.add(mapToRadioItemEntity(radioItemDto, radioOption));
        }

        return result;
    }

    private RadioItemEntity mapToRadioItemEntity(RadioItemDto radioItemDto, OptionEntity radioOption) {
        RadioItemEntity radioItemEntity = new RadioItemEntity();

        radioItemEntity.setId(radioItemDto.getId());
        radioItemEntity.setTitle(radioItemDto.getTitle());
        radioItemEntity.setPriceDiff(radioItemDto.getPriceDiff());
        radioItemEntity.setIndex(radioItemDto.getIndex());
        radioItemEntity.setOption(radioOption);

        return radioItemEntity;
    }
}
