package com.github.clboettcher.bonappetit.app.staff;


import com.github.clboettcher.bonappetit.server.staff.to.StaffMemberDto;

import java.util.*;

public class StaffMemberDao {
    // TODO: save entities not dtos when implementing the real db thing
    private Map<Long, StaffMemberDto> idToStaffMemberDto = new HashMap<>();

    public void save(List<StaffMemberDto> staffMemberDtos) {
        if (staffMemberDtos == null) {
            throw new IllegalArgumentException("staffMemberDtos null.");
        }
        for (StaffMemberDto staffMemberDto : staffMemberDtos) {
            idToStaffMemberDto.put(staffMemberDto.getId(), staffMemberDto);
        }
    }

    public void saveOrUpdate(StaffMemberDto staffMemberDto) {
        this.idToStaffMemberDto.put(staffMemberDto.getId(), staffMemberDto);
    }

    public List<StaffMemberDto> list() {
        return Collections.unmodifiableList(new ArrayList<>(this.idToStaffMemberDto.values()));
    }
}
