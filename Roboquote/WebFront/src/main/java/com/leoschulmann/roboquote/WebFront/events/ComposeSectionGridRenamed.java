package com.leoschulmann.roboquote.WebFront.events;

import com.leoschulmann.roboquote.WebFront.ui.Compose;
import com.vaadin.flow.component.ComponentEvent;


public class ComposeSectionGridRenamed extends ComponentEvent<Compose> {

    private String name;

    public ComposeSectionGridRenamed(Compose source, String name) {
        super(source, false);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
