package com.github.clboettcher.bonappetit.app.staff;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.service.ApiProvider;
import com.github.clboettcher.bonappetit.app.staff.event.PerformStaffMembersUpdateEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateFailedEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateSuccessfulEvent;
import com.github.clboettcher.bonappetit.server.staff.to.StaffMemberDto;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.util.List;

public class StaffMemberService {

    private static final String TAG = StaffMemberService.class.getName();

    private StaffMemberDao staffMemberDao;
    private EventBus bus;
    private ApiProvider apiProvider;
    private StaffMemberEntityMapper mapper;

    @Inject
    public StaffMemberService(StaffMemberDao staffMemberDao,
                              StaffMemberEntityMapper mapper,
                              EventBus eventBus,
                              ApiProvider apiProvider) {
        Log.i(TAG, "Creating StaffMemberService. Registering for events.");
        this.staffMemberDao = staffMemberDao;
        this.mapper = mapper;
        this.bus = eventBus;
        this.apiProvider = apiProvider;
        bus.register(this);
    }

    @Subscribe
    public void onUpdateStaffMembersEvent(PerformStaffMembersUpdateEvent event) {
        Log.i(TAG, "Reveiced PerformStaffMembersUpdateEvent. Creating UpdateStaffMembersTask to fetch" +
                " the latest staff members from the server.");

        Call<List<StaffMemberDto>> listCall = apiProvider
                .getStaffMemberApi()
                .listStaffMembers();
        listCall.enqueue(new Callback<List<StaffMemberDto>>() {
            @Override
            public void onResponse(Call<List<StaffMemberDto>> call, Response<List<StaffMemberDto>> response) {
                if (response.isSuccessful()) {
                    staffMemberDao.save(mapper.mapToStaffMemberEntities(response.body()));
                    Log.i(TAG, "Staff member update successful");
                    bus.post(new StaffMembersUpdateSuccessfulEvent());
                } else {
                    Log.e(TAG, String.format("Staff member update failed: %d %s",
                            response.code(),
                            response.message()));
                    bus.post(new StaffMembersUpdateFailedEvent(response.code(), response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<StaffMemberDto>> call, Throwable t) {
                Log.e(TAG, "Staff member update failed", t);
                bus.post(new StaffMembersUpdateFailedEvent(t));
            }
        });
    }
}
