package com.leoschulmann.roboquote.WebFront.ui;

import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Arrays;

public class ErrorDialog extends Dialog {

    public ErrorDialog(String... strings) {
        Icon i = VaadinIcon.WARNING.create();
        i.setColor("Red");
        i.setSize("50px");
        VerticalLayout vl = new VerticalLayout(i);
        Arrays.stream(strings).forEach(s -> vl.add(new Span(s)));
        vl.setAlignItems(FlexComponent.Alignment.CENTER);
        add(vl);
    }
}
