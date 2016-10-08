package com.github.clboettcher.bonappetit.app.takeorders;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.github.clboettcher.bonappetit.app.R;
import com.github.clboettcher.bonappetit.app.activity.BonAppetitBaseFragmentActivity;
import com.github.clboettcher.bonappetit.app.activity.OnSwitchToTabListener;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;

public class TakeOrdersActivity extends BonAppetitBaseFragmentActivity implements ActionBar.TabListener, OnSwitchToTabListener {

    /**
     * The tag for logging.
     */
    public static final String TAG = TakeOrdersActivity.class.getName();

    /**
     * The index of the select customer tab.
     */
    public static final int TAB_SELECT_CUSTOMER = 0;

    /**
     * The index of the menu tab.
     */
    public static final int TAB_MENU = 1;

    /**
     * The index of the order overview tab.
     */
    public static final int TAB_OVERVIEW = 2;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private TakeOrdersPagerAdapter takeOrdersPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.takeOrdersActivityPager)
    ViewPager viewPager;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_orders);
        ButterKnife.bind(this);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        this.actionBar = getActionBar();
        this.actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        this.actionBar.setHomeButtonEnabled(false);
        // comment this in to use the app icon as "home"
        // button (and comment the previous line out ofc)
        // actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        takeOrdersPagerAdapter = new TakeOrdersPagerAdapter(this, getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        viewPager.setAdapter(takeOrdersPagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected(int position = " + position + ")");
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                onSwitchToTab(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < takeOrdersPagerAdapter.getCount(); i++) {
            // CREATE a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(takeOrdersPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_take_orders, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.actionSettings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }


    public void onSwitchToTab(int tabPos) {
        Log.d(TAG, "onSwitchToTab(int tabPos = " + tabPos + ")");
        actionBar.setSelectedNavigationItem(tabPos);
        viewPager.setCurrentItem(tabPos, true);
        takeOrdersPagerAdapter.update(tabPos);
    }

    @Override
    protected void injectDependencies(DiComponent diComponent) {
        diComponent.inject(this);
    }


}
