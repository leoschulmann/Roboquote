package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.AddSectionDialog;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class SectionDialogAddBundledSectionButtonClicked extends ComponentEvent<AddSectionDialog> {
    private final int id;

    public SectionDialogAddBundledSectionButtonClicked(AddSectionDialog addSectionDialog, int id) {
        super(addSectionDialog, false);
        this.id = id;
    }
}
