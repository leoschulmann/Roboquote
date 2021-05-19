package com.leoschulmann.roboquote.WebFront.ui.bits;

import com.leoschulmann.roboquote.WebFront.events.DialogConfirmed;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;


public class ConfirmDialog extends Dialog {
    public ConfirmDialog(String msg) {
        Icon i = VaadinIcon.QUESTION_CIRCLE.create();
        i.setColor("#3177eb"); //vaadin 'primary' color
        i.setSize("50px");

        Div text = new Div(new Span(msg));

        Button ok = new Button("OK");
        ok.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        ok.addClickListener(c -> {
            fireEvent(new DialogConfirmed(this));
            this.close();
        });

        Button cancel = new Button("Cancel");
        cancel.addClickListener(c -> this.close());

        VerticalLayout vl = new VerticalLayout(i, text, new HorizontalLayout(cancel, ok));
        vl.setAlignItems(FlexComponent.Alignment.CENTER);

        add(vl);

        setModal(true);
        setResizable(false);
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
