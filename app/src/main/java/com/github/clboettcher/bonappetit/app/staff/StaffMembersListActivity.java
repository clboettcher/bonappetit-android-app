package com.github.clboettcher.bonappetit.app.staff;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.activity.BonAppetitBaseActivity;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;
import com.github.clboettcher.bonappetit.app.staff.event.PerformStaffMembersUpdateEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateFailedEvent;
import com.github.clboettcher.bonappetit.app.staff.event.StaffMembersUpdateSuccessfulEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class StaffMembersListActivity extends BonAppetitBaseActivity {

    private static final String TAG = StaffMembersListActivity.class.getName();

    /**
     * Comparator for {@link StaffMemberEntity} that compares by first name, then by last name.
     */
    public static final Comparator<StaffMemberEntity> STAFF_MEMBER_ENTITY_COMPARATOR = new Comparator<StaffMemberEntity>() {
        @Override
        public int compare(StaffMemberEntity lhs, StaffMemberEntity rhs) {
            int firstNameComparison = lhs.getFirstName().compareTo(rhs.getFirstName());
            if (firstNameComparison == 0) {
                return lhs.getLastName().compareTo(rhs.getLastName());
            } else {
                return firstNameComparison;
            }
        }
    };

    @BindView(R.id.staffMembersListViewSwitcher)
    ViewSwitcher viewSwitcher;

    @BindView(R.id.staffMembersListProgressView)
    View progressView;

    @BindView(R.id.staffMembersListValueView)
    View valueView;

    @Inject
    StaffMemberDao staffMemberDao;

    @BindView(R.id.staffMembersListView)
    ListView staffMembersListView;

    @Inject
    EventBus bus;

    private ArrayAdapter<StaffMemberEntity> adapter;

    private List<StaffMemberEntity> staffMemberDtos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_members_list);
        ButterKnife.bind(this);

        Log.i(TAG, "StaffMembersListActivity created. Initializing the adapter");
        this.staffMemberDtos = new ArrayList<>(staffMemberDao.list());
        adapter = new StaffMemberAdapter(this, staffMemberDtos);
        staffMembersListView.setAdapter(adapter);
        adapter.sort(STAFF_MEMBER_ENTITY_COMPARATOR);
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
            updateStaffMembers();
        } else {
            Log.i(TAG, "StaffMembersListActivity resuming. There are staff members in the db. " +
                    "No need to update.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_staff_member_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.actionUpdate:
                updateStaffMembers();
                return true;
        }
        return false;
    }

    private void updateStaffMembers() {
        Log.i(TAG, "Forcing staff member update");
        this.showProgressView();
        bus.post(new PerformStaffMembersUpdateEvent());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStaffMembersUpdatedEvent(StaffMembersUpdateSuccessfulEvent event) {
        Log.i(TAG, "Received StaffMembersUpdatedEvent. Updating the list view with " +
                "the contents from the database.");
        Toast.makeText(this, "Staff members updated, refreshing", Toast.LENGTH_SHORT).show();
        adapter.clear();
        this.staffMemberDtos = staffMemberDao.list();
        adapter.addAll(this.staffMemberDtos);
        adapter.sort(STAFF_MEMBER_ENTITY_COMPARATOR);
        adapter.notifyDataSetChanged();
        this.showValueView();
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
        this.showValueView();
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
