package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.bits.AddSectionDialog;
import com.vaadin.flow.component.ComponentEvent;
import lombok.Getter;

@Getter
public class SectionDialogAddEmptySectionButtonClicked extends ComponentEvent<AddSectionDialog> {
    private final String name;

    public SectionDialogAddEmptySectionButtonClicked(AddSectionDialog addSectionDialog, String name) {
        super(addSectionDialog, false);
        this.name = name;
    }
}
