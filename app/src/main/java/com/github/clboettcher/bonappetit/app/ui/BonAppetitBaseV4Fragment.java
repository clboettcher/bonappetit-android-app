package com.github.clboettcher.bonappetit.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.core.DiComponent;

/**
 * The base fragment from support v4 that provides an abstract method that reminds the programmer
 * to inject dependencies into the class since this is a manual operation when using dagger.
 */
public abstract class BonAppetitBaseV4Fragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectDependencies(((BonAppetitApplication) getActivity().getApplication()).getDiComponent());
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
