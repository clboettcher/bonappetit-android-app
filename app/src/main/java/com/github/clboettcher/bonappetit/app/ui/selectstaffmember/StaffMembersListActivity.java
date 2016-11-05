package com.github.clboettcher.bonappetit.app.ui.selectstaffmember;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.ErrorCode;
import com.github.clboettcher.bonappetit.app.data.Loadable;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMembersRepository;
import com.github.clboettcher.bonappetit.app.data.staff.event.StaffMembersUpdateCompletedEvent;
import com.github.clboettcher.bonappetit.app.ui.BonAppetitBaseActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class StaffMembersListActivity extends BonAppetitBaseActivity {

    private static final String TAG = StaffMembersListActivity.class.getName();

    @BindView(R.id.staffMembersListViewSwitcher)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.staffMembersListProgressView)
    View progressView;

    @BindView(R.id.staffMembersListValueView)
    View valueView;

    @Inject
    StaffMembersRepository staffMembersRepository;

    @Inject
    StaffMemberRefDao staffMemberRefDao;

    @BindView(R.id.staffMembersListView)
    ListView staffMembersListView;

    @Inject
    EventBus bus;

    private ArrayAdapter<StaffMemberEntity> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_members_list);
        ButterKnife.bind(this);

        Log.i(TAG, "StaffMembersListActivity created. Initializing the adapter");

        adapter = new StaffMemberAdapter(this, new ArrayList<StaffMemberEntity>());
        staffMembersListView.setAdapter(adapter);

        this.update();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Starting. Registering for events");
        this.bus.register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Stopping. Unregistering from events");
        this.bus.unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.update();
    }

    private void update() {
        Loadable<List<StaffMemberEntity>> staffMembersLoadable = staffMembersRepository.getStaffMembers();

        if (staffMembersLoadable.isLoading()) {
            this.showProgressView();
        } else if (staffMembersLoadable.isLoaded()) {
            Log.i(TAG, "Updating the list view with the contents from the repository.");
            Toast.makeText(this, "Staff members updated, refreshing", Toast.LENGTH_SHORT).show();
            adapter.clear();
            adapter.addAll(staffMembersRepository.getStaffMembers().getValue());
            adapter.sort(StaffMemberEntityComparator.INSTANCE);
            adapter.notifyDataSetChanged();
            this.showValueView();
        } else if (staffMembersLoadable.isFailed()) {
            Log.e(TAG, "Staff members update failed ");
            String errorMsg;
            ErrorCode errorCode = staffMembersLoadable.getErrorCode();
            errorMsg = String.format("Staff members update failed: %s",
                    errorCode);
            Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            this.showValueView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_staff_member_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.actionUpdateStaffMembers:
                updateStaffMembers();
                return true;
        }
        return false;
    }

    private void updateStaffMembers() {
        Log.i(TAG, "Forcing staff member update");
        this.showProgressView();
        staffMembersRepository.updateStaffMembers();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStaffMembersUpdatedEvent(StaffMembersUpdateCompletedEvent event) {
        this.update();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStaffMembersUpdateFailedEvent(StaffMembersUpdateCompletedEvent event) {
        this.update();
    }

    @OnItemClick(R.id.staffMembersListView)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        StaffMemberEntity selectedStaffMember = (StaffMemberEntity) view.getTag();
        Log.i(TAG, String.format("Selected staff member: %s", selectedStaffMember));
        staffMemberRefDao.save(selectedStaffMember);

        // We are done here when the user selected who he/she is.
        finish();
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }

    /**
     * Triggers the switcher to show the view containing the progress bar.
     */
    private void showProgressView() {
        Log.d(TAG, "Showing progress view.");
        showView(progressView);
    }

    /**
     * Triggers the switcher to show the view containing the values.
     */
    private void showValueView() {
        Log.d(TAG, "Showing value view.");
        showView(valueView);
    }

    private void showView(View view) {
        // Show the view if not already visible
        if (viewSwitcher.getNextView().getId() == view.getId()) {
            viewSwitcher.showNext();
        }
    }
}
