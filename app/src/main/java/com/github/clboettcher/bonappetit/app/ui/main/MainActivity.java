package com.github.clboettcher.bonappetit.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.menu.event.PerformMenuUpdateEvent;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberDao;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberEntity;
import com.github.clboettcher.bonappetit.app.data.staff.StaffMemberRefDao;
import com.github.clboettcher.bonappetit.app.ui.BonAppetitBaseActivity;
import com.github.clboettcher.bonappetit.app.ui.preferences.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.ui.selectstaffmember.StaffMembersListActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class MainActivity extends BonAppetitBaseActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final int SELECT_STAFF_MEMBER_REQUEST = 1;
    public static final String EXTRA_SELECTED_STAFF_MEMBER_ID = "EXTRA_SELECTED_STAFF_MEMBER_ID";

    @Inject
    StaffMemberRefDao staffMemberRefDao;

    @Inject
    StaffMemberDao staffMemberDao;

    @Inject
    EventBus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.actionSettings:
                startActivity(new Intent(this, BonAppetitPreferencesActivity.class));
                return true;
        }
        return false;
    }

    @OnClick(R.id.switchToStaffMembersListActivity)
    public void onSwitchToStaffMembersListActivityButtonClicked() {
        Intent intent = new Intent(this, StaffMembersListActivity.class);
        startActivityForResult(intent, SELECT_STAFF_MEMBER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_STAFF_MEMBER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Long staffMemberId = data.getLongExtra(EXTRA_SELECTED_STAFF_MEMBER_ID, -1L);
                if (staffMemberId == -1) {
                    throw new IllegalStateException(String.format("Expected %s to return intent containing the " +
                                    "id of the selected staff member",
                            StaffMembersListActivity.class.getName()));
                }
                StaffMemberEntity selectedStaffMember = staffMemberDao.getById(staffMemberId);
                Log.i(TAG, String.format("Selected staff member: %s", selectedStaffMember));
                staffMemberRefDao.save(selectedStaffMember);
            } else {
                Log.i(TAG, String.format("Received activity result for " +
                        "request code SELECT_STAFF_MEMBER_REQUEST with " +
                        "a result code different from RESULT_OK: %d", resultCode));
            }
        }
    }

    @OnClick(R.id.switchToTakeOrdersActivity)
    public void onSwitchToTakeOrdersActivity() {
        Intent intent = new Intent(this, TakeOrdersActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.updateMenu)
    public void onUpdateMenu() {
        bus.post(new PerformMenuUpdateEvent());
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }
}
