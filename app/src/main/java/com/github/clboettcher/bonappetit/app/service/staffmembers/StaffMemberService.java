package com.github.clboettcher.bonappetit.app.service.staffmembers;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.Constants;
import com.github.clboettcher.bonappetit.app.dao.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.event.staffmembers.PerformStaffMembersUpdateEvent;
import com.github.clboettcher.bonappetit.app.event.staffmembers.StaffMembersUpdateSuccessfulEvent;
import com.github.clboettcher.bonappetit.app.event.staffmembers.StaffMembersUpdateFailedEvent;
import com.github.clboettcher.bonappetit.app.service.ApiProvider;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.List;

public class StaffMemberService {

    private StaffMemberDao staffMemberDao;
    private EventBus bus;
    private ApiProvider apiProvider;

    @Inject
    public StaffMemberService(StaffMemberDao staffMemberDao, EventBus eventBus, ApiProvider apiProvider) {
        Log.i(Constants.TAG, "Creating StaffMemberService. Registering for events.");
        this.staffMemberDao = staffMemberDao;
        this.bus = eventBus;
        this.apiProvider = apiProvider;
        bus.register(this);
    }

    @Subscribe
    public void onUpdateStaffMembersEvent(PerformStaffMembersUpdateEvent event) {
        Log.i(Constants.TAG, "Reveiced PerformStaffMembersUpdateEvent. Creating UpdateStaffMembersTask to fetch" +
                " the latest staff members from the server.");

        Call<List<StaffMemberDto>> listCall = apiProvider
                .getStaffMemberApi()
                .listStaffMembers();
        listCall.enqueue(new Callback<List<StaffMemberDto>>() {
            @Override
            public void onResponse(Call<List<StaffMemberDto>> call, Response<List<StaffMemberDto>> response) {
                if (response.isSuccessful()) {
                    staffMemberDao.save(response.body());
                    Log.i(Constants.TAG, "Staff member update successful");
                    bus.post(new StaffMembersUpdateSuccessfulEvent());
                } else {
                    Log.e(Constants.TAG, String.format("Staff member update failed: %d %s",
                            response.code(),
                            response.message()));
                    bus.post(new StaffMembersUpdateFailedEvent(response.code(), response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<StaffMemberDto>> call, Throwable t) {
                Log.e(Constants.TAG, "Staff member update failed", t);
                bus.post(new StaffMembersUpdateFailedEvent(t));
            }
        });
    }
}
