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
package com.github.clboettcher.bonappetit.app.ui.selectcustomer;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import org.apache.commons.lang3.StringUtils;

class FreeTextCustomerTextWatcher implements TextWatcher {

    private Button buttonFreetextConfirm;

    public FreeTextCustomerTextWatcher(Button buttonFreetextConfirm) {
        this.buttonFreetextConfirm = buttonFreetextConfirm;
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // nothing to do
    }

    public void afterTextChanged(Editable editable) {
        // nothing to do
    }

    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (StringUtils.isBlank(charSequence.toString())) {
            buttonFreetextConfirm.setEnabled(false);
        } else {
            buttonFreetextConfirm.setEnabled(true);
        }
    }
}
