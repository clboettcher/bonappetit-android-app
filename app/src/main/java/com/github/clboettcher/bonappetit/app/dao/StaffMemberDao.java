package com.github.clboettcher.bonappetit.app.dao;


import com.github.clboettcher.bonappetit.app.service.staffmembers.StaffMemberDto;

import java.util.*;

public class StaffMemberDao {
    private Map<Long, StaffMemberDto> idToStaffMemberDto = new HashMap<>();

    public void save(List<StaffMemberDto> staffMemberDtos) {
        if (staffMemberDtos == null || staffMemberDtos.isEmpty()) {
            throw new IllegalArgumentException("staffMemberDtos null or empty.");
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
