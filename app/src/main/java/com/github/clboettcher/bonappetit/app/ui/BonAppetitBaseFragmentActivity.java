/*
 * Copyright (c) 2016 Claudius Boettcher (pos.bonappetit@gmail.com)
 *
 * This file is part of BonAppetit. BonAppetit is an Android based
 * Point-of-Sale client-server application for small restaurants.
 *
 * BonAppetit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BonAppetit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BonAppetit.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.clboettcher.bonappetit.app.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.github.clboettcher.bonappetit.app.core.BonAppetitApplication;
import com.github.clboettcher.bonappetit.app.core.DiComponent;

/**
 * The base fragment activity provides an abstract method that reminds the programmer
 * to inject dependencies into the class since this is a manual operation when using dagger.
 */
public abstract class BonAppetitBaseFragmentActivity extends FragmentActivity {

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
