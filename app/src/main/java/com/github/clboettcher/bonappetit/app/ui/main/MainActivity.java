package com.github.clboettcher.bonappetit.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.core.DiComponent;
import com.github.clboettcher.bonappetit.app.data.menu.event.PerformMenuUpdateEvent;
import com.github.clboettcher.bonappetit.app.ui.BonAppetitBaseActivity;
import com.github.clboettcher.bonappetit.app.ui.preferences.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.ui.selectstaffmember.StaffMembersListActivity;
import com.github.clboettcher.bonappetit.app.ui.takeorders.TakeOrdersActivity;
import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class MainActivity extends BonAppetitBaseActivity {

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
        startActivity(intent);
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
