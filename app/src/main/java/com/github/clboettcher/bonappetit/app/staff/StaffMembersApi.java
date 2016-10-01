package com.github.clboettcher.bonappetit.app.staff;

import com.github.clboettcher.bonappetit.server.staff.to.StaffMemberDto;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface StaffMembersApi {

    @GET("staffMembers")
    Call<List<StaffMemberDto>> listStaffMembers();
}
