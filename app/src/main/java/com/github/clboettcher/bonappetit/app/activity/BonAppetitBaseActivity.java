package com.github.clboettcher.bonappetit.app.activity;

import android.app.Activity;
import android.os.Bundle;
import com.github.clboettcher.bonappetit.app.application.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;

public abstract class BonAppetitBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies(((BonAppetitApplication) getApplication()).getDiComponent());
    }

    protected abstract void injectDependencies(DiComponent diComponent);
}
