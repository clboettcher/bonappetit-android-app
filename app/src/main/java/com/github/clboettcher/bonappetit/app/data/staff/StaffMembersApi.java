package com.github.clboettcher.bonappetit.app.data.staff;

import com.github.clboettcher.bonappetit.server.staff.api.StaffMemberManagement;
import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface StaffMembersApi {

    @GET(StaffMemberManagement.ROOT_PATH)
    Call<List<StaffMemberDto>> listStaffMembers();
}
