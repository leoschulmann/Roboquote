package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

public class ErrorDialog extends Dialog {

    public ErrorDialog(List<String> strings) {
        Icon i = VaadinIcon.WARNING.create();
        i.setColor("Red");
        i.setSize("50px");

        Button ok = new Button("OK");
        ok.addClickListener(c -> this.close());
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout vl = new VerticalLayout(i);
        strings.forEach(s -> vl.add(new Span(s)));
        vl.add(ok);
        vl.setAlignItems(FlexComponent.Alignment.CENTER);
        add(vl);
    }

    public ErrorDialog(String string) {
        this(List.of(string));
    }
}
