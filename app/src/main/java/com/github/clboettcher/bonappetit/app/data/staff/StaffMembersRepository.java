package com.github.clboettcher.bonappetit.app.data.staff;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.core.ConfigProvider;
import com.github.clboettcher.bonappetit.app.data.ErrorCode;
import com.github.clboettcher.bonappetit.app.data.ErrorMapper;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.staff.event.PerformStaffMembersUpdateEvent;
import com.github.clboettcher.bonappetit.app.data.staff.event.StaffMembersUpdateCompletedEvent;
import com.github.clboettcher.bonappetit.server.staff.api.dto.StaffMemberDto;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class StaffMembersRepository {

    private static final String TAG = StaffMembersRepository.class.getName();

    private Context context;
    private StaffMemberService staffMemberService;
    private StaffMemberDao staffMemberDao;
    private StaffMemberEntityMapper staffMemberEntityMapper;
    private EventBus bus;
    private ConfigProvider configProvider;
    private AtomicReference<Loadable<List<StaffMemberEntity>>> staffMembers = new
            AtomicReference<>(Loadable.<List<StaffMemberEntity>>initial());

    @Inject
    public StaffMembersRepository(Context context,
                                  StaffMemberService staffMemberService,
                                  StaffMemberDao staffMemberDao,
                                  StaffMemberEntityMapper staffMemberEntityMapper,
                                  EventBus bus,
                                  ConfigProvider configProvider) {
        this.context = context;
        this.staffMemberService = staffMemberService;
        this.staffMemberDao = staffMemberDao;
        this.staffMemberEntityMapper = staffMemberEntityMapper;
        this.bus = bus;
        this.configProvider = configProvider;

        bus.register(this);
    }

    public Loadable<List<StaffMemberEntity>> getStaffMembers() {
        return staffMembers.get();
    }

    public void updateStaffMembers() {
        this.updateStaffMembers(null);
    }

    @Subscribe
    public void updateStaffMembers(PerformStaffMembersUpdateEvent event) {
        this.staffMembers.set(Loadable.<List<StaffMemberEntity>>loading());
        if (configProvider.useTestData()) {
            updateStaffMembersWithTestData();
        } else {
            fetchStaffMembersFromServer();
        }
    }

    /**
     * Updates the staff member db content with content from local files.
     */
    private void updateStaffMembersWithTestData() {
        Log.i(TAG, "Test data enabled. Using local staff member test data.");
        List<StaffMemberDto> staffMemberDtos;
        try {
            staffMemberDtos = configProvider.readRawResourceAsJsonArray(
                    R.raw.staff_members, StaffMemberDto.class);
        } catch (IOException e) {
            Log.e(TAG, "Failed to read staff member test data from resources. Update aborted.", e);
            this.staffMembers.set(Loadable.<List<StaffMemberEntity>>failed(ErrorCode.ERR_RESOURCE_ACCESS_FAILED));
            bus.post(new StaffMembersUpdateCompletedEvent());
            return;
        }

        List<StaffMemberEntity> staffMemberEntities = staffMemberEntityMapper
                .mapToStaffMemberEntities(staffMemberDtos);
        staffMemberDao.save(staffMemberEntities);
        this.staffMembers.set(Loadable.loaded(staffMemberEntities));
        bus.post(new StaffMembersUpdateCompletedEvent());
    }


    private void fetchStaffMembersFromServer() {
        Log.i(TAG, "Fetching the staff members from server.");
        staffMemberService.getStaffMembers(new Callback<List<StaffMemberDto>>() {
            @Override
            public void onResponse(Call<List<StaffMemberDto>> call, Response<List<StaffMemberDto>> response) {
                if (response.isSuccessful()) {
                    List<StaffMemberEntity> staffMemberEntities = staffMemberEntityMapper
                            .mapToStaffMemberEntities(response.body());
                    staffMemberDao.save(staffMemberEntities);
                    Log.i(TAG, "Staff member update successful");
                    StaffMembersRepository.this.staffMembers.set(
                            Loadable.loaded(staffMemberEntities));
                    bus.post(new StaffMembersUpdateCompletedEvent());
                } else {
                    String errorMsg = String.format("Staff member update failed: %d %s",
                            response.code(),
                            response.message());
                    Log.e(TAG, errorMsg);

                    ErrorCode errorCode = ErrorMapper.toErrorCode(response.code());

                    StaffMembersRepository.this.staffMembers.set(
                            Loadable.<List<StaffMemberEntity>>failed(errorCode));
                    bus.post(new StaffMembersUpdateCompletedEvent());
                }
            }

            @Override
            public void onFailure(Call<List<StaffMemberDto>> call, Throwable t) {
                Log.e(TAG, "Staff member update failed", t);
                ErrorCode errorCode = ErrorMapper.toErrorCode(t);
                StaffMembersRepository.this.staffMembers.set(
                        Loadable.<List<StaffMemberEntity>>failed(errorCode));
                bus.post(new StaffMembersUpdateCompletedEvent());

                if (BonAppetitApplication.DEBUG_TOASTS_ENABLED) {
                    String errorMsg = String.format("Staff members update failed: %s (%s)",
                            t.getMessage(),
                            t.getClass().getName());
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
