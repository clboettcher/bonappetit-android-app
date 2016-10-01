package com.github.clboettcher.bonappetit.app.staff;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.activity.BonAppetitBaseActivity;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;
import com.github.clboettcher.bonappetit.app.staff.event.PerformStaffMembersUpdateEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateFailedEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateSuccessfulEvent;
import com.github.clboettcher.bonappetit.server.staff.to.StaffMemberDto;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class StaffMembersListActivity extends BonAppetitBaseActivity {

    private static final String TAG = StaffMembersListActivity.class.getName();

    @Inject
    StaffMemberDao staffMemberDao;

    @BindView(R.id.staffMembersListView)
    ListView staffMembersListView;

    @Inject
    EventBus bus;

    private ArrayAdapter<StaffMemberDto> adapter;

    private List<StaffMemberDto> staffMemberDtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_members_list);
        ButterKnife.bind(this);

        Log.i(TAG, "StaffMembersListActivity created. Initializing the adapter");
        this.staffMemberDtos = new ArrayList<>(staffMemberDao.list());
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, staffMemberDtos);
        staffMembersListView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "StaffMembersListActivity starting. Registering for events");
        this.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "StaffMembersListActivity starting. Unregistering from events");
        this.bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (staffMemberDao.list().isEmpty()) {
            Log.i(TAG, "StaffMembersListActivity resuming. No staff members in the db. Triggering update.");
            bus.post(new PerformStaffMembersUpdateEvent());
        } else {
            Log.i(TAG, "StaffMembersListActivity resuming. There are staff members in the db. " +
                    "No need to update.");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStaffMembersUpdatedEvent(StaffMembersUpdateSuccessfulEvent event) {
        Log.i(TAG, "Received StaffMembersUpdatedEvent. Updating the list view with " +
                "the contents from the database.");
        Toast.makeText(this, "Staff members updated, refreshing", Toast.LENGTH_SHORT).show();
        adapter.clear();
        this.staffMemberDtos = staffMemberDao.list();
        adapter.addAll(this.staffMemberDtos);
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStaffMembersUpdateFailedEvent(StaffMembersUpdateFailedEvent event) {
        Log.e(TAG, "Staff members update failed " + event);
        String errorMsg;
        Throwable throwable = event.getThrowable();
        if (throwable != null) {
            errorMsg = String.format("Staff member update failed: %s (%s)",
                    throwable.getMessage(),
                    throwable.getClass().getName());
        } else {
            errorMsg = String.format("Staff member update failed: %d %s", event.getHttpCode(), event.getHttpMessage());
        }
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @OnItemClick(R.id.staffMembersListView)
    public void onItemClick(ListView view) {
        Log.i(TAG, "Item clicked");
//        bus.post(new FetchStaffMemberByIdEvent(1L));
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }
}
