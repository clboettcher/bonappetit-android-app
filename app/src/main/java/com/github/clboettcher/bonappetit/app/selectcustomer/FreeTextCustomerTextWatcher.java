package com.github.clboettcher.bonappetit.app.selectcustomer;

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
