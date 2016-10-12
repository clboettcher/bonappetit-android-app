package com.github.clboettcher.bonappetit.app.data.staff;

import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StaffMemberEntityMapper {

    public List<StaffMemberEntity> mapToStaffMemberEntities(List<StaffMemberDto> dtos) {
        if (CollectionUtils.isEmpty(dtos)) {
            return Collections.emptyList();
        }
        List<StaffMemberEntity> result = new ArrayList<>();
        for (StaffMemberDto dto : dtos) {
            StaffMemberEntity entity = mapToStaffMemberEntity(dto);
            result.add(entity);
        }

        return result;
    }

    private StaffMemberEntity mapToStaffMemberEntity(StaffMemberDto dto) {
        StaffMemberEntity entity = new StaffMemberEntity();
        entity.setId(dto.getId());
        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        return entity;
    }
}
