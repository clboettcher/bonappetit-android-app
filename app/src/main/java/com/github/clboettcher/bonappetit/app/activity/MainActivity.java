package com.github.clboettcher.bonappetit.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;
import com.github.clboettcher.bonappetit.app.preferences.BonAppetitPreferencesActivity;
import com.github.clboettcher.bonappetit.app.staff.StaffMembersListActivity;

public class MainActivity extends BonAppetitBaseActivity {

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

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }
}
