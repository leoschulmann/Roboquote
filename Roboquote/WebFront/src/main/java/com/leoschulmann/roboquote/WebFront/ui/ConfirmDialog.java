package com.leoschulmann.roboquote.WebFront.ui;

import com.leoschulmann.roboquote.WebFront.events.DialogConfirmed;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.shared.Registration;


public class ConfirmDialog extends Dialog {
    public ConfirmDialog(String msg) {
        Div text = new Div(new Span(msg));
        Button ok = new Button("Confirm");
        ok.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);
        Button cancel = new Button("Cancel");
        cancel.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(text, new HorizontalLayout(ok, cancel));

        setDraggable(true);
        setModal(true);
        setResizable(false);

        ok.addClickListener(c -> {
            fireEvent(new DialogConfirmed(this));
            this.close();
        });

        cancel.addClickListener(c -> this.close());
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }

}
