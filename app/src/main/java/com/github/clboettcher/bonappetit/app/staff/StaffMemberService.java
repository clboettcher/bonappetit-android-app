package com.github.clboettcher.bonappetit.app.staff;

import android.util.Log;
import com.github.clboettcher.bonappetit.app.ConfigProvider;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.service.ApiProvider;
import com.github.clboettcher.bonappetit.app.staff.event.PerformStaffMembersUpdateEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateFailedEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateSuccessfulEvent;
import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

public class StaffMemberService {

    private static final String TAG = StaffMemberService.class.getName();

    private StaffMemberDao staffMemberDao;
    private EventBus bus;
    private ApiProvider apiProvider;
    private ConfigProvider configProvider;
    private StaffMemberEntityMapper staffMemberEntityMapper;

    @Inject
    public StaffMemberService(StaffMemberDao staffMemberDao,
                              StaffMemberEntityMapper staffMemberEntityMapper,
                              EventBus eventBus,
                              ApiProvider apiProvider, ConfigProvider configProvider) {
        Log.i(TAG, "Creating StaffMemberService. Registering for events.");
        this.configProvider = configProvider;
        this.staffMemberDao = staffMemberDao;
        this.staffMemberEntityMapper = staffMemberEntityMapper;
        this.bus = eventBus;
        this.apiProvider = apiProvider;
        bus.register(this);
    }

    @Subscribe
    public void onUpdateStaffMembersEvent(PerformStaffMembersUpdateEvent event) {
        if (configProvider.useTestData()) {
            Log.i(TAG, "Test data enabled. Using local staff member test data.");
            updateStaffMembersWithTestData();
        } else {
            Log.i(TAG, "Reveiced PerformStaffMembersUpdateEvent. Creating UpdateStaffMembersTask to fetch" +
                    " the latest staff members from the server.");
            fetchStaffMembersFromServer();
        }
    }

    /**
     * Updates the staff member db content with content from local files.
     */
    private void updateStaffMembersWithTestData() {
        List<StaffMemberDto> staffMemberDtos;
        try {
            staffMemberDtos = configProvider.readRawResourceAsJsonArray(
                    R.raw.staff_members, StaffMemberDto.class);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read staff member test data from resources. Update aborted.", e);
            return;
        }

        staffMemberDao.save(staffMemberEntityMapper.mapToStaffMemberEntities(staffMemberDtos));
        bus.post(new StaffMembersUpdateSuccessfulEvent());
    }


    private void fetchStaffMembersFromServer() {
        Call<List<StaffMemberDto>> listCall = apiProvider
                .getStaffMemberApi()
                .listStaffMembers();
        listCall.enqueue(new Callback<List<StaffMemberDto>>() {
            @Override
            public void onResponse(Call<List<StaffMemberDto>> call, Response<List<StaffMemberDto>> response) {
                if (response.isSuccessful()) {
                    staffMemberDao.save(staffMemberEntityMapper.mapToStaffMemberEntities(response.body()));
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
