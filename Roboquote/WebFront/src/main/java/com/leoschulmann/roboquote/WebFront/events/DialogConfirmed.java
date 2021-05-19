package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.ConfirmDialog;
import com.vaadin.flow.component.ComponentEvent;

public class DialogConfirmed extends ComponentEvent<ConfirmDialog> {
    public DialogConfirmed(ConfirmDialog confirmDialog) {
        super(confirmDialog, false);
    }
}
