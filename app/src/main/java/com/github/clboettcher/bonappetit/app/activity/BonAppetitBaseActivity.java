package com.github.clboettcher.bonappetit.app.activity;

import android.app.Activity;
import android.os.Bundle;
import com.github.clboettcher.bonappetit.app.application.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.dagger.DiComponent;

/**
 * The base activity provides an abstract method that reminds the programmer
 * to inject dependencies into the class since this is a manual operation when using dagger.
 */
public abstract class BonAppetitBaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies(((BonAppetitApplication) getApplication()).getDiComponent());
    }

    /**
     * Inject dependencies.
     * <p/>
     * Implement with
     * <pre>
     * diComponent.inject(this);
     * </pre>
     *
     * @param diComponent The di component that provides injections.
     */
    protected abstract void injectDependencies(DiComponent diComponent);
}
