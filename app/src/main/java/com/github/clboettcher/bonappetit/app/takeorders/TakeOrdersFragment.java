package com.github.clboettcher.bonappetit.app.takeorders;

import com.github.clboettcher.bonappetit.app.activity.BonAppetitBaseV4Fragment;

/**
 * Base class for fragments which can update their state.
 */
public abstract class TakeOrdersFragment extends BonAppetitBaseV4Fragment {
    public abstract void update();
}