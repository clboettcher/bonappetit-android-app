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
