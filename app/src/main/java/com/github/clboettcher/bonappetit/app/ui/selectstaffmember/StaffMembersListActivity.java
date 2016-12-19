package com.github.clboettcher.bonappetit.app.ui.selectstaffmember;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    public static final int SELECT_STAFF_MEMBER_REQUEST = 1;
    public static final String EXTRA_SELECTED_STAFF_MEMBER_ID = "EXTRA_SELECTED_STAFF_MEMBER_ID";
    private static final String TAG = StaffMembersListActivity.class.getName();

    @BindView(R.id.staffMembersListViewSwitcher)
    ViewFlipper viewFlipper;

    @BindView(R.id.staffMembersListProgressView)
    View progressView;

    @BindView(R.id.staffMembersListValueView)
    View valueView;

    @BindView(R.id.generalFailedViewErrorCode)
    TextView errorCode;

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

        Log.i(TAG, String.format("Updating. Staff members loadable is %s", staffMembersLoadable));

        if (staffMembersLoadable.isLoading()) {
            this.setState(StaffMembersListActivityViewState.UPDATE_IN_PROGRESS);
        } else if (staffMembersLoadable.isLoaded()) {
            Log.i(TAG, "Updating the list view with the contents from the repository.");
            adapter.clear();
            adapter.addAll(staffMembersRepository.getStaffMembers().getValue());
            adapter.sort(StaffMemberEntityComparator.INSTANCE);
            adapter.notifyDataSetChanged();
            this.setState(StaffMembersListActivityViewState.OK);
        } else if (staffMembersLoadable.isFailed()) {
            Log.e(TAG, "Staff members update failed ");
            ErrorCode errorCode = staffMembersLoadable.getErrorCode();
            this.errorCode.setText(errorCode.toString());
            this.setState(StaffMembersListActivityViewState.UPDATE_FAILED);
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
        this.setState(StaffMembersListActivityViewState.UPDATE_IN_PROGRESS);
        staffMembersRepository.updateStaffMembers();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStaffMembersUpdateCompletedEvent(StaffMembersUpdateCompletedEvent event) {
        this.update();
    }

    @OnItemClick(R.id.staffMembersListView)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Each list item is tagged with the corresponding staff member object.
        StaffMemberEntity selectedStaffMember = (StaffMemberEntity) view.getTag();
        Intent result = new Intent();
        result.putExtra(EXTRA_SELECTED_STAFF_MEMBER_ID, selectedStaffMember.getId());
        // Set result for the activity that started this one
        setResult(Activity.RESULT_OK, result);
        // Finish this activity.
        finish();
    }

    @OnClick(R.id.generalFailedViewButtonRetry)
    public void onRetryClicked() {
        this.updateStaffMembers();
    }


    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }

    private void setState(StaffMembersListActivityViewState state) {
        View newView = viewFlipper.findViewById(state.getViewId());
        int newIndex = viewFlipper.indexOfChild(newView);
        viewFlipper.setDisplayedChild(newIndex);
    }
}
