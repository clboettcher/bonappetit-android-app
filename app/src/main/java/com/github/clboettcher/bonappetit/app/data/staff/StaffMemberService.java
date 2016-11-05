package com.github.clboettcher.bonappetit.app.data.staff;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.data.ApiProvider;
import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import retrofit2.Call;
import retrofit2.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class StaffMemberService {

    private static final String TAG = StaffMemberService.class.getName();
    private ApiProvider apiProvider;

    @Inject
    public StaffMemberService(ApiProvider apiProvider) {
        Log.i(TAG, "Creating StaffMemberService. Registering for events.");
        this.apiProvider = apiProvider;
    }

    public void getStaffMembers(Callback<List<StaffMemberDto>> callback) {
        Call<List<StaffMemberDto>> listCall = apiProvider
                .getStaffMemberApi()
                .listStaffMembers();
        listCall.enqueue(callback);
    }
}
