package com.github.clboettcher.bonappetit.app.staff;

import com.github.clboettcher.bonappetit.server.staff.api.StaffMemberResource;
import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface StaffMembersApi {

    @GET(StaffMemberResource.ROOT_PATH)
    Call<List<StaffMemberDto>> listStaffMembers();
}
