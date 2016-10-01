package com.github.clboettcher.bonappetit.app.staff;


import java.util.*;

public class StaffMemberDao {
    private Map<Long, StaffMemberEntity> idToStaffMemberEntity = new HashMap<>();

    public void save(List<StaffMemberEntity> staffMembers) {
        if (staffMembers == null) {
            throw new IllegalArgumentException("staffMembers null.");
        }
        for (StaffMemberEntity staffMember : staffMembers) {
            idToStaffMemberEntity.put(staffMember.getId(), staffMember);
        }
    }

    public void saveOrUpdate(StaffMemberEntity staffMember) {
        this.idToStaffMemberEntity.put(staffMember.getId(), staffMember);
    }

    public List<StaffMemberEntity> list() {
        return Collections.unmodifiableList(new ArrayList<>(this.idToStaffMemberEntity.values()));
    }
}
